package com.experimental.rate.limit.simple;

import com.dipasquale.common.DateTimeSupport;
import com.dipasquale.data.structure.deque.SimpleNode;
import com.dipasquale.data.structure.deque.SimpleNodeDeque;
import com.dipasquale.threading.WaitHandle;
import com.experimental.rate.limit.RateLimitChecker;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

final class RateLimiterMultiBucket {
    private final RateLimitSlidingWindow slidingWindow;
    private final RateLimitChecker checker;
    private final DateTimeSupport dateTimeSupport;
    private final WaitHandle waitHandle;
    private final Map<String, SimpleNode<Bucket>> rateLimitersMap;
    private final SimpleNodeDeque<Bucket> rateLimitersQueue;

    public RateLimiterMultiBucket(final RateLimitSlidingWindow slidingWindow, final RateLimitChecker checker, final DateTimeSupport dateTimeSupport, final WaitHandle waitHandle, final int initialCapacity, final float loadFactor, final int concurrencyLevel) {
        this.slidingWindow = slidingWindow;
        this.checker = checker;
        this.dateTimeSupport = dateTimeSupport;
        this.waitHandle = waitHandle;
        this.rateLimitersMap = new ConcurrentHashMap<>(initialCapacity, loadFactor, concurrencyLevel);
        this.rateLimitersQueue = new SimpleNodeDeque<>(); // TODO: this won't work
    }

    private void clearOldRateLimiters() {
        SimpleNode<Bucket> node = rateLimitersQueue.peek();
        Bucket bucket = rateLimitersQueue.getValue(node);

        while (bucket != null && bucket.rateLimiter.cleared()) {
            rateLimitersMap.remove(bucket.name);
            rateLimitersQueue.remove(node);
            node = rateLimitersQueue.peek();
            bucket = rateLimitersQueue.getValue(node);
        }

        if (bucket != null) {
            rateLimitersQueue.offerLast(node);
        }
    }

    private RateLimiterSingle getRateLimiter(final String bucketName) {
        SimpleNode<Bucket> node = rateLimitersMap.compute(bucketName, (bn, nl) -> {
            if (nl == null) {
                RateLimitAuditor auditor = new RateLimitAuditor(slidingWindow, checker);
                RateLimiterSingle rateLimiter = new RateLimiterSingle(dateTimeSupport, auditor, waitHandle);
                Bucket bucket = new Bucket(bn, rateLimiter);
                SimpleNode<Bucket> nodeNew = rateLimitersQueue.createUnbound(bucket);

                clearOldRateLimiters();
                rateLimitersQueue.offer(nodeNew);

                return nodeNew;
            }

            rateLimitersQueue.offerLast(nl);

            return nl;
        });

        return rateLimitersQueue.getValue(node).rateLimiter;
    }

    public boolean isLimitHit(final String bucketName, final int count) {
        return getRateLimiter(bucketName).isLimitHit(count);
    }

    public void acquire(final String bucketName, final int count) throws InterruptedException {
        getRateLimiter(bucketName).acquire(count);
    }

    @RequiredArgsConstructor
    private static final class Bucket {
        private final String name;
        private final RateLimiterSingle rateLimiter;
    }
}
