package com.sequenceiq.cloudbreak.auth.altus.endpoint;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.rholder.retry.Attempt;
import com.github.rholder.retry.RetryException;
import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.StopStrategy;
import com.github.rholder.retry.WaitStrategies;
import com.github.rholder.retry.WaitStrategy;
import com.sequenceiq.cloudbreak.endpoint.ServiceEndpoint;
import com.sequenceiq.cloudbreak.endpoint.ServiceEndpointFinder;
import com.sequenceiq.cloudbreak.endpoint.ServiceEndpointLookupException;
import com.sequenceiq.cloudbreak.endpoint.ServiceEndpointRequest;

/**
 * Service endpoint finders that retries lookups on a delegate finder.
 */
public class RetryingServiceEndpointFinder implements ServiceEndpointFinder {

    private static final Logger LOG = LoggerFactory.getLogger(RetryingServiceEndpointFinder.class);

    /**
     * The delegate service endpoint finder.
     */
    private final ServiceEndpointFinder delegate;

    /**
     * Creates a retrying service endpoint finder with the specified parameters.
     *
     * @param delegate the delegate service endpoint finder
     */
    public RetryingServiceEndpointFinder(@Nonnull ServiceEndpointFinder delegate) {
        this.delegate = Objects.requireNonNull(delegate, "delegate is null");
    }

    @Nonnull
    @Override
    public <T extends ServiceEndpoint> T getServiceEndpoint(ServiceEndpointRequest<T> serviceEndpointRequest)
            throws ServiceEndpointLookupException, InterruptedException {

        String targetInstanceId = serviceEndpointRequest.getTargetInstance().getTargetInstanceId();

        Retryer<T> loop = buildRetryer(serviceEndpointRequest);

        T serviceEndpoint;
        try {
            serviceEndpoint = loop.call(() -> delegate.getServiceEndpoint(serviceEndpointRequest));
        } catch (ExecutionException e) {
            try {
                throw e.getCause();
            } catch (RuntimeException | Error | InterruptedException cause) {
                throw cause;
            } catch (Throwable cause) {
                throw new IllegalStateException(cause);
            }
        } catch (RetryException e) {
            if (Thread.interrupted()) {
                throw new InterruptedException(String.format(
                        "Interrupted while discovering service endpoint for instance %s", targetInstanceId));
            }

            // All attempts failed
            LOG.info("Failed to discover service endpoint for instance {}", targetInstanceId);
            Attempt<?> lastFailedAttempt = e.getLastFailedAttempt();
            if (lastFailedAttempt.hasException()) {
                try {
                    throw lastFailedAttempt.getExceptionCause();
                } catch (RuntimeException | Error | InterruptedException | ServiceEndpointLookupException cause) {
                    throw cause;
                } catch (Throwable cause) {
                    throw new IllegalStateException(cause);
                }
            } else {
                throw new ServiceEndpointLookupException(String.format(
                        "Timed out while discovering service endpoint for instance %s", targetInstanceId), true);
            }
        }
        return serviceEndpoint;
    }

    /**
     * Builds a retryer for the specified request.
     *
     * @param serviceEndpointRequest the service endpoint request
     * @param <T> the type of service endpoint
     * @return the retryer
     */
    private <T extends ServiceEndpoint> Retryer<T> buildRetryer(ServiceEndpointRequest<T> serviceEndpointRequest) {
        String targetInstanceId = serviceEndpointRequest.getTargetInstance().getTargetInstanceId();
        Optional<DateTime> waitUntilTime = serviceEndpointRequest.getWaitUntilTime();

        StopStrategy stop;
        if (waitUntilTime.isPresent()) {
            // Given the time at which waiting should stop,
            // get the available number of millis from this instant
            DateTime timeout = waitUntilTime.get();
            stop = StopStrategies2.waitUntilDateTime(timeout);
            LOG.info("Trying until {} to discover service endpoint for instance {}", timeout, targetInstanceId);
        } else {
            stop = StopStrategies.neverStop();
            LOG.info("Unbounded wait to discover service endpoint for instance {}", targetInstanceId);
        }

        long sleepTime = serviceEndpointRequest.getPollingIntervalInMs();
        WaitStrategy wait = WaitStrategies.fixedWait(sleepTime, TimeUnit.MILLISECONDS);
        LOG.info("Checking every {} milliseconds", sleepTime);

        return RetryerBuilder.<T>newBuilder()
                .retryIfException(t -> (t instanceof ServiceEndpointLookupException)
                        && ((ServiceEndpointLookupException) t).isRetryable())
                .retryIfResult(Objects::isNull)
                .withStopStrategy(stop)
                .withWaitStrategy(wait)
                .build();
    }

}
