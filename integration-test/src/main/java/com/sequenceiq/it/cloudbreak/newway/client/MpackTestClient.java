package com.sequenceiq.it.cloudbreak.newway.client;

import org.springframework.stereotype.Service;

import com.sequenceiq.it.cloudbreak.newway.action.IntegrationTestAction;
import com.sequenceiq.it.cloudbreak.newway.action.v4.mpack.MpackCreateAction;
import com.sequenceiq.it.cloudbreak.newway.action.v4.mpack.MpackDeleteAction;
import com.sequenceiq.it.cloudbreak.newway.action.v4.mpack.MpackListAction;
import com.sequenceiq.it.cloudbreak.newway.dto.mpack.MPackTestDto;

@Service
public class MpackTestClient {

    public IntegrationTestAction<MPackTestDto> createV4() {
        return new MpackCreateAction();
    }

    public IntegrationTestAction<MPackTestDto> listV4() {
        return new MpackListAction();
    }

    public IntegrationTestAction<MPackTestDto> deleteV4() {
        return new MpackDeleteAction();
    }

}
