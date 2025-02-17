package com.sequenceiq.environment.network.dto;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import com.sequenceiq.cloudbreak.cloud.model.CloudSubnet;

public class NetworkDto {

    private Long id;

    private String name;

    private final String resourceCrn;

    private final AwsParams aws;

    private final AzureParams azure;

    private final Set<String> subnetIds;

    private final Map<String, CloudSubnet> subnetMetas;

    public NetworkDto(String resourceCrn, Long id, String name, AwsParams aws, AzureParams azure, Set<String> subnetIds, Map<String, CloudSubnet> subnetMetas) {
        this.resourceCrn = resourceCrn;
        this.id = id;
        this.name = name;
        this.aws = aws;
        this.azure = azure;
        if (CollectionUtils.isEmpty(subnetIds)) {
            this.subnetIds = new HashSet<>();
        } else {
            this.subnetIds = subnetIds;
        }
        if (MapUtils.isEmpty(subnetMetas)) {
            this.subnetMetas = new HashMap<>();
        } else {
            this.subnetMetas = subnetMetas;
        }
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public AwsParams getAws() {
        return aws;
    }

    public AzureParams getAzure() {
        return azure;
    }

    public Set<String> getSubnetIds() {
        return subnetIds;
    }

    public Map<String, CloudSubnet> getSubnetMetas() {
        return subnetMetas;
    }

    public String getResourceCrn() {
        return resourceCrn;
    }

    public String getName() {
        return name;
    }

    public static final class Builder {
        private Long id;

        private String name;

        private String resourceCrn;

        private AwsParams aws;

        private AzureParams azure;

        private Set<String> subnetIds;

        private Map<String, CloudSubnet> subnetMetas;

        private Builder() {
        }

        public static Builder aNetworkDto() {
            return new Builder();
        }

        public Builder withId(Long id) {
            this.id = id;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withAws(AwsParams aws) {
            this.aws = aws;
            return this;
        }

        public Builder withAzure(AzureParams azure) {
            this.azure = azure;
            return this;
        }

        public Builder withSubnetIds(Set<String> subnetIds) {
            this.subnetIds = subnetIds;
            return this;
        }

        public Builder withSubnetMetas(Map<String, CloudSubnet> subnetMetas) {
            this.subnetMetas = subnetMetas;
            return this;
        }

        public Builder withResourceCrn(String resourceCrn) {
            this.resourceCrn = resourceCrn;
            return this;
        }

        public NetworkDto build() {
            return new NetworkDto(resourceCrn, id, name, aws, azure, subnetIds, subnetMetas);
        }
    }
}
