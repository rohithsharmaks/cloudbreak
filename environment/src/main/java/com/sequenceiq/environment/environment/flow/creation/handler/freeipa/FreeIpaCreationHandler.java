package com.sequenceiq.environment.environment.flow.creation.handler.freeipa;

import static com.sequenceiq.cloudbreak.polling.PollingResult.SUCCESS;
import static com.sequenceiq.environment.environment.flow.creation.event.EnvCreationHandlerSelectors.CREATE_FREEIPA_EVENT;
import static com.sequenceiq.environment.environment.flow.creation.event.EnvCreationStateSelectors.FINISH_ENV_CREATION_EVENT;

import java.security.KeyPair;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.sequenceiq.cloudbreak.certificate.PkiUtil;
import com.sequenceiq.cloudbreak.polling.PollingResult;
import com.sequenceiq.cloudbreak.polling.PollingService;
import com.sequenceiq.cloudbreak.service.secret.model.StringToSecretResponseConverter;
import com.sequenceiq.cloudbreak.util.PasswordUtil;
import com.sequenceiq.environment.CloudPlatform;
import com.sequenceiq.environment.configuration.SupportedPlatforms;
import com.sequenceiq.environment.environment.EnvironmentStatus;
import com.sequenceiq.environment.environment.dto.EnvironmentDto;
import com.sequenceiq.environment.environment.flow.creation.event.EnvCreationEvent;
import com.sequenceiq.environment.environment.service.EnvironmentService;
import com.sequenceiq.environment.exception.FreeIpaOperationFailedException;
import com.sequenceiq.flow.reactor.api.event.EventSender;
import com.sequenceiq.flow.reactor.api.handler.EventSenderAwareHandler;
import com.sequenceiq.freeipa.api.v1.freeipa.stack.FreeIpaV1Endpoint;
import com.sequenceiq.freeipa.api.v1.freeipa.stack.model.FreeIpaServerRequest;
import com.sequenceiq.freeipa.api.v1.freeipa.stack.model.common.region.PlacementRequest;
import com.sequenceiq.freeipa.api.v1.freeipa.stack.model.common.security.StackAuthenticationRequest;
import com.sequenceiq.freeipa.api.v1.freeipa.stack.model.create.CreateFreeIpaRequest;
import com.sequenceiq.freeipa.api.v1.freeipa.stack.model.create.credential.CredentialRequest;

import reactor.bus.Event;

@Component
public class FreeIpaCreationHandler extends EventSenderAwareHandler<EnvironmentDto> {

    private static final Logger LOGGER = LoggerFactory.getLogger(FreeIpaCreationHandler.class);

    private final EnvironmentService environmentService;

    private final FreeIpaV1Endpoint freeIpaV1Endpoint;

    private final SupportedPlatforms supportedPlatforms;

    private final Map<CloudPlatform, FreeIpaNetworkProvider> freeIpaNetworkProviderMapByCloudPlatform;

    private final PollingService<FreeIpaPollerObject> freeIpaPollingService;

    private final StringToSecretResponseConverter secretConverter;

    public FreeIpaCreationHandler(
            EventSender eventSender,
            EnvironmentService environmentService,
            FreeIpaV1Endpoint freeIpaV1Endpoint,
            SupportedPlatforms supportedPlatforms,
            Map<CloudPlatform, FreeIpaNetworkProvider> freeIpaNetworkProviderMapByCloudPlatform,
            PollingService<FreeIpaPollerObject> freeIpaPollingService,
            StringToSecretResponseConverter secretConverter) {
        super(eventSender);
        this.environmentService = environmentService;
        this.freeIpaV1Endpoint = freeIpaV1Endpoint;
        this.supportedPlatforms = supportedPlatforms;
        this.freeIpaNetworkProviderMapByCloudPlatform = freeIpaNetworkProviderMapByCloudPlatform;
        this.freeIpaPollingService = freeIpaPollingService;
        this.secretConverter = secretConverter;
    }

    @Override
    public void accept(Event<EnvironmentDto> environmentDtoEvent) {
        EnvironmentDto environmentDto = environmentDtoEvent.getData();
        Optional<EnvironmentDto> environmentOptional = environmentService.findById(environmentDto.getId());

        try {
            if (environmentOptional.isPresent()) {
                EnvironmentDto environment = environmentOptional.get();
                if (supportedPlatforms.supportedPlatformForFreeIpa(environment.getCloudPlatform())) {
                    environment.setStatus(EnvironmentStatus.FREEIPA_CREATION_IN_PROGRESS);
                    environmentService.save(environment);
                    CreateFreeIpaRequest createFreeIpaRequest = createFreeIpaRequest(environment);
                    freeIpaV1Endpoint.create(createFreeIpaRequest);
                    awaitFreeIpaCreation(environmentDtoEvent, environment);
                } else {
                    eventSender().sendEvent(getNextStepObject(environment), environmentDtoEvent.getHeaders());
                }
            } else {
                LOGGER.warn("FreeIpa preparation failed. No environment was found with ID: {}", environmentDto.getId());
                throw new FreeIpaOperationFailedException("Environment was not found!");
            }
        } catch (Exception ex) {
            throw new FreeIpaOperationFailedException("Failed to prepare FreeIpa!", ex);
        }
    }

