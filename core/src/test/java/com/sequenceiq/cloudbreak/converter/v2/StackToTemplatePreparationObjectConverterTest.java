package com.sequenceiq.cloudbreak.converter.v2;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.sequenceiq.cloudbreak.blueprint.GeneralClusterConfigsProvider;
import com.sequenceiq.cloudbreak.blueprint.nifi.HdfConfigProvider;
import com.sequenceiq.cloudbreak.blueprint.sharedservice.SharedServiceConfigsViewProvider;
import com.sequenceiq.cloudbreak.blueprint.utils.StackInfoService;
import com.sequenceiq.cloudbreak.cloud.model.StackInputs;
import com.sequenceiq.cloudbreak.cloud.model.component.StackRepoDetails;
import com.sequenceiq.cloudbreak.cluster.service.ClusterComponentConfigProvider;
import com.sequenceiq.cloudbreak.common.json.Json;
import com.sequenceiq.cloudbreak.common.user.CloudbreakUser;
import com.sequenceiq.cloudbreak.converter.StackToTemplatePreparationObjectConverter;
import com.sequenceiq.cloudbreak.core.bootstrap.service.container.postgres.PostgresConfigService;
import com.sequenceiq.cloudbreak.domain.Blueprint;
import com.sequenceiq.cloudbreak.domain.FileSystem;
import com.sequenceiq.cloudbreak.domain.stack.Stack;
import com.sequenceiq.cloudbreak.domain.stack.cluster.Cluster;
import com.sequenceiq.cloudbreak.domain.stack.cluster.DatalakeResources;
import com.sequenceiq.cloudbreak.domain.stack.cluster.gateway.Gateway;
import com.sequenceiq.cloudbreak.domain.stack.cluster.host.HostGroup;
import com.sequenceiq.cloudbreak.domain.stack.instance.InstanceMetaData;
import com.sequenceiq.cloudbreak.dto.LdapView;
import com.sequenceiq.cloudbreak.dto.credential.Credential;
import com.sequenceiq.cloudbreak.kerberos.KerberosConfigService;
import com.sequenceiq.cloudbreak.ldap.LdapConfigService;
import com.sequenceiq.cloudbreak.service.CloudbreakServiceException;
import com.sequenceiq.cloudbreak.service.blueprint.BlueprintViewProvider;
import com.sequenceiq.cloudbreak.service.cluster.ClusterService;
import com.sequenceiq.cloudbreak.service.cluster.InstanceGroupMetadataCollector;
import com.sequenceiq.cloudbreak.service.datalake.DatalakeResourcesService;
import com.sequenceiq.cloudbreak.service.environment.credential.CredentialClientService;
import com.sequenceiq.cloudbreak.service.hostgroup.HostGroupService;
import com.sequenceiq.cloudbreak.service.stack.StackService;
import com.sequenceiq.cloudbreak.template.TemplatePreparationObject;
import com.sequenceiq.cloudbreak.template.filesystem.BaseFileSystemConfigurationsView;
import com.sequenceiq.cloudbreak.template.filesystem.FileSystemConfigurationProvider;
import com.sequenceiq.cloudbreak.template.model.BlueprintStackInfo;
import com.sequenceiq.cloudbreak.template.model.GeneralClusterConfigs;
import com.sequenceiq.cloudbreak.template.model.HdfConfigs;
import com.sequenceiq.cloudbreak.template.views.BlueprintView;
import com.sequenceiq.cloudbreak.template.views.SharedServiceConfigsView;

public class StackToTemplatePreparationObjectConverterTest {

    private static final Long TEST_CLUSTER_ID = 1L;

    private static final String TEST_BLUEPRINT_TEXT = "{}";

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @InjectMocks
    private StackToTemplatePreparationObjectConverter underTest;

    @Mock
    private HostGroupService hostGroupService;

    @Mock
    private ClusterComponentConfigProvider clusterComponentConfigProvider;

    @Mock
    private InstanceGroupMetadataCollector instanceGroupMetadataCollector;

    @Mock
    private StackInfoService stackInfoService;

    @Mock
    private HdfConfigProvider hdfConfigProvider;

    @Mock
    private PostgresConfigService postgresConfigService;

    @Mock
    private FileSystemConfigurationProvider fileSystemConfigurationProvider;

