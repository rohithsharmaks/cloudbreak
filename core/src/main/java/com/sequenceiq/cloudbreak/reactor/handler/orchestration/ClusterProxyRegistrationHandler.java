package com.sequenceiq.cloudbreak.reactor.handler.orchestration;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.sequenceiq.cloudbreak.common.event.Selectable;
import com.sequenceiq.cloudbreak.core.flow2.cluster.provision.ClusterProxyService;
import com.sequenceiq.cloudbreak.reactor.api.event.orchestration.ClusterProxyRegistrationFailed;
import com.sequenceiq.cloudbreak.reactor.api.event.orchestration.ClusterProxyRegistrationRequest;
import com.sequenceiq.cloudbreak.reactor.api.event.orchestration.ClusterProxyRegistrationSuccess;
import com.sequenceiq.flow.event.EventSelectorUtil;
import com.sequenceiq.flow.reactor.api.handler.EventHandler;

import reactor.bus.Event;
import reactor.bus.EventBus;

@Component
public class ClusterProxyRegistrationHandler implements EventHandler<ClusterProxyRegistrationRequest> {
    @Inject
    private EventBus eventBus;

    @Inject
    private ClusterProxyService clusterProxyService;

    @Override
    public String selector() {
        return EventSelectorUtil.selector(ClusterProxyRegistrationRequest.class);
    }

    @Override
    public void accept(Event<ClusterProxyRegistrationRequest> event) {
        ClusterProxyRegistrationRequest request = event.getData();
        Selectable response;
        try {
            clusterProxyService.registerProxyConfiguration(request.getResourceId());
            response = new ClusterProxyRegistrationSuccess(request.getResourceId());
        } catch (Exception e) {
            response = new ClusterProxyRegistrationFailed(request.getResourceId(), e);
        }
        eventBus.notify(response.selector(), new Event<>(event.getHeaders(), response));
    }
}
