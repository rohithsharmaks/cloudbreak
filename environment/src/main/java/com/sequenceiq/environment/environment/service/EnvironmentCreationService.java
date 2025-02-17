package com.sequenceiq.environment.environment.service;

import static com.sequenceiq.environment.environment.flow.creation.event.EnvCreationStateSelectors.START_NETWORK_CREATION_EVENT;

import java.util.Map;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.ws.rs.BadRequestException;

import org.springframework.stereotype.Service;

import com.sequenceiq.cloudbreak.auth.ThreadBasedUserCrnProvider;
import com.sequenceiq.cloudbreak.auth.altus.Crn;
import com.sequenceiq.cloudbreak.cloud.model.CloudRegions;
import com.sequenceiq.cloudbreak.util.ValidationResult;
import com.sequenceiq.environment.CloudPlatform;
import com.sequenceiq.environment.credential.domain.Credential;
import com.sequenceiq.environment.environment.domain.Environment;
import com.sequenceiq.environment.environment.dto.EnvironmentCreationDto;
import com.sequenceiq.environment.environment.dto.EnvironmentDto;
import com.sequenceiq.environment.environment.dto.EnvironmentDtoConverter;
import com.sequenceiq.environment.environment.flow.creation.event.EnvCreationEvent;
import com.sequenceiq.environment.environment.validation.EnvironmentValidatorService;
import com.sequenceiq.flow.core.FlowConstants;
import com.sequenceiq.flow.reactor.api.event.EventSender;

import reactor.bus.Event;

@Service
public class EnvironmentCreationService {

    private final EnvironmentService environmentService;

    private final EnvironmentValidatorService validatorService;

    private final EnvironmentResourceService environmentResourceService;

    private final EventSender eventSender;

    private final EnvironmentDtoConverter environmentDtoConverter;

    private final ThreadBasedUserCrnProvider authenticatedUserService;

    public EnvironmentCreationService(
            EnvironmentService environmentService,
            EnvironmentValidatorService validatorService,
            EnvironmentResourceService environmentResourceService,
            EventSender eventSender,
            EnvironmentDtoConverter environmentDtoConverter,
            ThreadBasedUserCrnProvider authenticatedUserService) {
        this.environmentService = environmentService;
        this.validatorService = validatorService;
        this.environmentResourceService = environmentResourceService;
        this.eventSender = eventSender;
        this.environmentDtoConverter = environmentDtoConverter;
        this.authenticatedUserService = authenticatedUserService;
    }

    // TODO: trigger creation properly
    public void triggerCreationFlow(long envId, String envName, String accountId) {
        EnvCreationEvent envCreationEvent = EnvCreationEvent.EnvCreationEventBuilder.anEnvCreationEvent()
                .withSelector(START_NETWORK_CREATION_EVENT.selector())
                .withResourceId(envId)
                .withResourceName(envName)
                .build();
        String userCrn = authenticatedUserService.getUserCrn();
        if (userCrn == null) {
            userCrn = environmentService.getByNameAndAccountId(envName, accountId).getCreator();
        }
        Map<String, Object> flowTriggerUsercrn = Map.of(FlowConstants.FLOW_TRIGGER_USERCRN, userCrn);
        eventSender.sendEvent(envCreationEvent, new Event.Headers(flowTriggerUsercrn));
    }

    public EnvironmentDto create(EnvironmentCreationDto creationDto, String accountId, String creator) {
        if (environmentService.isNameOccupied(creationDto.getName(), creationDto.getAccountId())) {
            throw new BadRequestException(String.format("Environment with name '%s' already exists in account '%s'.",
                    creationDto.getName(), creationDto.getAccountId()));
        }
        Environment environment = initializeEnvironment(creationDto, creator);
        CloudRegions cloudRegions = setLocationAndRegions(creationDto, environment);
        validateCreation(creationDto, environment, cloudRegions);
        environment = environmentService.save(environment);
        if (!CloudPlatform.YARN.name().equals(environment.getCloudPlatform())) {
            environmentResourceService.createAndSetNetwork(environment, creationDto.getNetwork(), accountId);
            environmentService.save(environment);
        }
        return environmentDtoConverter.environmentToDto(environment);
    }

    private Environment initializeEnvironment(EnvironmentCreationDto creationDto, String creator) {
        Environment environment = environmentDtoConverter.creationDtoToEnvironment(creationDto);
        environment.setResourceCrn(createCrn(creationDto.getAccountId()));
        environment.setCreator(creator);
        Credential credential = environmentResourceService
                .getCredentialFromRequest(creationDto.getCredential(), creationDto.getAccountId(), creator);
        environment.setCredential(credential);
        environment.setCloudPlatform(credential.getCloudPlatform());
        return environment;
    }

    private CloudRegions setLocationAndRegions(EnvironmentCreationDto creationDto, Environment environment) {
        CloudRegions cloudRegions = environmentService.getRegionsByEnvironment(environment);
        environmentService.setLocation(environment, creationDto.getLocation(), cloudRegions);
        if (cloudRegions.areRegionsSupported()) {
            environmentService.setRegions(environment, creationDto.getRegions(), cloudRegions);
        }
        return cloudRegions;
    }

    private void validateCreation(EnvironmentCreationDto creationDto, Environment environment, CloudRegions cloudRegions) {
        ValidationResult validationResult = validatorService.validateCreation(environment, creationDto, cloudRegions);
        if (validationResult.hasError()) {
            throw new BadRequestException(validationResult.getFormattedErrors());
        }
    }

    private String createCrn(@Nonnull String accountId) {
        return Crn.builder()
                .setService(Crn.Service.ENVIRONMENTS)
                .setAccountId(accountId)
                .setResourceType(Crn.ResourceType.ENVIRONMENT)
                .setResource(UUID.randomUUID().toString())
                .build()
                .toString();
    }

}
