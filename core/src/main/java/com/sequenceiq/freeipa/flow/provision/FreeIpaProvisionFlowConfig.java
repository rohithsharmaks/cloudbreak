package com.sequenceiq.freeipa.flow.provision;

import static com.sequenceiq.freeipa.flow.provision.FreeIpaProvisionEvent.FREEIPA_PROVISION_EVENT;
import static com.sequenceiq.freeipa.flow.provision.FreeIpaProvisionEvent.FREEIPA_PROVISION_FAILED_EVENT;
import static com.sequenceiq.freeipa.flow.provision.FreeIpaProvisionEvent.FREEIPA_PROVISION_FAILURE_HANDLED_EVENT;

import java.util.List;

import com.sequenceiq.cloudbreak.core.flow2.config.AbstractFlowConfiguration;
import com.sequenceiq.cloudbreak.core.flow2.config.AbstractFlowConfiguration.Transition.Builder;
import com.sequenceiq.cloudbreak.core.flow2.config.RetryableFlowConfiguration;

public class FreeIpaProvisionFlowConfig extends AbstractFlowConfiguration<FreeIpaProvisionState, FreeIpaProvisionEvent>
    implements RetryableFlowConfiguration<FreeIpaProvisionEvent> {

    private static final List<Transition<FreeIpaProvisionState, FreeIpaProvisionEvent>> TRANSITIONS =
            new Builder<FreeIpaProvisionState, FreeIpaProvisionEvent>().defaultFailureEvent(FREEIPA_PROVISION_FAILED_EVENT)
            .build();

    private static final FlowEdgeConfig<FreeIpaProvisionState, FreeIpaProvisionEvent> EDGE_CONFIG =
            new FlowEdgeConfig<>(FreeIpaProvisionState.INIT_STATE, FreeIpaProvisionState.FINAL_STATE,
                    FreeIpaProvisionState.PROVISION_FAILED_STATE, FREEIPA_PROVISION_FAILURE_HANDLED_EVENT);

    public FreeIpaProvisionFlowConfig() {
        super(FreeIpaProvisionState.class, FreeIpaProvisionEvent.class);
    }

    @Override
    protected List<Transition<FreeIpaProvisionState, FreeIpaProvisionEvent>> getTransitions() {
        return TRANSITIONS;
    }

    @Override
    protected FlowEdgeConfig<FreeIpaProvisionState, FreeIpaProvisionEvent> getEdgeConfig() {
        return EDGE_CONFIG;
    }

    @Override
    public FreeIpaProvisionEvent[] getEvents() {
        return FreeIpaProvisionEvent.values();
    }

    @Override
    public FreeIpaProvisionEvent[] getInitEvents() {
        return new FreeIpaProvisionEvent[] {FREEIPA_PROVISION_EVENT};
    }

    @Override
    public FreeIpaProvisionEvent getFailHandledEvent() {
        return FREEIPA_PROVISION_FAILURE_HANDLED_EVENT;
    }
}
