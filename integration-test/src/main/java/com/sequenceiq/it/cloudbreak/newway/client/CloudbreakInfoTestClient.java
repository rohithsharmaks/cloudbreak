package com.sequenceiq.it.cloudbreak.newway.client;

import org.springframework.stereotype.Service;

import com.sequenceiq.it.cloudbreak.newway.action.IntegrationTestAction;
import com.sequenceiq.it.cloudbreak.newway.action.v4.info.CloudbreakInfoGetAction;
import com.sequenceiq.it.cloudbreak.newway.dto.info.CloudbreakInfoTestDto;

@Service
public class CloudbreakInfoTestClient {

    public IntegrationTestAction<CloudbreakInfoTestDto> get() {
        return new CloudbreakInfoGetAction();
    }

}
