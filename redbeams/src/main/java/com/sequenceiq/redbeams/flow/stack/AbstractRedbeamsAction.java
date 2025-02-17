package com.sequenceiq.redbeams.flow.stack;

import com.sequenceiq.cloudbreak.common.event.Payload;
import com.sequenceiq.flow.core.AbstractAction;
import com.sequenceiq.flow.core.CommonContext;
import com.sequenceiq.flow.core.FlowEvent;
import com.sequenceiq.flow.core.FlowState;

import javax.annotation.PostConstruct;

public abstract class AbstractRedbeamsAction<S extends FlowState, E extends FlowEvent, C extends CommonContext, P extends Payload>
        extends AbstractAction<S, E, C, P> {
    protected AbstractRedbeamsAction(Class<P> payloadClass) {
        super(payloadClass);
    }

    @PostConstruct
    public void init() {
        super.init();
    }
}
