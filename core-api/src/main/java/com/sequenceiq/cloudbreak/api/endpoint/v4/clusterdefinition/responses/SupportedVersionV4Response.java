package com.sequenceiq.cloudbreak.api.endpoint.v4.clusterdefinition.responses;

public class SupportedVersionV4Response {

    SupportedServicesV4Response services;

    private String version;

    private String type;

    public SupportedServicesV4Response getServices() {
        return services;
    }

    public void setServices(SupportedServicesV4Response services) {
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
