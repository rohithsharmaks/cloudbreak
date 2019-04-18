package com.sequenceiq.freeipa.flow.chain;

import static com.sequenceiq.cloudbreak.core.flow2.stack.provision.StackCreationEvent.START_CREATION_EVENT;
import static com.sequenceiq.freeipa.flow.provision.FreeIpaProvisionEvent.FREEIPA_PROVISION_EVENT;
import static com.sequenceiq.freeipa.flow.chain.FlowChainTriggers.FREEIPA_PROVISION_TRIGGER_EVENT;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.springframework.stereotype.Component;

import com.sequenceiq.cloudbreak.cloud.event.Selectable;
import com.sequenceiq.cloudbreak.core.flow2.chain.FlowEventChainFactory;
import com.sequenceiq.cloudbreak.reactor.api.event.StackEvent;

@Component
public class FreeIpaProvisionFlowEventChainFactory implements FlowEventChainFactory<StackEvent> {
    @Override
    public String initEvent() {
        return FREEIPA_PROVISION_TRIGGER_EVENT;
    }

    @Override
    public Queue<Selectable> createFlowTriggerEventQueue(StackEvent event) {

        Queue<Selectable> flowEventChain = new ConcurrentLinkedQueue<>();
        flowEventChain.add(new StackEvent(START_CREATION_EVENT.event(), event.getStackId(), event.accepted()));
        flowEventChain.add(new StackEvent(FREEIPA_PROVISION_EVENT.event(), event.getStackId()));
        return flowEventChain;
    }
}