    private CreateFreeIpaRequest createFreeIpaRequest(EnvironmentDto environment) {
        CreateFreeIpaRequest createFreeIpaRequest = initFreeIpaRequest(environment);
        setCredential(environment, createFreeIpaRequest);
        setFreeIpaServer(createFreeIpaRequest);
        setPlacementAndNetwork(environment, createFreeIpaRequest);
        setAuthentication(createFreeIpaRequest);
        return createFreeIpaRequest;
    }

    private CreateFreeIpaRequest initFreeIpaRequest(EnvironmentDto environment) {
        CreateFreeIpaRequest createFreeIpaRequest = new CreateFreeIpaRequest();
        createFreeIpaRequest.setEnvironmentCrn(environment.getResourceCrn());
        createFreeIpaRequest.setName(environment.getName() + "-freeipa");
        return createFreeIpaRequest;
    }

    private void setCredential(EnvironmentDto environment, CreateFreeIpaRequest createFreeIpaRequest) {
        CredentialRequest credentialRequest = new CredentialRequest();
        credentialRequest.setCloudPlatform(environment.getCloudPlatform());
        credentialRequest.setName(environment.getCredential().getName());
        credentialRequest.setSecret(secretConverter.convert(environment.getCredential().getAttributesSecret()));
        createFreeIpaRequest.setEnvironmentCrn(environment.getResourceCrn());
    }

    private void setFreeIpaServer(CreateFreeIpaRequest createFreeIpaRequest) {
        FreeIpaServerRequest freeIpaServerRequest = new FreeIpaServerRequest();
        freeIpaServerRequest.setAdminPassword(PasswordUtil.generatePassword());
        freeIpaServerRequest.setDomain("cdp.site");
        freeIpaServerRequest.setHostname("ipaserver");
        createFreeIpaRequest.setFreeIpa(freeIpaServerRequest);
    }

    private void setPlacementAndNetwork(EnvironmentDto environment, CreateFreeIpaRequest createFreeIpaRequest) {
        FreeIpaNetworkProvider freeIpaNetworkProvider = freeIpaNetworkProviderMapByCloudPlatform
                .get(CloudPlatform.valueOf(environment.getCloudPlatform()));
        PlacementRequest placementRequest = new PlacementRequest();
        placementRequest.setRegion(environment.getRegionSet().iterator().next().getName());
        createFreeIpaRequest.setPlacement(placementRequest);
        createFreeIpaRequest.setNetwork(
                freeIpaNetworkProvider.provider(environment));
        placementRequest.setAvailabilityZone(
                freeIpaNetworkProvider.availabilityZone(createFreeIpaRequest.getNetwork(), environment));
    }

    private void setAuthentication(CreateFreeIpaRequest createFreeIpaRequest) {
        StackAuthenticationRequest stackAuthenticationRequest = new StackAuthenticationRequest();
        stackAuthenticationRequest.setLoginUserName("cloudbreak");
        KeyPair keyPair = PkiUtil.generateKeypair();
        stackAuthenticationRequest.setPublicKey(PkiUtil.convertOpenSshPublicKey(keyPair.getPublic()));
        createFreeIpaRequest.setAuthentication(stackAuthenticationRequest);
    }

    private void awaitFreeIpaCreation(Event<EnvironmentDto> environmentDtoEvent, EnvironmentDto environment) {
        PollingResult result = freeIpaPollingService.pollWithTimeoutSingleFailure(
                new FreeIpaCreationRetrievalTask(),
                new FreeIpaPollerObject(environment.getResourceCrn(), freeIpaV1Endpoint),
                FreeIpaCreationRetrievalTask.FREEIPA_RETRYING_INTERVAL,
                FreeIpaCreationRetrievalTask.FREEIPA_RETRYING_COUNT);
        if (result == SUCCESS) {
            eventSender().sendEvent(getNextStepObject(environment), environmentDtoEvent.getHeaders());
        } else {
            throw new FreeIpaOperationFailedException("Failed to prepare FreeIpa!");
        }
    }

    private EnvCreationEvent getNextStepObject(EnvironmentDto environmentDto) {
        return EnvCreationEvent.EnvCreationEventBuilder.anEnvCreationEvent()
                .withResourceId(environmentDto.getResourceId())
                .withSelector(FINISH_ENV_CREATION_EVENT.selector())
                .build();
    }

    @Override
    public String selector() {
        return CREATE_FREEIPA_EVENT.selector();
    }
}
