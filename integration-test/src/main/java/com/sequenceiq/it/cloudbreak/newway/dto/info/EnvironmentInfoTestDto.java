package com.sequenceiq.it.cloudbreak.newway.dto.info;

import com.sequenceiq.environment.api.info.model.response.EnvironmentInfoResponse;
import com.sequenceiq.it.cloudbreak.newway.Prototype;
import com.sequenceiq.it.cloudbreak.newway.context.TestContext;
import com.sequenceiq.it.cloudbreak.newway.dto.AbstractCloudbreakTestDto;

@Prototype
public class EnvironmentInfoTestDto extends AbstractCloudbreakTestDto<InfoRequest, EnvironmentInfoResponse, EnvironmentInfoTestDto> {

    public static final String INFO = "INFO";

    EnvironmentInfoTestDto(String newId) {
        super(newId);
        setRequest(new InfoRequest());
    }

    EnvironmentInfoTestDto() {
        this(INFO);
    }

    public EnvironmentInfoTestDto(TestContext testContext) {
        super(new InfoRequest(), testContext);
    }

    public EnvironmentInfoTestDto valid() {
        return this;
    }
}
