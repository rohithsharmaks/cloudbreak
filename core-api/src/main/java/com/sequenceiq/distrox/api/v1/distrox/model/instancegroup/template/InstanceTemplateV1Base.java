package com.sequenceiq.distrox.api.v1.distrox.model.instancegroup.template;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sequenceiq.cloudbreak.doc.ModelDescriptions.TemplateModelDescription;
import com.sequenceiq.distrox.api.v1.distrox.model.CloudPlatformProvider;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class InstanceTemplateV1Base implements Serializable, CloudPlatformProvider {

    @ApiModelProperty(TemplateModelDescription.AWS_PARAMETERS)
    private AwsInstanceTemplateV1Parameters aws;

    @ApiModelProperty(TemplateModelDescription.AZURE_PARAMETERS)
    private AzureInstanceTemplateV1Parameters azure;

    @ApiModelProperty(TemplateModelDescription.INSTANCE_TYPE)
    private String instanceType;

    public String getInstanceType() {
        return instanceType;
    }

    public void setInstanceType(String instanceType) {
        this.instanceType = instanceType;
    }

    @Override
    public AwsInstanceTemplateV1Parameters getAws() {
        return aws;
    }

    public void setAws(AwsInstanceTemplateV1Parameters aws) {
        this.aws = aws;
    }

    public void setAzure(AzureInstanceTemplateV1Parameters azure) {
        this.azure = azure;
    }

    @Override
    public AzureInstanceTemplateV1Parameters getAzure() {
        return azure;
    }
}
