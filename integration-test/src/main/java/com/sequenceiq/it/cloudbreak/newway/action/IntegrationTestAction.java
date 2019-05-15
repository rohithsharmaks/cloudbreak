package com.sequenceiq.it.cloudbreak.newway.action;

import com.sequenceiq.it.cloudbreak.newway.Entity;
import com.sequenceiq.it.cloudbreak.newway.context.TestContext;
import com.sequenceiq.it.cloudbreak.newway.dto.IntegrationTestDto;

public interface IntegrationTestAction<T extends IntegrationTestDto> {

    T action(TestContext testContext, T testDto, Entity cloudbreakClient) throws Exception;

}
