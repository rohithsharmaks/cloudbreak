package com.sequenceiq.redbeams.converter.cloud;

import static org.apache.commons.lang3.StringUtils.isEmpty;

import java.util.HashMap;
import java.util.Map;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.sequenceiq.cloudbreak.cloud.model.CloudCredential;
import com.sequenceiq.cloudbreak.common.json.Json;
import com.sequenceiq.redbeams.domain.Credential;

@Component
public class CredentialToCloudCredentialConverter implements Converter<Credential, CloudCredential> {

    private static final String CREDENTIAL_ID = "id";

    public CloudCredential convert(Credential credential) {
        if (credential == null) {
            return null;
        }
        Map<String, Object> fields;
        if (credential.getId() == null) {
            fields = isEmpty(credential.getAttributes()) ? new HashMap<>() : new Json(credential.getAttributes()).getMap();
        } else {
            fields = new Json(credential.getAttributes()).getMap();
        }
        fields.put(CREDENTIAL_ID, credential.getId());
        //TODO replace credential id with crn
        return new CloudCredential(credential.getId().toString(), credential.getName(), fields);
    }

}
