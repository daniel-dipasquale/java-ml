package com.dipasquale.data.structure.probabilistic.count.min.sketch;

import com.dipasquale.common.ArgumentValidatorUtils;
import com.dipasquale.common.ExpirySupport;
import com.dipasquale.threading.event.loop.EventLoop;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public final class HeavyHittersConfig<T> {
    private final ExpirySupport expirySupport;
    private final HeavyHittersCollector<T> collector;
    private final int topLimit;
    private final EventLoop eventLoop;
    private final int partitions;
    private final List<AggregateConfig<T>> aggregates;

    public interface AggregatePredicate<T> {
        boolean shouldRecycle(CountMinSketch<T> countMinSketch, long recycledDateTime, Set<T> heavyHitters, long minimumCount);

        void confirmRecycled(CountMinSketch<T> countMinSketch, long recycledDateTime, Set<T> heavyHitters, long minimumCount);

        default AggregatePredicate<T> orElse(final AggregatePredicate<T> aggregatePredicate) {
            return new AggregatePredicate<>() {
                @Override
                public boolean shouldRecycle(final CountMinSketch<T> countMinSketch, final long recycledDateTime, final Set<T> heavyHitters, final long minimumCount) {
                    return AggregatePredicate.this.shouldRecycle(countMinSketch, recycledDateTime, heavyHitters, minimumCount) || aggregatePredicate.shouldRecycle(countMinSketch, recycledDateTime, heavyHitters, minimumCount);
                }

                @Override
                public void confirmRecycled(final CountMinSketch<T> countMinSketch, final long recycledDateTime, final Set<T> heavyHitters, final long minimumCount) {
                    AggregatePredicate.this.confirmRecycled(countMinSketch, recycledDateTime, heavyHitters, minimumCount);
                    aggregatePredicate.confirmRecycled(countMinSketch, recycledDateTime, heavyHitters, minimumCount);
                }
            };
        }

        default AggregatePredicate<T> andAlso(final AggregatePredicate<T> aggregatePredicate) {
            return new AggregatePredicate<>() {
                @Override
                public boolean shouldRecycle(final CountMinSketch<T> countMinSketch, final long recycledDateTime, final Set<T> heavyHitters, final long minimumCount) {
                    return AggregatePredicate.this.shouldRecycle(countMinSketch, recycledDateTime, heavyHitters, minimumCount) && aggregatePredicate.shouldRecycle(countMinSketch, recycledDateTime, heavyHitters, minimumCount);
                }

                @Override
                public void confirmRecycled(final CountMinSketch<T> countMinSketch, final long recycledDateTime, final Set<T> heavyHitters, final long minimumCount) {
                    AggregatePredicate.this.confirmRecycled(countMinSketch, recycledDateTime, heavyHitters, minimumCount);
                    aggregatePredicate.confirmRecycled(countMinSketch, recycledDateTime, heavyHitters, minimumCount);
                }
            };
        }

        static <T> AggregatePredicate<T> afterAggregating(final int times) {
            ArgumentValidatorUtils.ensureGreaterThanZero(times, "times");

            int[] count = new int[1];

            return new AggregatePredicate<>() {
                @Override
                public boolean shouldRecycle(final CountMinSketch<T> countMinSketch, final long recycledDateTime, final Set<T> heavyHitters, final long minimumCount) {
                    return ++count[0] == times;
                }

                @Override
                public void confirmRecycled(final CountMinSketch<T> countMinSketch, final long recycledDateTime, final Set<T> heavyHitters, final long minimumCount) {
                    count[0] = 0;
                }
            };
        }

        static <T> AggregatePredicate<T> afterEvery(final long expiryTime) {
            ArgumentValidatorUtils.ensureGreaterThanZero(expiryTime, "expiryTime");

            long[] lastRecycledDateTime = new long[]{Long.MIN_VALUE};

            return new AggregatePredicate<>() {
                @Override
                public boolean shouldRecycle(final CountMinSketch<T> countMinSketch, final long recycledDateTime, final Set<T> heavyHitters, final long minimumCount) {
                    if (lastRecycledDateTime[0] == Long.MIN_VALUE) {
                        lastRecycledDateTime[0] = recycledDateTime;

                        return false;
                    }

                    return recycledDateTime - lastRecycledDateTime[0] >= expiryTime;
                }

                @Override
                public void confirmRecycled(final CountMinSketch<T> countMinSketch, final long recycledDateTime, final Set<T> heavyHitters, final long minimumCount) {
                    lastRecycledDateTime[0] = recycledDateTime;
                }
            };
        }

        static <T> AggregatePredicate<T> afterMinimumCountOf(final long minimumCount) {
            ArgumentValidatorUtils.ensureGreaterThanZero(minimumCount, "minimumCount");

            return new AggregatePredicate<>() {
                @Override
                public boolean shouldRecycle(final CountMinSketch<T> countMinSketch, final long recycledDateTime, final Set<T> heavyHitters, final long _minimumCount) {
                    return minimumCount < _minimumCount;
                }

                @Override
                public void confirmRecycled(final CountMinSketch<T> countMinSketch, final long recycledDateTime, final Set<T> heavyHitters, final long _minimumCount) {
                }
            };
        }
    }

    @FunctionalInterface
    public interface RecyclableProxyFactory {
        <T> CountMinSketch<T> create(RecycledCollector<T> recycledCollector);
    }

    @FunctionalInterface
    public interface HeavyHittersProxyFactory {
        <T> CountMinSketch<T> create(HeavyHittersCollector<T> heavyHittersCollector);
    }

    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
    @Builder
    @Getter
    public static final class AggregateConfig<T> {
        private final Integer estimatedSizeOverride;
        private final Integer hashFunctionsOverride;
        private final Integer bitsOverride;
        private final AggregatePredicate<T> flushPredicate;
    }
}
