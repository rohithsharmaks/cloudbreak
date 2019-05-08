package com.sequenceiq.environment.api.credential.model.response;

import java.io.Serializable;

import com.sequenceiq.environment.api.credential.doc.CredentialModelDescription;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class CredentialPrerequisitesV4Response implements Serializable {

    @ApiModelProperty(value = CredentialModelDescription.CLOUD_PLATFORM, required = true)
    private String cloudPlatform;

    @ApiModelProperty(CredentialModelDescription.ACCOUNT_IDENTIFIER)
    private String accountId;

    @ApiModelProperty(CredentialModelDescription.AWS_CREDENTIAL_PREREQUISITES)
    private AwsCredentialPrerequisites aws;

    @ApiModelProperty(CredentialModelDescription.AZURE_CREDENTIAL_PREREQUISITES)
    private AzureCredentialPrerequisites azure;

    @ApiModelProperty(CredentialModelDescription.GCP_CREDENTIAL_PREREQUISITES)
    private GcpCredentialPrerequisites gcp;

    public CredentialPrerequisitesV4Response(String cloudPlatform, String accountId, AwsCredentialPrerequisites aws) {
        this.cloudPlatform = cloudPlatform;
        this.accountId = accountId;
        this.aws = aws;
    }

    public CredentialPrerequisitesV4Response(String cloudPlatform, AzureCredentialPrerequisites azure) {
        this.cloudPlatform = cloudPlatform;
        this.azure = azure;
    }

    public CredentialPrerequisitesV4Response(String cloudPlatform, GcpCredentialPrerequisites gcp) {
        this.cloudPlatform = cloudPlatform;
        this.gcp = gcp;
    }

    public String getCloudPlatform() {
        return cloudPlatform;
    }

    public String getAccountId() {
        return accountId;
    }

    public AwsCredentialPrerequisites getAws() {
        return aws;
    }

    public AzureCredentialPrerequisites getAzure() {
        return azure;
    }

    public GcpCredentialPrerequisites getGcp() {
        return gcp;
    }
}
