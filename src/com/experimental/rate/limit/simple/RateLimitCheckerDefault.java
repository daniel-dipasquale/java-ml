package com.experimental.rate.limit.simple;

import com.experimental.rate.limit.RateLimitChecker;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class RateLimitCheckerDefault implements RateLimitChecker {
    private final int allowed;

    @Override
    public boolean isLimitHit(final String bucketName, final int count) {
        return count >= allowed;
    }
}
