package com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.request.cluster.storage;

import java.util.HashSet;
import java.util.Set;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.base.CloudStorageV4Base;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.request.cluster.storage.location.StorageLocationV4Request;
import com.sequenceiq.cloudbreak.doc.ModelDescriptions.ClusterModelDescription;
import com.sequenceiq.cloudbreak.validation.ValidCloudStorageV4Request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
@ValidCloudStorageV4Request
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CloudStorageV4Request extends CloudStorageV4Base {

    @Valid
    @ApiModelProperty(value = ClusterModelDescription.LOCATIONS, required = true)
    private Set<StorageLocationV4Request> locations = new HashSet<>();

    public Set<StorageLocationV4Request> getLocations() {
        return locations;
    }

    public void setLocations(Set<StorageLocationV4Request> locations) {
        this.locations = locations;
    }

}
