package com.sequenceiq.it.cloudbreak.newway.client;

import org.springframework.stereotype.Service;

import com.sequenceiq.it.cloudbreak.newway.action.IntegrationTestAction;
import com.sequenceiq.it.cloudbreak.newway.action.v4.kerberos.KerberosCreateAction;
import com.sequenceiq.it.cloudbreak.newway.action.v4.kerberos.KerberosDeleteAction;
import com.sequenceiq.it.cloudbreak.newway.action.v4.kerberos.KerberosGetAction;
import com.sequenceiq.it.cloudbreak.newway.dto.kerberos.KerberosTestDto;

@Service
public class KerberosTestClient {

    public IntegrationTestAction<KerberosTestDto> createV4() {
        return new KerberosCreateAction();
    }

    public IntegrationTestAction<KerberosTestDto> deleteV4() {
        return new KerberosDeleteAction();
    }

    public IntegrationTestAction<KerberosTestDto> getV4() {
        return new KerberosGetAction();
    }

}