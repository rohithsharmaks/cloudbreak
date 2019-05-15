package com.sequenceiq.it.cloudbreak.newway.action.v4.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sequenceiq.it.cloudbreak.newway.CloudbreakClient;
import com.sequenceiq.it.cloudbreak.newway.action.IntegrationTestAction;
import com.sequenceiq.it.cloudbreak.newway.context.TestContext;
import com.sequenceiq.it.cloudbreak.newway.dto.util.SubscriptionTestDto;

public class SubscriptionAction implements IntegrationTestAction<SubscriptionTestDto> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubscriptionAction.class);

    @Override
    public SubscriptionTestDto action(TestContext testContext, SubscriptionTestDto testDto, CloudbreakClient cloudbreakClient) throws Exception {
        String logInitMessage = "Subscribing";
        LOGGER.info("{}", logInitMessage);
        testDto.setResponse(cloudbreakClient.getCloudbreakClient().utilV4Endpoint().subscribe(testDto.getRequest()));
        LOGGER.info("{} was successful", logInitMessage);
        return testDto;
    }
}
