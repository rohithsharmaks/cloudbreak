package com.sequenceiq.it.cloudbreak.newway.action.v4.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sequenceiq.it.cloudbreak.newway.CloudbreakClient;
import com.sequenceiq.it.cloudbreak.newway.action.IntegrationTestAction;
import com.sequenceiq.it.cloudbreak.newway.context.TestContext;
import com.sequenceiq.it.cloudbreak.newway.dto.util.DeploymentPreferencesTestDto;

public class DeploymentPreferencesAction implements IntegrationTestAction<DeploymentPreferencesTestDto> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeploymentPreferencesAction.class);

    @Override
    public DeploymentPreferencesTestDto action(TestContext testContext, DeploymentPreferencesTestDto testDto, CloudbreakClient client) throws Exception {
        String logInitMessage = "Obtaining deployment";
        LOGGER.info("{}", logInitMessage);
        testDto.setResponse(client.getCloudbreakClient().utilV4Endpoint().deployment());
        LOGGER.info("{} was successful", logInitMessage);
        return testDto;
    }
}
