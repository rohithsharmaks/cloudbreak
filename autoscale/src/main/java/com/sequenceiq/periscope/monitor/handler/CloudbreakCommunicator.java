package com.sequenceiq.periscope.monitor.handler;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.sequenceiq.cloudbreak.api.endpoint.v4.autoscales.request.FailureReportV4Request;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.response.StackV4Response;
import com.sequenceiq.cloudbreak.auth.altus.InternalCrnBuilder;
import com.sequenceiq.cloudbreak.client.CloudbreakUserCrnClient;

@Service
public class CloudbreakCommunicator {

    @Inject
    private CloudbreakUserCrnClient cloudbreakClient;

    @Inject
    private InternalCrnBuilder internalCrnBuilder;

    public StackV4Response getById(long cloudbreakStackId) {
        return cloudbreakClient.withCrn(internalCrnBuilder.getInternalCrnForServiceAsString()).autoscaleEndpoint().get(cloudbreakStackId);
    }

    public void failureReport(long stackId, FailureReportV4Request failureReport) {
        cloudbreakClient.withCrn(internalCrnBuilder.getInternalCrnForServiceAsString()).autoscaleEndpoint().failureReport(stackId, failureReport);
    }
}
