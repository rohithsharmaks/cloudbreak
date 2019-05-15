package com.sequenceiq.it.cloudbreak.newway.client;

import org.springframework.stereotype.Service;

import com.sequenceiq.it.cloudbreak.newway.action.IntegrationTestAction;
import com.sequenceiq.it.cloudbreak.newway.action.v4.connector.PlatformAccessConfigsAction;
import com.sequenceiq.it.cloudbreak.newway.action.v4.connector.PlatformDisksAction;
import com.sequenceiq.it.cloudbreak.newway.action.v4.connector.PlatformEncryptionKeysAction;
import com.sequenceiq.it.cloudbreak.newway.action.v4.connector.PlatformGatewaysAction;
import com.sequenceiq.it.cloudbreak.newway.action.v4.connector.PlatformIpPoolsAction;
import com.sequenceiq.it.cloudbreak.newway.action.v4.connector.PlatformNetworksAction;
import com.sequenceiq.it.cloudbreak.newway.action.v4.connector.PlatformRegionsAction;
import com.sequenceiq.it.cloudbreak.newway.action.v4.connector.PlatformSecurityGroupsAction;
import com.sequenceiq.it.cloudbreak.newway.action.v4.connector.PlatformSshKeysAction;
import com.sequenceiq.it.cloudbreak.newway.action.v4.connector.PlatformVmTypesAction;
import com.sequenceiq.it.cloudbreak.newway.dto.connector.PlatformAccessConfigsTestDto;
import com.sequenceiq.it.cloudbreak.newway.dto.connector.PlatformDiskTestDto;
import com.sequenceiq.it.cloudbreak.newway.dto.connector.PlatformEncryptionKeysTestDto;
import com.sequenceiq.it.cloudbreak.newway.dto.connector.PlatformGatewaysTestDto;
import com.sequenceiq.it.cloudbreak.newway.dto.connector.PlatformIpPoolsTestDto;
import com.sequenceiq.it.cloudbreak.newway.dto.connector.PlatformNetworksTestDto;
import com.sequenceiq.it.cloudbreak.newway.dto.connector.PlatformRegionTestDto;
import com.sequenceiq.it.cloudbreak.newway.dto.connector.PlatformSecurityGroupsTestDto;
import com.sequenceiq.it.cloudbreak.newway.dto.connector.PlatformSshKeysTestDto;
import com.sequenceiq.it.cloudbreak.newway.dto.connector.PlatformVmTypesTestDto;

@Service
public class ConnectorTestClient {

    public IntegrationTestAction<PlatformAccessConfigsTestDto> accessConfigs() {
        return new PlatformAccessConfigsAction();
    }

    public IntegrationTestAction<PlatformDiskTestDto> disks() {
        return new PlatformDisksAction();
    }

    public IntegrationTestAction<PlatformEncryptionKeysTestDto> encryptionKeys() {
        return new PlatformEncryptionKeysAction();
    }

    public IntegrationTestAction<PlatformIpPoolsTestDto> ipPools() {
        return new PlatformIpPoolsAction();
    }

    public IntegrationTestAction<PlatformNetworksTestDto> networks() {
        return new PlatformNetworksAction();
    }

    public IntegrationTestAction<PlatformGatewaysTestDto> gateways() {
        return new PlatformGatewaysAction();
    }

    public IntegrationTestAction<PlatformRegionTestDto> regions() {
        return new PlatformRegionsAction();
    }

    public IntegrationTestAction<PlatformSecurityGroupsTestDto> securityGroups() {
        return new PlatformSecurityGroupsAction();
    }

    public IntegrationTestAction<PlatformSshKeysTestDto> sshKeys() {
        return new PlatformSshKeysAction();
    }

    public IntegrationTestAction<PlatformVmTypesTestDto> vmTypes() {
        return new PlatformVmTypesAction();
    }

}
