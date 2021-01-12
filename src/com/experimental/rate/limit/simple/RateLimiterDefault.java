package com.experimental.rate.limit.simple;

import com.dipasquale.common.DateTimeSupport;
import com.dipasquale.threading.WaitHandle;
import com.experimental.rate.limit.RateLimitChecker;
import com.experimental.rate.limit.RateLimiter;

public final class RateLimiterDefault implements RateLimiter {
    private final RateLimiterSingle rateLimiterSingle;
    private final RateLimiterMultiBucket rateLimiterMultiBucket;

    public RateLimiterDefault(final RateLimitSlidingWindow slidingWindow, final RateLimitChecker checker, final DateTimeSupport dateTimeSupport, final WaitHandle waitHandle, final int initialCapacity, final float loadFactor, final int concurrencyLevel) {
        this.rateLimiterSingle = new RateLimiterSingle(dateTimeSupport, new RateLimitAuditor(slidingWindow, checker), waitHandle);
        this.rateLimiterMultiBucket = new RateLimiterMultiBucket(slidingWindow, checker, dateTimeSupport, waitHandle, initialCapacity, loadFactor, concurrencyLevel);
    }

    @Override
    public boolean isLimitHit(final int count) {
        return rateLimiterSingle.isLimitHit(count);
    }

    @Override
    public boolean isLimitHit(final String bucketName, final int count) {
        return rateLimiterMultiBucket.isLimitHit(bucketName, count);
    }

    @Override
    public void acquire(final int count) throws InterruptedException {
        rateLimiterSingle.acquire(count);
    }

    @Override
    public void acquire(final String bucketName, final int count) throws InterruptedException {
        rateLimiterMultiBucket.acquire(bucketName, count);
    }
}
