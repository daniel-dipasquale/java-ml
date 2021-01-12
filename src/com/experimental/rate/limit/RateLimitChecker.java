package com.experimental.rate.limit;

@FunctionalInterface
public interface RateLimitChecker {
    boolean isLimitHit(String bucketName, int count);
}
