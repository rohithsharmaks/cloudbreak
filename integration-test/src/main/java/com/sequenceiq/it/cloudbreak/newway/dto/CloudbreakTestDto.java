package com.sequenceiq.it.cloudbreak.newway.dto;

import com.sequenceiq.it.cloudbreak.newway.context.ServiceType;

public interface CloudbreakTestDto extends IntegrationTestDto {

    @Override
    default ServiceType type() {
        return ServiceType.CLOUDBREAK;
    }
}
