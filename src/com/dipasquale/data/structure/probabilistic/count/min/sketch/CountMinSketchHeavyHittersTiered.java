package com.dipasquale.data.structure.probabilistic.count.min.sketch;

import com.dipasquale.common.ExceptionHandlerUtils;
import com.dipasquale.common.ExpirySupport;
import com.dipasquale.common.ObjectFactory;
import com.dipasquale.data.structure.map.SortedByValueRankedAggregator;
import com.dipasquale.threading.EventLoop;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

final class CountMinSketchHeavyHittersTiered<T> implements CountMinSketch<T> {
    private CountMinSketch<T> aggregatedCountMinSketch;
    private final ObjectFactory<CountMinSketch<T>> aggregatedCountMinSketchFactory;
    private final CountMinSketch<T> countMinSketch;
    private final HeavyHittersCollector<T> heavyHittersCollector;
    private final SortedByValueRankedAggregator<T, Long> heavyHittersRankedAggregator;
    private final HeavyHittersConfig.AggregatePredicate<T> flushPredicate;
    private final EventLoop eventLoop;

    CountMinSketchHeavyHittersTiered(final ObjectFactory<CountMinSketch<T>> aggregatedCountMinSketchFactory, final ObjectFactory<CountMinSketch<T>> countMinSketchFactory, final ExpirySupport expirySupport, final HeavyHittersCollector<T> heavyHittersCollector, final int topLimit, final HeavyHittersConfig.AggregatePredicate<T> flushPredicate, final EventLoop eventLoop) {
        this.aggregatedCountMinSketch = aggregatedCountMinSketchFactory.create();
        this.aggregatedCountMinSketchFactory = aggregatedCountMinSketchFactory;
        this.countMinSketch = new CountMinSketchHeavyHitters<>(countMinSketchFactory, expirySupport, this::collectRecycledHeavyHitters, topLimit, eventLoop);
        this.heavyHittersCollector = heavyHittersCollector;
        this.heavyHittersRankedAggregator = SortedByValueRankedAggregator.createHighestRankedConcurrent(Integer.MAX_VALUE);
        this.flushPredicate = flushPredicate;
        this.eventLoop = eventLoop;
    }

    CountMinSketchHeavyHittersTiered(final ObjectFactory<CountMinSketch<T>> aggregatedCountMinSketchFactory, final HeavyHittersConfig.HeavyHittersProxyFactory countMinSketchFactory, final HeavyHittersCollector<T> heavyHittersCollector, final HeavyHittersConfig.AggregatePredicate<T> flushPredicate, final EventLoop eventLoop) {
        this.aggregatedCountMinSketch = aggregatedCountMinSketchFactory.create();
        this.aggregatedCountMinSketchFactory = aggregatedCountMinSketchFactory;
        this.countMinSketch = countMinSketchFactory.create(this::collectRecycledHeavyHitters);
        this.heavyHittersCollector = heavyHittersCollector;
        this.heavyHittersRankedAggregator = SortedByValueRankedAggregator.createHighestRankedConcurrent(Integer.MAX_VALUE);
        this.flushPredicate = flushPredicate;
        this.eventLoop = eventLoop;
    }

    private void collectRecycledHeavyHittersIfPredicateApproves(final long expiryDateTime) {
        Set<T> heavyHitters = heavyHittersRankedAggregator.getKeys();
        long minimumHeavyHitterCount = heavyHittersRankedAggregator.getExtremeValue();
        List<Throwable> suppressed = new ArrayList<>();

        if (flushPredicate.shouldRecycle(aggregatedCountMinSketch, expiryDateTime, heavyHitters, minimumHeavyHitterCount)) {
            try {
                CollectHeavyHittersHandler collectHeavyHittersHandler = new CollectHeavyHittersHandler(aggregatedCountMinSketch, expiryDateTime, heavyHittersRankedAggregator.clear());

                if (eventLoop != null) {
                    eventLoop.queue(collectHeavyHittersHandler, 0L);
                } else {
                    collectHeavyHittersHandler.run();
                }
            } catch (Throwable e) {
                suppressed.add(e);
            } finally {
                try {
                    flushPredicate.confirmRecycled(aggregatedCountMinSketch, expiryDateTime, heavyHitters, minimumHeavyHitterCount);
                } catch (Throwable e) {
                    ExceptionHandlerUtils.throwAsSuppressedIfAny(() -> new RuntimeException(e.getMessage(), e), suppressed);
                } finally {
                    aggregatedCountMinSketch = aggregatedCountMinSketchFactory.create();
                }
            }

            ExceptionHandlerUtils.throwAsSuppressedIfAny("unable to collect the heavy hitters", suppressed);
        }
    }

    private void collectRecycledHeavyHitters(final CountMinSketch<T> countMinSketch, final long expiryDateTime, final List<T> items) {
        synchronized (heavyHittersRankedAggregator) {
            for (T item : items) {
                long count = countMinSketch.get(item);
                long countAggregated = aggregatedCountMinSketch.put(item, count);

                heavyHittersRankedAggregator.put(item, countAggregated);
            }

            collectRecycledHeavyHittersIfPredicateApproves(expiryDateTime);
        }
    }

    @Override
    public long get(final T item) {
        return countMinSketch.get(item);
    }

    @Override
    public long put(final T item, final long count) {
        return countMinSketch.put(item, count);
    }

    @RequiredArgsConstructor
    private final class CollectHeavyHittersHandler implements Runnable {
        private final CountMinSketch<T> aggregatedCountMinSketch;
        private final long expiryDateTime;
        private final SortedByValueRankedAggregator<T, Long>.ClearResult heavyHittersResult;

        @Override
        public void run() {
            heavyHittersCollector.collect(aggregatedCountMinSketch, expiryDateTime, heavyHittersResult.retrieve());
        }
    }
}
