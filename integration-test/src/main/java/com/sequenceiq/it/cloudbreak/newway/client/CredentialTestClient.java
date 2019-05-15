package com.sequenceiq.it.cloudbreak.newway.client;

import org.springframework.stereotype.Service;

import com.sequenceiq.it.cloudbreak.newway.action.IntegrationTestAction;
import com.sequenceiq.it.cloudbreak.newway.action.v4.credential.CredentialCreateAction;
import com.sequenceiq.it.cloudbreak.newway.action.v4.credential.CredentialCreateIfNotExistAction;
import com.sequenceiq.it.cloudbreak.newway.action.v4.credential.CredentialDeleteAction;
import com.sequenceiq.it.cloudbreak.newway.action.v4.credential.CredentialGetAction;
import com.sequenceiq.it.cloudbreak.newway.action.v4.credential.CredentialListAction;
import com.sequenceiq.it.cloudbreak.newway.action.v4.credential.CredentialModifyAction;
import com.sequenceiq.it.cloudbreak.newway.dto.credential.CredentialTestDto;

@Service
public class CredentialTestClient {

    public IntegrationTestAction<CredentialTestDto> createV4() {
        return new CredentialCreateAction();
    }

    public IntegrationTestAction<CredentialTestDto> deleteV4() {
        return new CredentialDeleteAction();
    }

    public IntegrationTestAction<CredentialTestDto> listV4() {
        return new CredentialListAction();
    }

    public IntegrationTestAction<CredentialTestDto> createIfNotExistV4() {
        return new CredentialCreateIfNotExistAction();
    }

    public IntegrationTestAction<CredentialTestDto> modifyV4() {
        return new CredentialModifyAction();
    }

    public IntegrationTestAction<CredentialTestDto> getV4() {
        return new CredentialGetAction();
    }

}
