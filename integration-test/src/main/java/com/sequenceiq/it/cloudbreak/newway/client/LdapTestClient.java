package com.sequenceiq.it.cloudbreak.newway.client;

import org.springframework.stereotype.Service;

import com.sequenceiq.it.cloudbreak.newway.action.IntegrationTestAction;
import com.sequenceiq.it.cloudbreak.newway.action.v4.ldap.LdapCreateAction;
import com.sequenceiq.it.cloudbreak.newway.action.v4.ldap.LdapCreateIfNotExistsAction;
import com.sequenceiq.it.cloudbreak.newway.action.v4.ldap.LdapDeleteAction;
import com.sequenceiq.it.cloudbreak.newway.action.v4.ldap.LdapGetAction;
import com.sequenceiq.it.cloudbreak.newway.dto.ldap.LdapTestDto;

@Service
public class LdapTestClient {

    public IntegrationTestAction<LdapTestDto> createV4() {
        return new LdapCreateAction();
    }

    public IntegrationTestAction<LdapTestDto> createIfNotExistV4() {
        return new LdapCreateIfNotExistsAction();
    }

    public IntegrationTestAction<LdapTestDto> getV4() {
        return new LdapGetAction();
    }

    public IntegrationTestAction<LdapTestDto> deleteV4() {
        return new LdapDeleteAction();
    }

}