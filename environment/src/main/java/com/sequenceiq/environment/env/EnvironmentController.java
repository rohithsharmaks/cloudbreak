package com.sequenceiq.environment.env;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.stereotype.Controller;

import com.sequenceiq.environment.api.environment.endpoint.EnvironmentV1Endpoint;
import com.sequenceiq.environment.api.environment.model.request.EnvironmentAttachV1Request;
import com.sequenceiq.environment.api.environment.model.request.EnvironmentChangeCredentialV1Request;
import com.sequenceiq.environment.api.environment.model.request.EnvironmentDetachV1Request;
import com.sequenceiq.environment.api.environment.model.request.EnvironmentEditV1Request;
import com.sequenceiq.environment.api.environment.model.request.EnvironmentV1Request;
import com.sequenceiq.environment.api.environment.model.response.DetailedEnvironmentV1Response;
import com.sequenceiq.environment.api.environment.model.response.SimpleEnvironmentV1Response;
import com.sequenceiq.environment.api.environment.model.response.SimpleEnvironmentV1Responses;
import com.sequenceiq.environment.env.service.EnvironmentCreationService;
import com.sequenceiq.environment.env.service.EnvironmentDto;

@Controller
public class EnvironmentController implements EnvironmentV1Endpoint {

    private final EnvDtoConverter converter;

    private final EnvironmentCreationService environmentCreationService;

    public EnvironmentController(EnvDtoConverter envDtoConverter, EnvironmentCreationService environmentCreationService) {
        this.converter = envDtoConverter;
        this.environmentCreationService = environmentCreationService;
    }

    @Override
    public DetailedEnvironmentV1Response post(@Valid EnvironmentV1Request request) {
        EnvironmentDto environmentDto = converter.requestToDto(request);
        EnvironmentDto created = environmentCreationService.createEnvironment(environmentDto);
        return converter.dtoToResponse(created);
    }

    @Override
    public DetailedEnvironmentV1Response get(String environmentName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SimpleEnvironmentV1Response delete(String environmentName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DetailedEnvironmentV1Response edit(String environmentName, @NotNull EnvironmentEditV1Request request) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SimpleEnvironmentV1Responses list() {
        throw new UnsupportedOperationException();
    }

    @Override
    public DetailedEnvironmentV1Response attach(String environmentName, @Valid EnvironmentAttachV1Request request) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DetailedEnvironmentV1Response detach(String environmentName, @Valid EnvironmentDetachV1Request request) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DetailedEnvironmentV1Response changeCredential(String environmentName, @Valid EnvironmentChangeCredentialV1Request request) {
        throw new UnsupportedOperationException();
    }
}
