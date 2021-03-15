package com.experimental.rate.limit.simple;

import com.dipasquale.data.structure.deque.NodeDeque;
import com.dipasquale.data.structure.deque.SimpleNode;
import com.dipasquale.data.structure.deque.SimpleNodeDeque;
import com.experimental.rate.limit.RateLimitChecker;
import lombok.Generated;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@RequiredArgsConstructor
final class RateLimitAuditor {
    private final RateLimitSlidingWindow slidingWindow;
    private final RateLimitChecker checker;
    private final Map<Long, Counter> timeFrameCounters = new HashMap<>();
    private final NodeDeque<Long, SimpleNode<Long>> timeFrames = new SimpleNodeDeque<>();
    private int transactions = 0;

    private long getTimeFrame(final long currentTime, final int index) {
        return currentTime - currentTime % slidingWindow.getMilliseconds() - slidingWindow.getMilliseconds() * index;
    }

    private boolean removeOldCounters(final long now) {
        boolean removed = false;
        long timeFrame = getTimeFrame(now, slidingWindow.getBuckets());

        while (!timeFrames.isEmpty() && timeFrames.getValue(timeFrames.peek()) <= timeFrame) {
            long timeFrameRemoved = timeFrames.getValue(timeFrames.poll());
            Counter counterRemoved = timeFrameCounters.remove(timeFrameRemoved);

            transactions -= counterRemoved.count;
            removed = true;
        }

        return removed;
    }

    private Function<Long, Counter> createCounterFactory(final boolean removedOldCounters, final long currentTime) {
        return timeFrame -> {
            timeFrames.offer(timeFrames.createUnbound(timeFrame));

            if (!removedOldCounters) {
                removeOldCounters(currentTime);
            }

            return new Counter();
        };
    }

    private boolean isLimitHit() {
        return checker.isLimitHit(null, transactions);
    }

    public boolean isLimitHit(final long currentTime, final int count) {
        long timeFrame = getTimeFrame(currentTime, 0);
        boolean removedOldCounters = false;

        if (isLimitHit()) {
            removedOldCounters = removeOldCounters(currentTime);

            if (!removedOldCounters) {
                return true;
            }
        }

        Counter counter = timeFrameCounters.computeIfAbsent(timeFrame, createCounterFactory(removedOldCounters, currentTime));

        counter.count += count;
        transactions += count;

        return isLimitHit();
    }

    public boolean cleared(final long currentTime) {
        if (timeFrames.isEmpty()) {
            return true;
        }

        long currentTimeFrame = getTimeFrame(currentTime, 0);
        long timeFrame = timeFrames.getValue(timeFrames.peekLast());
        int index = (int) ((currentTimeFrame - timeFrame) / slidingWindow.getMilliseconds());

        return index >= slidingWindow.getBuckets();
    }

    public long getWaitTime(final long currentTime) {
        if (isLimitHit()) {
            long currentTimeFrame = getTimeFrame(currentTime, 0);
            long timeFrame = timeFrames.getValue(timeFrames.peek());
            int index = (int) ((currentTimeFrame - timeFrame) / slidingWindow.getMilliseconds());

            if (index < slidingWindow.getBuckets()) {
                return timeFrame + slidingWindow.getMilliseconds() * slidingWindow.getBuckets() - currentTime;
            }
        }

        return 0;
    }

    @Generated
    @ToString
    private static final class Counter {
        private int count;
    }
}
