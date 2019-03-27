package com.sequenceiq.cloudbreak.api.endpoint.v4.clusterdefinition.responses;

public class ServiceDependencyMatrixV4Response {

    private DependenciesV4Responses dependencies;

    private ServicesV4Responses services;

    public DependenciesV4Responses getDependencies() {
        return dependencies;
    }

    public void setDependencies(DependenciesV4Responses dependencies) {
        this.dependencies = dependencies;
    }

    public ServicesV4Responses getServices() {
        return services;
    }

    public void setServices(ServicesV4Responses services) {
        this.services = services;
    }
}
