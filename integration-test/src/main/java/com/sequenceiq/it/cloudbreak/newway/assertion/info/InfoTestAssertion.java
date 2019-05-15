package com.sequenceiq.it.cloudbreak.newway.assertion.info;

import java.util.Map;

import com.sequenceiq.it.cloudbreak.newway.assertion.AssertionV2;
import com.sequenceiq.it.cloudbreak.newway.dto.info.CloudbreakInfoTestDto;
import com.sequenceiq.it.cloudbreak.newway.dto.info.EnvironmentInfoTestDto;

public class InfoTestAssertion {

    private static final String APP = "app";

    private static final String NAME = "name";

    private static final String VERSION = "version";

    private InfoTestAssertion() {
    }

    public static AssertionV2<CloudbreakInfoTestDto> infoContainsCloudbreakProperties(String name) {
        return (testContext, testDto, cloudbreakClient) -> {
            hasInfoVersion(testDto.getResponse().getInfo());
            hasAppName(testDto.getResponse().getInfo(), name);
            return testDto;
        };
    }

    public static AssertionV2<EnvironmentInfoTestDto> infoContainsEnvironmentProperties(String name) {
        return (testContext, testDto, cloudbreakClient) -> {
            hasInfoVersion(testDto.getResponse().getInfo());
            hasAppName(testDto.getResponse().getInfo(), name);
            return testDto;
        };
    }

    private static void hasInfoVersion(Map<String, Object> response) {
        if (response.get(APP) != null) {
            Map<String, Object> app = (Map<String, Object>) response.get(APP);
            if (app.get(VERSION) == null || "".equals(app.get(VERSION))) {
                throw new IllegalArgumentException(String.format("The Service version is null or empty."));
            }
        }
    }

    private static void hasAppName(Map<String, Object> response, String name) {
        if (response.get(APP) != null) {
            Map<String, Object> app = (Map<String, Object>) response.get(APP);
            if (!name.equals(app.get(NAME))) {
                throw new IllegalArgumentException(String.format("The Service name is not equal with %s.", name));
            }
        }
    }
}
