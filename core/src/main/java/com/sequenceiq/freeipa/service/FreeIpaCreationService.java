package com.sequenceiq.freeipa.service;

import static com.sequenceiq.cloudbreak.util.Benchmark.measure;
import static com.sequenceiq.cloudbreak.util.SqlUtil.getProperSqlErrorMessage;

import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.base.InstanceStatus;
import com.sequenceiq.cloudbreak.api.freeipa.FreeIpaRequest;
import com.sequenceiq.cloudbreak.api.freeipa.FreeIpaResponse;
import com.sequenceiq.cloudbreak.cloud.event.validation.ParametersValidationRequest;
import com.sequenceiq.cloudbreak.cloud.model.CloudCredential;
import com.sequenceiq.cloudbreak.common.model.user.CloudbreakUser;
import com.sequenceiq.cloudbreak.common.type.APIResourceType;
import com.sequenceiq.cloudbreak.controller.exception.BadRequestException;
import com.sequenceiq.cloudbreak.controller.validation.ParametersValidator;
import com.sequenceiq.cloudbreak.controller.validation.ValidationResult;
import com.sequenceiq.cloudbreak.controller.validation.Validator;
import com.sequenceiq.cloudbreak.controller.validation.template.TemplateValidator;
import com.sequenceiq.cloudbreak.converter.spi.CredentialToCloudCredentialConverter;
import com.sequenceiq.cloudbreak.core.CloudbreakImageCatalogException;
import com.sequenceiq.cloudbreak.core.CloudbreakImageNotFoundException;
import com.sequenceiq.cloudbreak.core.flow2.service.ReactorFlowManager;
import com.sequenceiq.cloudbreak.domain.stack.Stack;
import com.sequenceiq.cloudbreak.domain.stack.instance.InstanceGroup;
import com.sequenceiq.cloudbreak.domain.stack.instance.InstanceMetaData;
import com.sequenceiq.cloudbreak.domain.workspace.User;
import com.sequenceiq.cloudbreak.domain.workspace.Workspace;
import com.sequenceiq.cloudbreak.logger.MDCBuilder;
import com.sequenceiq.cloudbreak.service.StackUnderOperationService;
import com.sequenceiq.cloudbreak.service.TransactionService;
import com.sequenceiq.cloudbreak.service.TransactionService.TransactionExecutionException;
import com.sequenceiq.cloudbreak.service.TransactionService.TransactionRuntimeExecutionException;
import com.sequenceiq.cloudbreak.service.image.ImageService;
import com.sequenceiq.cloudbreak.service.image.StatedImage;
import com.sequenceiq.cloudbreak.service.stack.StackService;
import com.sequenceiq.freeipa.converter.FreeIpaRequestToStackConverter;
import com.sequenceiq.freeipa.converter.StackToFreeIpaResponseConverter;
import com.sequenceiq.freeipa.decorator.FreeIpaStackDecorator;

@Service
public class FreeIpaCreationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FreeIpaCreationService.class);

