package com.sequenceiq.redbeams.api.model.describe;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.sequenceiq.redbeams.api.model.common.Status;
import com.sequenceiq.redbeams.doc.ModelDescriptions;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

@ApiModel("DescribeRedbeamsV1Response")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DescribeRedbeamsResponse {
    @NotNull
    @ApiModelProperty(value = ModelDescriptions.ENVIRONMENT_ID, required = true)
    private String environmentId;

    @NotNull
    @ApiModelProperty(value = ModelDescriptions.ExternalDatabaseServer.NAME, required = true)
    private String name;

    @NotNull
    private String resourceCrn;

    private Status status;

    private String statusReason;

    public String getEnvironmentId() {
        return environmentId;
    }

    public void setEnvironmentId(String environmentId) {
        this.environmentId = environmentId;
    }

    public String getResourceCrn() {
        return resourceCrn;
    }

    public void setResourceCrn(String resourceCrn) {
        this.resourceCrn = resourceCrn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getStatusReason() {
        return statusReason;
    }

    public void setStatusReason(String statusReason) {
        this.statusReason = statusReason;
    }
}
