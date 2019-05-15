package com.sequenceiq.it.cloudbreak.newway.client.environment;

import org.springframework.stereotype.Service;

import com.sequenceiq.it.cloudbreak.newway.action.environment.info.EnvironmentInfoGetAction;
import com.sequenceiq.it.cloudbreak.newway.dto.info.EnvironmentInfoTestDto;

@Service
public class EnvironmentInfoTestClient {

    public EnvironmentInfoGetAction<EnvironmentInfoTestDto> get() {
        return new EnvironmentInfoGetAction();
    }
}