//    @Inject
    private Validator<FreeIpaRequest> freeIpaRequestValidator;

    @Inject
    private FreeIpaRequestToStackConverter freeIpaRequestToStackConverter;

    @Inject
    private StackToFreeIpaResponseConverter stackToFreeIpaResponseConverter;

    @Inject
    private StackService stackService;

    @Inject
    private ExecutorService executorService;

    @Inject
    private ImageService imageService;

    @Inject
    private FreeIpaStackDecorator stackDecorator;

    @Inject
    private TransactionService transactionService;

    @Inject
    private CredentialToCloudCredentialConverter credentialToCloudCredentialConverter;

    @Inject
    private ParametersValidator parametersValidator;

    @Inject
    private TemplateValidator templateValidator;

    @Inject
    private StackUnderOperationService stackUnderOperationService;

    @Inject
    private ReactorFlowManager flowManager;

    public FreeIpaResponse createFreeIpa(FreeIpaRequest request, CloudbreakUser cloudbreakUser, User user, Workspace workspace) {

        ValidationResult validationResult = freeIpaRequestValidator.validate(request);
        if (validationResult.getState() == ValidationResult.State.ERROR) {
            LOGGER.debug("Stack request has validation error(s): {}.", validationResult.getFormattedErrors());
            throw new BadRequestException(validationResult.getFormattedErrors());
        }

        ensureStackDoesNotExists(request.getName(), workspace);

        Stack stackStub = freeIpaRequestToStackConverter.convert(request);
        stackStub.setWorkspace(workspace);
        stackStub.setCreator(user);

        MDCBuilder.buildMdcContext(stackStub);

        String platformString = stackStub.getCloudPlatform().toLowerCase();
        Future<StatedImage> imgFromCatalogFuture = determineImageCatalog(request.getName(), platformString, request, user, workspace);

        Stack savedStack;
        try {
            savedStack = transactionService.required(() -> {
                Stack stack = stackDecorator.decorate(stackStub, request, user, workspace);

                CloudCredential cloudCredential = credentialToCloudCredentialConverter.convert(stackStub.getCredential());

                ParametersValidationRequest parametersValidationRequest = parametersValidator.triggerValidate(request.getCloudPlatform().name(),
                        cloudCredential, stack.getParameters(), stack.getCreator().getUserId(), stack.getWorkspace().getId());

                if (stack.getOrchestrator() != null && stack.getOrchestrator().getApiEndpoint() != null) {
                    stackService.validateOrchestrator(stack.getOrchestrator());
                }

                measure(() -> {
                    for (InstanceGroup instanceGroup : stack.getInstanceGroups()) {
                        templateValidator.validateTemplateRequest(stack.getCredential(), instanceGroup.getTemplate(), stack.getRegion(),
                                stack.getAvailabilityZone(), stack.getPlatformVariant());
                    }
                }, LOGGER, "Stack's instance templates have been validated in {} ms for stack {}", request.getName());

                fillInstanceMetadata(stack);

                parametersValidator.waitResult(parametersValidationRequest);

                StatedImage imgFromCatalog = getImageCatalog(imgFromCatalogFuture);

                return stackService.create(stack, platformString, imgFromCatalog, user, workspace);
            });
        } catch (TransactionExecutionException e) {
            stackUnderOperationService.off();
            if (e.getCause() instanceof DataIntegrityViolationException) {
                String msg = String.format("Error with resource [%s], error: [%s]", APIResourceType.STACK,
                        getProperSqlErrorMessage((DataIntegrityViolationException) e.getCause()));
                throw new BadRequestException(msg);
            }
            throw new TransactionRuntimeExecutionException(e);
        }

        flowManager.triggerProvisioning(savedStack.getId());

        return stackToFreeIpaResponseConverter.convert(savedStack);
    }

    private void ensureStackDoesNotExists(String stackName, Workspace workspace) {
        Stack stack = stackService.findStackByNameAndWorkspaceId(stackName, workspace.getId());
        if (stack != null) {
            throw new BadRequestException("Cluster already exists: " + stackName);
        }
    }

    private Future<StatedImage> determineImageCatalog(String stackName, String platformString, FreeIpaRequest stackRequest, User user, Workspace workspace) {
        return executorService.submit(() -> {
            try {
                return imageService.determineImageFromCatalog(workspace.getId(), stackRequest.getImage(), platformString, null,true, user);
            } catch (CloudbreakImageNotFoundException | CloudbreakImageCatalogException e) {
                throw new BadRequestException(e.getMessage(), e);
            }
        });
    }

    private void fillInstanceMetadata(Stack stack) {
        long privateIdNumber = 0;
        for (InstanceGroup instanceGroup : stack.getInstanceGroups()) {
            for (InstanceMetaData instanceMetaData : instanceGroup.getAllInstanceMetaData()) {
                instanceMetaData.setPrivateId(privateIdNumber++);
                instanceMetaData.setInstanceStatus(InstanceStatus.REQUESTED);
            }
        }
    }

    private StatedImage getImageCatalog(Future<StatedImage> imgFromCatalogFuture) {
        return Optional.ofNullable(imgFromCatalogFuture).map(f -> {
            try {
                return f.get(1, TimeUnit.HOURS);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                throw new RuntimeException("Image catalog determaination failed", e);
            }
        }).orElse(null);
    }
}
