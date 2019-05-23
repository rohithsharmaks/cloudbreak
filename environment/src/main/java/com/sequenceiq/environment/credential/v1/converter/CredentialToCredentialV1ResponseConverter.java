package com.sequenceiq.environment.credential.v1.converter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;

import org.springframework.stereotype.Component;

import com.sequenceiq.cloudbreak.common.json.Json;
import com.sequenceiq.cloudbreak.service.secret.model.StringToSecretResponseConverter;
import com.sequenceiq.environment.api.v1.credential.model.request.CredentialRequest;
import com.sequenceiq.environment.api.v1.credential.model.response.CredentialResponse;
import com.sequenceiq.environment.credential.attributes.CredentialAttributes;
import com.sequenceiq.environment.credential.domain.Credential;
import com.sequenceiq.environment.credential.validation.CredentialValidator;
import com.sequenceiq.environment.credential.validation.definition.CredentialDefinitionService;

@Component
public class CredentialToCredentialV1ResponseConverter {
    private static final List<String> FIELDS_TO_COVER = Arrays.asList("password", "secretKey", "serviceAccountPrivateKey");

    private static final String PLACEHOLDER = "********";

    @Inject
    private CredentialValidator credentialValidator;

    @Inject
    private CredentialDefinitionService credentialDefinitionService;

    @Inject
    private AwsCredentialV1ParametersToAwsCredentialAttributesConverter awsConverter;

    @Inject
    private AzureCredentialV1ParametersToAzureCredentialAttributesConverter azureConverter;

    @Inject
    private CumulusCredentialV1ParametersToCumulusCredentialAttributesConverter cumulusConverter;

    @Inject
    private GcpCredentialV1ParametersToGcpCredentialAttributesConverter gcpConverter;

    @Inject
    private MockCredentialV1ParametersToMockCredentialAttributesConverter mockConverter;

    @Inject
    private OpenStackCredentialV1ParametersToOpenStackCredentialAttributesConverter openstackConverter;

    @Inject
    private YarnCredentialV1ParametersToAwsYarnAttributesConverter yarnConverter;

    @Inject
    private StringToSecretResponseConverter secretConverter;

    public CredentialResponse convert(Credential source) {
        CredentialResponse response = new CredentialResponse();
        response.setId(source.getId());
        credentialValidator.validateCredentialCloudPlatform(source.getCloudPlatform());
        response.setCloudPlatform(source.getCloudPlatform());
        response.setName(source.getName());
        if (source.getAttributes() != null) {
            try {
                CredentialAttributes attributes = new Json(source.getAttributes()).get(CredentialAttributes.class);
                response.setAzure(azureConverter.convert(attributes.getAzure()));
                response.setAws(awsConverter.convert(attributes.getAws()));
                response.setCumulus(cumulusConverter.convert(attributes.getCumulus()));
                response.setGcp(gcpConverter.convert(attributes.getGcp()));
                response.setMock(mockConverter.convert(attributes.getMock()));
                response.setOpenstack(openstackConverter.convert(attributes.getOpenstack()));
                response.setYarn(yarnConverter.convert(attributes.getYarn()));
            } catch (IOException e) {
                throw new BadRequestException("Cannot deserialize the credential's attributes", e);
            }

            //TODO: remove sesitives: Bubba
//            Map<String, Object> parameters = credentialDefinitionService.removeSensitives(platform(source.getCloudPlatform()), secretAttributes.getMap());
//            coverSensitiveData(parameters);
//            credentialParameterSetterUtil.setProperParameters(source.getCloudPlatform(), response, parameters);
            if (response.getAws() != null) {
                response.getAws().setGovCloud(source.getGovCloud());
            }
            response.setAttributes(secretConverter.convert(source.getAttributesSecret()));
            response.setResourceCrn(source.getResourceCrn());
        }
        response.setDescription(source.getDescription() == null ? "" : source.getDescription());
        return response;
    }

    public Credential convert(CredentialRequest source) {
        Credential credential = new Credential();
        credential.setName(source.getName());
        credential.setDescription(source.getDescription());
        credential.setCloudPlatform(source.getCloudPlatform());
        convertAttributes(source, credential);
        if (source.getAws() != null) {
            credential.setGovCloud(source.getAws().getGovCloud());
        }
        return credential;
    }

    private void convertAttributes(CredentialRequest source, Credential credential) {
        CredentialAttributes credentialAttributes = new CredentialAttributes();
        Optional.ofNullable(source.getAws()).ifPresent(params -> credentialAttributes.setAws(awsConverter.convert(params)));
        Optional.ofNullable(source.getAzure()).ifPresent(params -> credentialAttributes.setAzure(azureConverter.convert(params)));
        Optional.ofNullable(source.getGcp()).ifPresent(params -> credentialAttributes.setGcp(gcpConverter.convert(params)));
        Optional.ofNullable(source.getCumulus()).ifPresent(params -> credentialAttributes.setCumulus(cumulusConverter.convert(params)));
        Optional.ofNullable(source.getMock()).ifPresent(params -> credentialAttributes.setMock(mockConverter.convert(params)));
        Optional.ofNullable(source.getOpenstack()).ifPresent(params -> credentialAttributes.setOpenstack(openstackConverter.convert(params)));
        Optional.ofNullable(source.getYarn()).ifPresent(params -> credentialAttributes.setYarn(yarnConverter.convert(params)));
        credential.setAttributes(new Json(credentialAttributes).getValue());
    }

    private void coverSensitiveData(Map<String, Object> params) {
        for (String field : FIELDS_TO_COVER) {
            if (params.get(field) != null) {
                params.put(field, PLACEHOLDER);
            }
        }
    }
}
