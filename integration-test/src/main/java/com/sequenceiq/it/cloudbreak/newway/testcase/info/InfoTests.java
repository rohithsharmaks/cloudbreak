package com.sequenceiq.it.cloudbreak.newway.testcase.info;

import static com.sequenceiq.it.cloudbreak.newway.assertion.info.InfoTestAssertion.infoContainsCloudbreakProperties;
import static com.sequenceiq.it.cloudbreak.newway.assertion.info.InfoTestAssertion.infoContainsEnvironmentProperties;

import javax.inject.Inject;

import org.testng.annotations.Test;

import com.sequenceiq.it.cloudbreak.newway.client.CloudbreakInfoTestClient;
import com.sequenceiq.it.cloudbreak.newway.client.environment.EnvironmentInfoTestClient;
import com.sequenceiq.it.cloudbreak.newway.context.Description;
import com.sequenceiq.it.cloudbreak.newway.context.TestContext;
import com.sequenceiq.it.cloudbreak.newway.dto.info.CloudbreakInfoTestDto;
import com.sequenceiq.it.cloudbreak.newway.dto.info.EnvironmentInfoTestDto;
import com.sequenceiq.it.cloudbreak.newway.testcase.AbstractIntegrationTest;

public class InfoTests extends AbstractIntegrationTest {

    @Inject
    private CloudbreakInfoTestClient cloudbreakInfoTestClient;

    @Inject
    private EnvironmentInfoTestClient environmentInfoTestClient;

    @Test(dataProvider = TEST_CONTEXT_WITH_MOCK)
    @Description(
            given = "there is a running cloudbreak",
            when = "calling the info endpoint",
            then = "the service has to report the status")
    public void testCloudbreakRunning(TestContext testContext) {

        testContext
                .given(CloudbreakInfoTestDto.class)
                .then((tc, testDto, cc) -> cloudbreakInfoTestClient.get().action(tc, testDto, cc))
                .then(infoContainsCloudbreakProperties("cloudbreak"))
                .validate();
    }

    @Test(dataProvider = TEST_CONTEXT_WITH_MOCK)
    @Description(
            given = "there is a running environment",
            when = "calling the info endpoint",
            then = "the service has to report the status")
    public void testEnvironmentRunning(TestContext testContext) {

        testContext
                .given(EnvironmentInfoTestDto.class)
                .then((tc, testDto, cc) -> environmentInfoTestClient.get().action(tc, testDto, cc))
                .then(infoContainsEnvironmentProperties("environment"))
                .validate();
    }
}
