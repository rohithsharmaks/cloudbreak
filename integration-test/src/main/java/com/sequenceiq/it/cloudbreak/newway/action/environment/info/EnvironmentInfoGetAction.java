package com.sequenceiq.it.cloudbreak.newway.action.environment.info;

import static com.sequenceiq.it.cloudbreak.newway.log.Log.logJSON;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sequenceiq.environment.api.info.model.response.EnvironmentInfoResponse;
import com.sequenceiq.environment.client.EnvironmentClient;
import com.sequenceiq.it.cloudbreak.newway.Entity;
import com.sequenceiq.it.cloudbreak.newway.action.IntegrationTestAction;
import com.sequenceiq.it.cloudbreak.newway.context.TestContext;
import com.sequenceiq.it.cloudbreak.newway.dto.info.EnvironmentInfoTestDto;

public class EnvironmentInfoGetAction implements IntegrationTestAction<EnvironmentInfoTestDto> {

    private static final Logger LOGGER = LoggerFactory.getLogger(EnvironmentInfoGetAction.class);

    public EnvironmentInfoGetAction() {
    }

    @Override
    public EnvironmentInfoTestDto action(TestContext testContext, EnvironmentInfoTestDto testDto, Entity environmentClient) throws Exception {
        LOGGER.info("Get Info: {}", testDto.getRequest());
        try {
            EnvironmentInfoResponse info = EnvironmentInfoTestDto.environmentInfoV1Endpoint().info();
            testDto.setResponse(info);
            logJSON(LOGGER, "info has been fetched successfully: ", testDto.getRequest());
        } catch (Exception e) {
            LOGGER.warn("Cannot get info : {}", testDto.getRequest());
            throw e;
        }
        return testDto;
    }
}
