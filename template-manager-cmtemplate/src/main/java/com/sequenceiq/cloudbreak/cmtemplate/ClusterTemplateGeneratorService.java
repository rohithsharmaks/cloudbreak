package com.sequenceiq.cloudbreak.cmtemplate;

import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;

import org.springframework.stereotype.Service;

import com.sequenceiq.cloudbreak.cmtemplate.generator.dependencies.ServiceDependencyMatrixService;
import com.sequenceiq.cloudbreak.cmtemplate.generator.dependencies.domain.ServiceDependencyMatrix;
import com.sequenceiq.cloudbreak.cmtemplate.generator.support.SupportedVersionService;
import com.sequenceiq.cloudbreak.cmtemplate.generator.support.domain.SupportedVersions;
import com.sequenceiq.cloudbreak.cmtemplate.generator.template.GeneratedClusterTemplateService;
import com.sequenceiq.cloudbreak.cmtemplate.generator.template.domain.GeneratedClusterTemplate;

@Service
public class ClusterTemplateGeneratorService {

    @Inject
    private SupportedVersionService supportedVersionService;

    @Inject
    private ServiceDependencyMatrixService serviceDependencyMatrixService;

    @Inject
    private GeneratedClusterTemplateService generatedClusterTemplateService;

    public GeneratedClusterTemplate generateTemplateByServices(Set<String> services, String platform) {
        String generatedId = UUID.randomUUID().toString();
        String[] stackTypeAndVersionArray = getStackTypeAndVersion(platform);

        return generatedClusterTemplateService.prepareClusterTemplate(
                services,
                stackTypeAndVersionArray[0],
                stackTypeAndVersionArray[1],
                generatedId);
    }

    public ServiceDependencyMatrix getServicesAndDependencies(Set<String> services, String platform) {
        String[] stackTypeAndVersionArray = getStackTypeAndVersion(platform);
        return serviceDependencyMatrixService.collectServiceDependencyMatrix(
                services,
                stackTypeAndVersionArray[0],
                stackTypeAndVersionArray[1]);
    }

    private String[] getStackTypeAndVersion(String platform) {
        String[] stackTypeAndVersionArray = platform.split("-");
        if (stackTypeAndVersionArray.length != 2) {
            throw new BadRequestException("The request does not contain stack type and version or it does not match for the required pattern eg CDH-6.1.");
        }
        return stackTypeAndVersionArray;
    }

    public SupportedVersions getVersionsAndSupportedServiceList() {
        return supportedVersionService.collectSupportedVersions();
    }

}
