package com.sequenceiq.cloudbreak.auth.altus.endpoint;

import java.util.concurrent.TimeUnit;

import javax.annotation.concurrent.Immutable;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import com.github.rholder.retry.Attempt;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.StopStrategy;

/**
 * Factory class for instances of {@link StopStrategy}.
 */
public final class StopStrategies2 {

    private static final StopStrategy STOP_IMMEDIATELY = new StopImmediately();

    private StopStrategies2() {
    }

    /**
     * Returns a stop strategy that will stop retrying when the specified date and time is reached. Will always
     * execute the callable at least one time.
     *
     * @param stopDateTime the date and time to stop retrying
     * @return a stop strategy that stops at a specified date and time
     */
    public static StopStrategy waitUntilDateTime(DateTime stopDateTime) {
        long millisecondsUntilStop = new Duration(DateTime.now(), stopDateTime).getMillis();

        if (millisecondsUntilStop < 0) {
            return STOP_IMMEDIATELY;
        }

        return StopStrategies.stopAfterDelay(millisecondsUntilStop, TimeUnit.MILLISECONDS);
    }

    @Immutable
    private static final class StopImmediately implements StopStrategy {

        StopImmediately() {
        }

        @Override
        public boolean shouldStop(Attempt failedAttempt) {
            return true;
        }
    }
}
