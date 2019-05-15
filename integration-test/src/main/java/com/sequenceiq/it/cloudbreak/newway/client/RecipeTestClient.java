package com.sequenceiq.it.cloudbreak.newway.client;

import org.springframework.stereotype.Service;

import com.sequenceiq.it.cloudbreak.newway.action.IntegrationTestAction;
import com.sequenceiq.it.cloudbreak.newway.action.v4.recipe.RecipeCreateAction;
import com.sequenceiq.it.cloudbreak.newway.action.v4.recipe.RecipeDeleteAction;
import com.sequenceiq.it.cloudbreak.newway.action.v4.recipe.RecipeGetAction;
import com.sequenceiq.it.cloudbreak.newway.action.v4.recipe.RecipeListAction;
import com.sequenceiq.it.cloudbreak.newway.dto.recipe.RecipeTestDto;

@Service
public class RecipeTestClient {

    public IntegrationTestAction<RecipeTestDto> createV4() {
        return new RecipeCreateAction();
    }

    public IntegrationTestAction<RecipeTestDto> getV4() {
        return new RecipeGetAction();
    }

    public IntegrationTestAction<RecipeTestDto> deleteV4() {
        return new RecipeDeleteAction();
    }

    public IntegrationTestAction<RecipeTestDto> listV4() {
        return new RecipeListAction();
    }

}
