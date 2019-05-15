package com.sequenceiq.it.cloudbreak.newway.action.v4.recipe;

import com.sequenceiq.it.cloudbreak.newway.CloudbreakClient;
import com.sequenceiq.it.cloudbreak.newway.action.IntegrationTestAction;
import com.sequenceiq.it.cloudbreak.newway.context.TestContext;
import com.sequenceiq.it.cloudbreak.newway.dto.recipe.RecipeTestDto;

public class RecipeListAction implements IntegrationTestAction<RecipeTestDto> {

    @Override
    public RecipeTestDto action(TestContext testContext, RecipeTestDto testDto, CloudbreakClient cloudbreakClient) throws Exception {
        testDto.setSimpleResponses(
                cloudbreakClient.getCloudbreakClient().recipeV4Endpoint().list(cloudbreakClient.getWorkspaceId())
        );
        return testDto;
    }
}
