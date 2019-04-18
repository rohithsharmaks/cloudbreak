package com.sequenceiq.freeipa.flow.provision;

import com.sequenceiq.cloudbreak.core.flow2.FlowState;

public enum FreeIpaProvisionState implements FlowState {
    INIT_STATE,
    PROVISION_FAILED_STATE,
    FINAL_STATE;
}