    @Mock
    private StackService stackService;

    @Mock
    private ClusterService clusterService;

    @Mock
    private GeneralClusterConfigsProvider generalClusterConfigsProvider;

    @Mock
    private SharedServiceConfigsViewProvider sharedServiceConfigProvider;

    @Mock
    private Stack stackMock;

    @Mock
    private Cluster cluster;

    @Mock
    private Cluster sourceCluster;

    @Mock
    private StackRepoDetails stackRepoDetails;

    @Mock
    private Map<String, List<InstanceMetaData>> groupInstances;

    @Mock
    private Blueprint blueprint;

    @Mock
    private CloudbreakUser user;

    @Mock
    private Json stackInputs;

    @Mock
    private BlueprintStackInfo blueprintStackInfo;

    @Mock
    private BlueprintViewProvider blueprintViewProvider;

    @Mock
    private DatalakeResourcesService datalakeResourcesService;

    @Mock
    private CredentialClientService credentialClientService;

    @Mock
    private LdapConfigService ldapConfigService;

    @Mock
    private KerberosConfigService kerberosConfigService;

    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);
        when(clusterService.getById(any(Long.class))).thenReturn(cluster);
        when(stackMock.getCluster()).thenReturn(sourceCluster);
        when(stackMock.getEnvironmentCrn()).thenReturn("env");
        when(sourceCluster.getId()).thenReturn(TEST_CLUSTER_ID);
        when(cluster.getId()).thenReturn(TEST_CLUSTER_ID);
        when(clusterComponentConfigProvider.getHDPRepo(TEST_CLUSTER_ID)).thenReturn(stackRepoDetails);
        when(instanceGroupMetadataCollector.collectMetadata(stackMock)).thenReturn(groupInstances);
        when(cluster.getBlueprint()).thenReturn(blueprint);
        when(blueprint.getBlueprintText()).thenReturn(TEST_BLUEPRINT_TEXT);
        when(stackMock.getInputs()).thenReturn(stackInputs);
        when(stackInputs.get(StackInputs.class)).thenReturn(null);
        when(stackInfoService.blueprintStackInfo(TEST_BLUEPRINT_TEXT)).thenReturn(blueprintStackInfo);
        Credential credential = Credential.builder()
                .crn("aCredentialCRN")
                .attributes(new Json(""))
                .build();
        when(credentialClientService.getByCrn(anyString())).thenReturn(credential);
        when(credentialClientService.getByEnvironmentCrn(anyString())).thenReturn(credential);
        when(credentialClientService.getByName(anyString())).thenReturn(credential);

        when(ldapConfigService.get(anyString())).thenReturn(Optional.empty());
    }

    @Test
    public void testConvertWhenClusterGivesGatewayThenNotNullShouldBeStored() {
        Gateway gateway = mock(Gateway.class);
        when(gateway.getSignKey()).thenReturn(null);
        when(cluster.getGateway()).thenReturn(gateway);

        TemplatePreparationObject result = underTest.convert(stackMock);

        assertNotNull(result.getGatewayView());
    }

    @Test
    public void testConvertWhenClusterDoesNotGivesAGatewayThenNullShouldBeStored() {
        when(cluster.getGateway()).thenReturn(null);
        TemplatePreparationObject result = underTest.convert(stackMock);
        assertNull(result.getGatewayView());
    }

    @Test
    public void testConvertWhenClusterProvidesFileSystemThenBaseFileSystemConfigurationsViewShouldBeExists() throws IOException {
        FileSystem sourceFileSystem = new FileSystem();
        FileSystem clusterServiceFileSystem = new FileSystem();
        BaseFileSystemConfigurationsView expected = mock(BaseFileSystemConfigurationsView.class);
        when(sourceCluster.getFileSystem()).thenReturn(sourceFileSystem);
        when(cluster.getFileSystem()).thenReturn(clusterServiceFileSystem);
        when(stackMock.getEnvironmentCrn()).thenReturn("envCredentialCRN");
        when(fileSystemConfigurationProvider.fileSystemConfiguration(clusterServiceFileSystem, stackMock, new Json(""))).thenReturn(expected);

        TemplatePreparationObject result = underTest.convert(stackMock);

        assertTrue(result.getFileSystemConfigurationView().isPresent());
        assertEquals(expected, result.getFileSystemConfigurationView().get());
        verify(fileSystemConfigurationProvider, times(1)).fileSystemConfiguration(clusterServiceFileSystem, stackMock, new Json(""));
    }

    @Test
    public void testConvertWhenClusterDoesNotProvidesFileSystemThenBaseFileSystemConfigurationsViewShouldBeEmpty() throws IOException {
        FileSystem clusterServiceFileSystem = new FileSystem();
        BaseFileSystemConfigurationsView expected = mock(BaseFileSystemConfigurationsView.class);
        when(sourceCluster.getFileSystem()).thenReturn(null);
        when(cluster.getFileSystem()).thenReturn(clusterServiceFileSystem);
        when(fileSystemConfigurationProvider.fileSystemConfiguration(clusterServiceFileSystem, stackMock, new Json(""))).thenReturn(expected);

        TemplatePreparationObject result = underTest.convert(stackMock);

        assertFalse(result.getFileSystemConfigurationView().isPresent());
        verify(fileSystemConfigurationProvider, times(0)).fileSystemConfiguration(clusterServiceFileSystem, stackMock, new Json(""));
    }

    @Test
    public void testConvertIfTheAttemptOfObtainingBaseFileSystemConfigurationsViewThrowsIOExceptionThenCloudbreakServiceExceptionShouldComeOutside()
            throws IOException {
        String iOExceptionMessage = "Unable to obtain BaseFileSystemConfigurationsView";
        IOException invokedException = new IOException(iOExceptionMessage);
        FileSystem sourceFileSystem = new FileSystem();
        FileSystem clusterServiceFileSystem = new FileSystem();
        when(sourceCluster.getFileSystem()).thenReturn(sourceFileSystem);
        when(cluster.getFileSystem()).thenReturn(clusterServiceFileSystem);
        when(stackMock.getEnvironmentCrn()).thenReturn("envCredentialCRN");
        when(fileSystemConfigurationProvider.fileSystemConfiguration(clusterServiceFileSystem, stackMock, new Json(""))).thenThrow(invokedException);

        expectedException.expect(CloudbreakServiceException.class);
        expectedException.expectMessage(iOExceptionMessage);
        expectedException.expectCause(is(invokedException));

        underTest.convert(stackMock);
    }

    @Test
    public void testConvertWhenClusterFromClusterServiceHasLdapConfigThenItShouldBeStored() {
        LdapView ldapView = LdapView.LdapViewBuilder.aLdapView().withProtocol("").withBindDn("admin<>").build();
        when(ldapConfigService.get(anyString())).thenReturn(Optional.of(ldapView));

        TemplatePreparationObject result = underTest.convert(stackMock);

        assertTrue(result.getLdapConfig().isPresent());
    }

    @Test
    public void testConvertWhenClusterFromClusterServiceHasNoLdapConfigThenTheOptionalShouldBeEmpty() {
        TemplatePreparationObject result = underTest.convert(stackMock);
        assertFalse(result.getLdapConfig().isPresent());
    }

    @Test
    public void testConvertWhenHdpRepoNotNullThenItsVersionShouldBeSet() {
        String hdpVersion = "2.6";
        when(clusterComponentConfigProvider.getHDPRepo(TEST_CLUSTER_ID)).thenReturn(stackRepoDetails);
        when(stackRepoDetails.getHdpVersion()).thenReturn(hdpVersion);

        TemplatePreparationObject result = underTest.convert(stackMock);

        assertTrue(result.getStackRepoDetailsHdpVersion().isPresent());
        assertEquals(hdpVersion, result.getStackRepoDetailsHdpVersion().get());
    }

    @Test
    public void testConvertWhenHdpRepoNullThenEmptyVersionShouldBeSet() {
        when(clusterComponentConfigProvider.getHDPRepo(TEST_CLUSTER_ID)).thenReturn(null);

        TemplatePreparationObject result = underTest.convert(stackMock);

        assertFalse(result.getStackRepoDetailsHdpVersion().isPresent());
    }

    @Test
    public void testConvertWhenHdfConfigProviderProvidedThenItShouldBeStored() {
        HdfConfigs expected = mock(HdfConfigs.class);
        Set<HostGroup> hostGroups = new LinkedHashSet<>();
        when(hdfConfigProvider.createHdfConfig(hostGroups, groupInstances, TEST_BLUEPRINT_TEXT)).thenReturn(expected);

        TemplatePreparationObject result = underTest.convert(stackMock);

        assertTrue(result.getHdfConfigs().isPresent());
        assertEquals(expected, result.getHdfConfigs().get());
    }

    @Test
    public void testConvertWhenHdfConfigIsNullThenOptionalShouldBeEmpty() {
        Set<HostGroup> hostGroups = new LinkedHashSet<>();
        when(hdfConfigProvider.createHdfConfig(hostGroups, groupInstances, TEST_BLUEPRINT_TEXT)).thenReturn(null);

        TemplatePreparationObject result = underTest.convert(stackMock);

        assertFalse(result.getHdfConfigs().isPresent());
    }

    @Test
    public void testConvertWhenProvidingStackAndBlueprintStackInfoThenExpectedBlueprintViewShouldBeStored() {
        String type = "HDF";
        String version = "2.6";
        BlueprintView expected = mock(BlueprintView.class);
        when(blueprintStackInfo.getType()).thenReturn(type);
        when(blueprintStackInfo.getVersion()).thenReturn(version);
        when(blueprintViewProvider.getBlueprintView(blueprint)).thenReturn(expected);

        TemplatePreparationObject result = underTest.convert(stackMock);

        assertSame(expected, result.getBlueprintView());
    }

    @Test
    public void testConvertWhenGeneralClusterConfigsProvidedThenThisShouldBeStored() {
        GeneralClusterConfigs expected = mock(GeneralClusterConfigs.class);
        when(generalClusterConfigsProvider.generalClusterConfigs(stackMock, cluster)).thenReturn(expected);

        TemplatePreparationObject result = underTest.convert(stackMock);

        assertEquals(expected, result.getGeneralClusterConfigs());
    }

    @Test
    public void testConvertWhenDataLakeIdNotNullThenExpectedSharedServiceConfigsShouldBeStored() {
        // just in case adding one to avoid matching with the class variable
        Optional<DatalakeResources> datalakeResources = Optional.of(new DatalakeResources());
        SharedServiceConfigsView expected = new SharedServiceConfigsView();
        when(sharedServiceConfigProvider.createSharedServiceConfigs(stackMock, datalakeResources)).thenReturn(expected);
        when(stackMock.getDatalakeResourceId()).thenReturn(1L);
        when(datalakeResourcesService.findById(anyLong())).thenReturn(datalakeResources);

        TemplatePreparationObject result = underTest.convert(stackMock);

        assertTrue(result.getSharedServiceConfigs().isPresent());
        assertEquals(expected, result.getSharedServiceConfigs().get());
    }

    @Test
    public void testConvertWhenStackInputsContainsCustomInputsThenThisShouldBeStored() throws IOException {
        StackInputs stackInputs = mock(StackInputs.class);
        Map<String, Object> customInputs = new LinkedHashMap<>();
        when(this.stackInputs.get(StackInputs.class)).thenReturn(stackInputs);
        when(stackInputs.getCustomInputs()).thenReturn(customInputs);

        TemplatePreparationObject result = underTest.convert(stackMock);

        assertEquals(customInputs, result.getCustomInputs());
    }

    @Test
    public void testConvertWhenStackInputsDoesNotContainsCustomInputsThenEmptyMapBeStored() throws IOException {
        StackInputs stackInputs = mock(StackInputs.class);
        when(this.stackInputs.get(StackInputs.class)).thenReturn(stackInputs);
        when(stackInputs.getCustomInputs()).thenReturn(null);

        TemplatePreparationObject result = underTest.convert(stackMock);

        assertTrue(result.getCustomInputs().isEmpty());
    }

    @Test
    public void testConvertWhenUnableToGetStackInputsThenCloudbreakServiceExceptionWouldCome() throws IOException {
        String ioExceptionMessage = "unable to get inputs";
        IOException invokedException = new IOException(ioExceptionMessage);
        when(stackInputs.get(StackInputs.class)).thenThrow(invokedException);

        expectedException.expect(CloudbreakServiceException.class);
        expectedException.expectMessage(ioExceptionMessage);
        expectedException.expectCause(is(invokedException));

        underTest.convert(stackMock);
    }

}
