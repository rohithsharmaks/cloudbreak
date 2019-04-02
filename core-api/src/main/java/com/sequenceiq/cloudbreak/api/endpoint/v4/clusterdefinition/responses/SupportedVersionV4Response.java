package com.sequenceiq.cloudbreak.api.endpoint.v4.clusterdefinition.responses;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SupportedVersionV4Response {

    private Set<SupportedServiceV4Response> services = new HashSet<>();

    private String version;

    private String type;

    public Set<SupportedServiceV4Response> getServices() {
        return services;
    }

    public void setServices(Set<SupportedServiceV4Response> services) {
        this.services = services;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
