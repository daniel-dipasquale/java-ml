package com.experimental.rate.limit.simple;

import com.dipasquale.common.DateTimeSupport;
import com.dipasquale.threading.wait.handle.WaitHandle;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
final class RateLimiterSingle {
    private static final TimeUnit MILLISECONDS_UNIT = TimeUnit.MILLISECONDS;
    private final DateTimeSupport dateTimeSupport;
    private final RateLimitAuditor auditor;
    private final WaitHandle waitHandle;

    public synchronized boolean isLimitHit(final int count) {
        return auditor.isLimitHit(dateTimeSupport.now(), count);
    }

    public synchronized void acquire(final int count) throws InterruptedException {
        long now = dateTimeSupport.now();

        if (auditor.isLimitHit(now, count)) {
            long waitTime = auditor.getWaitTime(now);

            if (waitTime > 0L) {
                waitHandle.await(waitTime, MILLISECONDS_UNIT);
            }
        }
    }

    public synchronized boolean cleared() {
        return auditor.cleared(dateTimeSupport.now());
    }
}
