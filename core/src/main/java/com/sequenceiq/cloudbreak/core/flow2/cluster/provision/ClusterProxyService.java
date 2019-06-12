package com.sequenceiq.cloudbreak.core.flow2.cluster.provision;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.sequenceiq.cloudbreak.domain.stack.Stack;
import com.sequenceiq.cloudbreak.domain.stack.cluster.Cluster;
import com.sequenceiq.cloudbreak.service.secret.vault.VaultSecret;
import com.sequenceiq.cloudbreak.service.stack.StackService;

@Component
public class ClusterProxyService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClusterProxyService.class);
    private static final Gson GSON = new Gson();

    @Inject
    private StackService stackService;

    class ClusterProxyConfigRegistrationRequest {
        private String clusterCrn;
        private List<ClusterServiceConfig> services;

        public ClusterProxyConfigRegistrationRequest(String clusterCrn, List<ClusterServiceConfig> services) {
            this.clusterCrn = clusterCrn;
            this.services = services;
        }

        @Override
        public String toString() {
            return "ClusterProxyConfigRegistrationRequest{" +
                    "clusterCrn='" + clusterCrn + '\'' +
                    ", services=" + services +
                    '}';
        }
    }

    private class ClusterProxyConfigRegistrationResponse {
        private String id;
        private String key;
    }

    class ClusterServiceConfig {
        private String serviceName;
        private List<String> endpoints;
        private List<ClusterServiceCredential> credentials;

        public ClusterServiceConfig(String serviceName, List<String> endpoints, List<ClusterServiceCredential> credentials) {
            this.serviceName = serviceName;
            this.endpoints = endpoints;
            this.credentials = credentials;
        }

        @Override
        public String toString() {
            return "ClusterServiceConfig{" +
                    "serviceName='" + serviceName + '\'' +
                    ", endpoints=" + endpoints +
                    ", credentials=" + credentials +
                    '}';
        }
    }

    class ClusterServiceCredential {
        private String username;
        private String credentialRef;

        public ClusterServiceCredential(String username, String credentialRef) {
            this.username = username;
            this.credentialRef = credentialRef;
        }

        @Override
        public String toString() {
            return "ClusterServiceCredential{" +
                    "username='" + username + '\'' +
                    ", credentialRef='" + credentialRef + '\'' +
                    '}';
        }
    }

    public void registerProxyConfiguration(Long stackId) {
        Stack stack = stackService.getByIdWithListsInTransaction(stackId);
        ClusterProxyConfigRegistrationRequest proxyConfigRequest = createProxyConfigRequest(stack);
        LOGGER.info("######### Proxy config request: {}", proxyConfigRequest);

        RestTemplate restTemplate = new RestTemplate();
//        ResponseEntity<ClusterProxyConfigRegistrationResponse> response = restTemplate.postForEntity("", proxyConfigRequest, ClusterProxyConfigRegistrationResponse.class);
//        LOGGER.info("######### Proxy config response: {}", response);
    }

    private ClusterProxyConfigRegistrationRequest createProxyConfigRequest(Stack stack) {
        Cluster cluster = stack.getCluster();

        String cloudbreakUser = cluster.getCloudbreakAmbariUser();
        String cloudbreakPasswordVaultPath = vaultPath(cluster.getCloudbreakAmbariPasswordSecret());

        String dpUser = cluster.getDpAmbariUser();
        String dpPasswordVaultPath = vaultPath(cluster.getDpAmbariPasswordSecret());

        List<ClusterServiceCredential> credentials = asList(new ClusterServiceCredential(cloudbreakUser, cloudbreakPasswordVaultPath),
                new ClusterServiceCredential(dpUser, dpPasswordVaultPath));
        ClusterServiceConfig serviceConfig = new ClusterServiceConfig("cloudera-manager", singletonList(clusterManagerUrl(stack)), credentials);
        return new ClusterProxyConfigRegistrationRequest(stack.getCluster().getId().toString(), singletonList(serviceConfig));
    }

    private String clusterManagerUrl(Stack stack) {
        String gatewayIp = stack.getPrimaryGatewayInstance().getPublicIpWrapper();
        Integer gatewayPort = stack.getGatewayPort();
        return String.format("https://%s:%d", gatewayIp, gatewayPort);
    }

    private String vaultPath(String vaultSecretJsonString) {
        return GSON.fromJson(vaultSecretJsonString, VaultSecret.class).getPath();
    }
}
