package com.sequenceiq.cloudbreak.core.cluster;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import com.sequenceiq.cloudbreak.api.endpoint.v4.common.StackType;
import com.sequenceiq.cloudbreak.blueprint.utils.BlueprintUtils;
import com.sequenceiq.cloudbreak.cloud.model.Telemetry;
import com.sequenceiq.cloudbreak.cluster.api.ClusterApi;
import com.sequenceiq.cloudbreak.common.service.TransactionService;
import com.sequenceiq.cloudbreak.common.service.TransactionService.TransactionExecutionException;
import com.sequenceiq.cloudbreak.domain.stack.Stack;
import com.sequenceiq.cloudbreak.domain.stack.cluster.Cluster;
import com.sequenceiq.cloudbreak.domain.stack.cluster.DatalakeResources;
import com.sequenceiq.cloudbreak.domain.stack.cluster.host.HostGroup;
import com.sequenceiq.cloudbreak.domain.stack.cluster.host.HostMetadata;
import com.sequenceiq.cloudbreak.domain.stack.instance.InstanceMetaData;
import com.sequenceiq.cloudbreak.dto.KerberosConfig;
import com.sequenceiq.cloudbreak.kerberos.KerberosConfigService;
import com.sequenceiq.cloudbreak.service.CloudbreakException;
import com.sequenceiq.cloudbreak.service.ComponentConfigProviderService;
import com.sequenceiq.cloudbreak.service.cluster.ClusterApiConnectors;
import com.sequenceiq.cloudbreak.service.cluster.ClusterCreationSuccessHandler;
import com.sequenceiq.cloudbreak.service.cluster.ClusterService;
import com.sequenceiq.cloudbreak.service.cluster.flow.recipe.RecipeEngine;
import com.sequenceiq.cloudbreak.service.datalake.DatalakeResourcesService;
import com.sequenceiq.cloudbreak.service.hostgroup.HostGroupService;
import com.sequenceiq.cloudbreak.service.hostmetadata.HostMetadataService;
import com.sequenceiq.cloudbreak.service.sharedservice.AmbariDatalakeConfigProvider;
import com.sequenceiq.cloudbreak.service.sharedservice.ClouderaManagerDatalakeConfigProvider;
import com.sequenceiq.cloudbreak.service.stack.InstanceMetaDataService;
import com.sequenceiq.cloudbreak.service.stack.StackService;
import com.sequenceiq.cloudbreak.template.TemplatePreparationObject;

@Service
public class ClusterBuilderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClusterBuilderService.class);

    @Autowired
    @Qualifier("conversionService")
    private ConversionService conversionService;

    @Inject
    private StackService stackService;

    @Inject
    private ClusterService clusterService;

    @Inject
    private ClusterApiConnectors clusterApiConnectors;

    @Inject
    private AmbariDatalakeConfigProvider ambariDatalakeConfigProvider;

    @Inject
    private ClouderaManagerDatalakeConfigProvider clouderaManagerDatalakeConfigProvider;

    @Inject
    private TransactionService transactionService;

    @Inject
    private ClusterCreationSuccessHandler clusterCreationSuccessHandler;

    @Inject
    private HostGroupService hostGroupService;

    @Inject
    private ComponentConfigProviderService componentConfigProviderService;

    @Inject
    private InstanceMetaDataService instanceMetaDataService;

    @Inject
    private RecipeEngine recipeEngine;

    @Inject
    private HostMetadataService hostMetadataService;

    @Inject
    private BlueprintUtils blueprintUtils;

    @Inject
    private DatalakeResourcesService datalakeResourcesService;

    @Inject
    private KerberosConfigService kerberosConfigService;

    public void startCluster(Long stackId) throws CloudbreakException {
        Stack stack = stackService.getByIdWithTransaction(stackId);
        ClusterApi connector = clusterApiConnectors.getConnector(stack);
        connector.waitForServer(stack);
        connector.changeOriginalCredentialsAndCreateCloudbreakUser();
    }

    public void buildCluster(Long stackId) throws CloudbreakException {
        Stack stack = stackService.getByIdWithListsInTransaction(stackId);
        ClusterApi connector = clusterApiConnectors.getConnector(stack);
        Set<HostGroup> hostGroups = hostGroupService.getByCluster(stack.getCluster().getId());
        Cluster cluster = stack.getCluster();
        clusterService.updateCreationDateOnCluster(cluster);
        TemplatePreparationObject templatePreparationObject = conversionService.convert(stack, TemplatePreparationObject.class);
        Set<HostMetadata> hostsInCluster = hostMetadataService.findHostsInCluster(cluster.getId());
        Map<HostGroup, List<InstanceMetaData>> instanceMetaDataByHostGroup = loadInstanceMetadataForHostGroups(hostGroups);
        recipeEngine.executePostAmbariStartRecipes(stack, instanceMetaDataByHostGroup.keySet());
        String blueprintText = cluster.getBlueprint().getBlueprintText();
        cluster.setExtendedBlueprintText(blueprintText);
        clusterService.updateCluster(cluster);
        final Telemetry telemetry = componentConfigProviderService.getTelemetry(stackId);


        Set<DatalakeResources> datalakeResources = datalakeResourcesService
                .findDatalakeResourcesByWorkspaceAndEnvironment(stack.getWorkspace().getId(), stack.getEnvironmentCrn());
        String sdxContext = Optional.ofNullable(datalakeResources)
                .map(Set::stream).flatMap(Stream::findFirst)
                .map(DatalakeResources::getDatalakeStackId)
                .map(stackService::getByIdWithListsInTransaction)
                .map(clusterApiConnectors::getConnector)
                .map(ClusterApi::getSdxContext).orElse(null);

        // TODO kerberost nyald fel itt
        KerberosConfig kerberosConfig = kerberosConfigService.get(stack.getEnvironmentCrn()).orElse(null);
        clusterService.save(connector.clusterSetupService().buildCluster(
                instanceMetaDataByHostGroup, templatePreparationObject, hostsInCluster, sdxContext, telemetry, kerberosConfig));
        recipeEngine.executePostInstallRecipes(stack, instanceMetaDataByHostGroup.keySet());
        clusterCreationSuccessHandler.handleClusterCreationSuccess(stack);
        if (StackType.DATALAKE == stack.getType()) {
            try {
                transactionService.required(() -> {
                    Stack stackInTransaction = stackService.getByIdWithListsInTransaction(stackId);
                    if (blueprintUtils.isAmbariBlueprint(blueprintText)) {
                        ambariDatalakeConfigProvider.collectAndStoreDatalakeResources(stackInTransaction);
                    } else {
                        clouderaManagerDatalakeConfigProvider.collectAndStoreDatalakeResources(stackInTransaction);
                    }
                    return null;
                });
            } catch (TransactionExecutionException e) {
                LOGGER.info("Couldn't collect Datalake paramaters", e);
            }
        }
    }

    private Map<HostGroup, List<InstanceMetaData>> loadInstanceMetadataForHostGroups(Iterable<HostGroup> hostGroups) {
        Map<HostGroup, List<InstanceMetaData>> instanceMetaDataByHostGroup = new HashMap<>();
        for (HostGroup hostGroup : hostGroups) {
            Long instanceGroupId = hostGroup.getConstraint().getInstanceGroup().getId();
            List<InstanceMetaData> metas = instanceMetaDataService.findAliveInstancesInInstanceGroup(instanceGroupId);
            instanceMetaDataByHostGroup.put(hostGroup, metas);
        }
        return instanceMetaDataByHostGroup;
    }

}
