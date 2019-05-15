package com.sequenceiq.it.cloudbreak.newway.context;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.sequenceiq.it.cloudbreak.newway.CloudbreakClient;
import com.sequenceiq.it.cloudbreak.newway.Entity;
import com.sequenceiq.it.cloudbreak.newway.EnvironmentClient;

@Service
public class ClientContext {

    private final Map<String, CloudbreakClient> clients = new HashMap<>();

    private final Map<String, EnvironmentClient> environmentClients = new HashMap<>();

    public Map<String, CloudbreakClient> getClients() {
        return clients;
    }

    public Map<String, EnvironmentClient> getEnvironmentClients() {
        return environmentClients;
    }

    public Entity getClient(ServiceType serviceType, String who) {
        switch (serviceType) {
            case ENVIRONMENT:
                return getCloudbreakClient(who);
            case CLOUDBREAK:
                return getEnvironmentClient(who);
            default:
                throw new IllegalArgumentException("This service is not supported: " + serviceType.name());
        }
    }

    private Entity getCloudbreakClient(String who) {
        CloudbreakClient cloudbreakClient = clients.get(who);
        if (cloudbreakClient == null) {
            throw new IllegalStateException("Should create a client for this user: " + who);
        }
        return cloudbreakClient;
    }

    private Entity getEnvironmentClient(String who) {
        EnvironmentClient environmentClient = environmentClients.get(who);
        if (environmentClient == null) {
            throw new IllegalStateException("Should create a client for this user: " + who);
        }
        return environmentClient;
    }
}
