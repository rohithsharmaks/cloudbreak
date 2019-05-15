package com.sequenceiq.environment.info.controller;

import javax.inject.Inject;

import org.springframework.boot.actuate.info.InfoEndpoint;
import org.springframework.stereotype.Controller;

import com.sequenceiq.environment.api.info.endpoint.EnvironmentInfoV1Endpoint;
import com.sequenceiq.environment.api.info.model.response.EnvironmentInfoResponse;

@Controller
public class CloudbreakInfoV4Controller implements EnvironmentInfoV1Endpoint {

    @Inject
    private InfoEndpoint infoEndpoint;

    @Override
    public EnvironmentInfoResponse info() {
        EnvironmentInfoResponse environmentInfoResponse = new EnvironmentInfoResponse();
        environmentInfoResponse.setInfo(infoEndpoint.info());
        return environmentInfoResponse;
    }
}
