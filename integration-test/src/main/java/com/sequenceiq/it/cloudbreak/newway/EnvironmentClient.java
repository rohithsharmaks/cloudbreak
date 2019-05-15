package com.sequenceiq.it.cloudbreak.newway;

import java.util.function.Function;

import com.sequenceiq.cloudbreak.client.ConfigKey;
import com.sequenceiq.cloudbreak.client.IdentityClient;
import com.sequenceiq.it.IntegrationTestContext;
import com.sequenceiq.it.cloudbreak.newway.actor.CloudbreakUser;

public class EnvironmentClient extends Entity {
    public static final String ENVIRONMENT_CLIENT = "ENVIRONMENT_CLIENT";

    private static com.sequenceiq.environment.client.EnvironmentClient singletonEnvironmentClient;

    private com.sequenceiq.environment.client.EnvironmentClient environmentClient;

    private IdentityClient identityClient;

    private Long workspaceId;

    EnvironmentClient(String newId) {
        super(newId);
    }

    EnvironmentClient() {
        this(ENVIRONMENT_CLIENT);
    }

    public void setEnvironmentClient(com.sequenceiq.environment.client.EnvironmentClient environmentClient) {
        this.environmentClient = environmentClient;
    }

    public com.sequenceiq.environment.client.EnvironmentClient getEnvironmentClient() {
        return environmentClient;
    }

    public static com.sequenceiq.environment.client.EnvironmentClient getSingletonEnvironmentClient() {
        return singletonEnvironmentClient;
    }

    public static Function<IntegrationTestContext, EnvironmentClient> getTestContextEnvironmentClient(String key) {
        return testContext -> testContext.getContextParam(key, EnvironmentClient.class);
    }

    public static Function<IntegrationTestContext, EnvironmentClient> getTestContextCloudbreakClient() {
        return getTestContextEnvironmentClient(ENVIRONMENT_CLIENT);
    }

    public static EnvironmentClient created() {
        EnvironmentClient client = new EnvironmentClient();
        client.setCreationStrategy(EnvironmentClient::createProxyEnvironmentClient);
        return client;
    }

    private static synchronized void createProxyEnvironmentClient(IntegrationTestContext integrationTestContext, Entity entity) {
        EnvironmentClient clientEntity = (EnvironmentClient) entity;
        if (singletonEnvironmentClient == null) {
            singletonEnvironmentClient = new ProxyEnvironmentClient(
                    integrationTestContext.getContextParam(CloudbreakTest.ENVIRONMENT_SERVER_ROOT),
                    integrationTestContext.getContextParam(CloudbreakTest.CAAS_PROTOCOL),
                    integrationTestContext.getContextParam(CloudbreakTest.CAAS_ADDRESS),
                    integrationTestContext.getContextParam(CloudbreakTest.REFRESH_TOKEN),
                    new ConfigKey(false, true, true),
                    integrationTestContext.getContextParam(CloudbreakTest.IDENTITY_URL),
                    integrationTestContext.getContextParam(CloudbreakTest.AUTOSCALE_CLIENT_ID),
                    integrationTestContext.getContextParam(CloudbreakTest.AUTOSCALE_SECRET));
        }
        clientEntity.environmentClient = singletonEnvironmentClient;
    }

    public static synchronized EnvironmentClient createProxyEnvironmentClient(TestParameter testParameter, CloudbreakUser cloudbreakUser) {
        EnvironmentClient clientEntity = new EnvironmentClient();
        clientEntity.environmentClient = new ProxyEnvironmentClient(
                testParameter.get(CloudbreakTest.ENVIRONMENT_SERVER_ROOT),
                testParameter.get(CloudbreakTest.CAAS_PROTOCOL),
                testParameter.get(CloudbreakTest.CAAS_ADDRESS),
                cloudbreakUser.getToken(),
                new ConfigKey(false, true, true));
        return clientEntity;
    }

    public Long getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(Long workspaceId) {
        this.workspaceId = workspaceId;
    }
}

