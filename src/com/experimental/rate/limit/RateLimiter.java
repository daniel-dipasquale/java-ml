package com.experimental.rate.limit;

public interface RateLimiter {
    boolean isLimitHit(int count);

    boolean isLimitHit(String bucketName, int count);

    void acquire(int count) throws InterruptedException;

    default void acquire() throws InterruptedException {
        acquire(1);
    }

    void acquire(String bucketName, int count) throws InterruptedException;

    default void acquire(String bucketName) throws InterruptedException {
        acquire(bucketName, 1);
    }
}
