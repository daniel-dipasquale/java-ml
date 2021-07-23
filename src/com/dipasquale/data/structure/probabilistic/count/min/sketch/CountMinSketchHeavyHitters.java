package com.dipasquale.data.structure.probabilistic.count.min.sketch;

import com.dipasquale.common.ObjectFactory;
import com.dipasquale.common.time.ExpirySupport;
import com.dipasquale.data.structure.map.SortedByValueRankedAggregator;
import com.dipasquale.threading.event.loop.EventLoop;
import lombok.RequiredArgsConstructor;

final class CountMinSketchHeavyHitters<T> implements CountMinSketch<T> {
    private final CountMinSketch<T> countMinSketch;
    private final HeavyHittersCollector<T> heavyHittersCollector;
    private final SortedByValueRankedAggregator<T, Long> heavyHittersRankedAggregator;
    private final EventLoop eventLoop;

    CountMinSketchHeavyHitters(final ObjectFactory<CountMinSketch<T>> countMinSketchFactory, final ExpirySupport expirySupport, final HeavyHittersCollector<T> heavyHittersCollector, final int topLimit, final EventLoop eventLoop) {
        this.countMinSketch = new CountMinSketchTimedRecyclable<>(countMinSketchFactory, expirySupport, this::collectRecycled);
        this.heavyHittersCollector = heavyHittersCollector;
        this.heavyHittersRankedAggregator = SortedByValueRankedAggregator.createHighestRankedConcurrent(topLimit);
        this.eventLoop = eventLoop;
    }

    CountMinSketchHeavyHitters(final HeavyHittersConfig.RecyclableProxyFactory recyclableProxyFactory, final HeavyHittersCollector<T> heavyHittersCollector, final int topLimit, final EventLoop eventLoop) {
        this.countMinSketch = recyclableProxyFactory.create(this::collectRecycled);
        this.heavyHittersCollector = heavyHittersCollector;
        this.heavyHittersRankedAggregator = SortedByValueRankedAggregator.createHighestRankedConcurrent(topLimit);
        this.eventLoop = eventLoop;
    }

    private void collectRecycled(final CountMinSketch<T> countMinSketch, final long expiryDateTime) {
        CollectRecycledHandler collectRecycledHandler = new CollectRecycledHandler(countMinSketch, expiryDateTime, heavyHittersRankedAggregator.clear());

        if (eventLoop != null) {
            eventLoop.queue(n -> collectRecycledHandler.run(), 0L);
        } else {
            collectRecycledHandler.run();
        }
    }

    @Override
    public long get(final T item) {
        return countMinSketch.get(item);
    }

    @Override
    public long put(final T item, final long count) {
        long countTotal = countMinSketch.put(item, count);

        heavyHittersRankedAggregator.put(item, countTotal);

        return countTotal;
    }

    @RequiredArgsConstructor
    private final class CollectRecycledHandler implements Runnable {
        private final CountMinSketch<T> countMinSketch;
        private final long expiryDateTime;
        private final SortedByValueRankedAggregator<T, Long>.ClearResult heavyHittersResult;

        @Override
        public void run() {
            heavyHittersCollector.collect(countMinSketch, expiryDateTime, heavyHittersResult.retrieve());
        }
    }
}
