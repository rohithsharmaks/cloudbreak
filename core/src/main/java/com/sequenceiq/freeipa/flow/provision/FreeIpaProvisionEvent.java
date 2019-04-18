package com.sequenceiq.freeipa.flow.provision;

import com.sequenceiq.cloudbreak.core.flow2.FlowEvent;

public enum FreeIpaProvisionEvent implements FlowEvent {
    FREEIPA_PROVISION_EVENT("FREEIPA_PROVISION_TRIGGER_EVENT"),
    FREEIPA_PROVISION_FAILED_EVENT("FREEIPA_PROVISION_FAILED_EVENT"),
    FREEIPA_PROVISION_FAILURE_HANDLED_EVENT("FREEIPA_PROVISION_FAILURE_HANDLED_EVENT"),
    FREEIPA_PROVISION_FINISHED_EVENT("FREEIPA_PROVISION_FINISHED_EVENT");

    private final String event;

    FreeIpaProvisionEvent(String event) {
        this.event = event;
    }

    @Override
    public String event() {
        return event;
    }
}
