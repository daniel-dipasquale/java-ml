package com.dipasquale.data.structure.probabilistic.count.min.sketch;

import com.dipasquale.common.ArgumentValidatorSupport;
import com.dipasquale.common.factory.ObjectFactory;
import com.dipasquale.data.structure.probabilistic.MultiHashingFunction;
import lombok.Builder;

import java.util.Optional;

final class CountMinSketchHeavyHittersTieredFactory implements CountMinSketchFactory {
    private final CountMinSketchDefaultFactory countMinSketchDefaultFactory;
    private final HeavyHittersConfig<?> countMinSketchHeavyHittersConfig;

    CountMinSketchHeavyHittersTieredFactory(final CountMinSketchDefaultFactory countMinSketchDefaultFactory, final HeavyHittersConfig<?> countMinSketchHeavyHittersConfig) {
        ArgumentValidatorSupport.ensureNotNull(countMinSketchHeavyHittersConfig.getExpirationFactory(), "countMinSketchHeavyHittersConfig.expirationSupport");
        ArgumentValidatorSupport.ensureNotNull(countMinSketchHeavyHittersConfig.getCollector(), "countMinSketchHeavyHittersConfig.collector");
        ArgumentValidatorSupport.ensureGreaterThanZero(countMinSketchHeavyHittersConfig.getTopLimit(), "countMinSketchHeavyHittersConfig.topLimit");
        ArgumentValidatorSupport.ensureGreaterThanZero(countMinSketchHeavyHittersConfig.getPartitions(), "countMinSketchHeavyHittersConfig.partitions");
        ArgumentValidatorSupport.ensureTrue(areAllFlushPredicatesAvailable(countMinSketchHeavyHittersConfig), "countMinSketchHeavyHittersConfig.aggregates[...].flushPredicate", "cannot be null");
        this.countMinSketchDefaultFactory = countMinSketchDefaultFactory;
        this.countMinSketchHeavyHittersConfig = countMinSketchHeavyHittersConfig;
    }

    CountMinSketchHeavyHittersTieredFactory(final MultiHashingFunction multiHashingFunction, final HeavyHittersConfig<?> countMinSketchHeavyHittersConfig) {
        this(new CountMinSketchDefaultFactory(multiHashingFunction), countMinSketchHeavyHittersConfig);
    }

    private static boolean areAllFlushPredicatesAvailable(final HeavyHittersConfig<?> countMinSketchHeavyHittersConfig) {
        return countMinSketchHeavyHittersConfig.getAggregates().stream()
                .allMatch(a -> a.getFlushPredicate() != null);
    }

    @Override
    public int getHashingFunctionCount() {
        return countMinSketchDefaultFactory.getHashingFunctionCount();
    }

    private static <T> T ensureType(final Object object) {
        return (T) object;
    }

    private <T> HeavyHittersConfig.HeavyHittersProxyFactory createSingleHeavyHittersProxyFactory(final HeavyHittersConfig<T> config, final Params params) {
        HeavyHittersConfig.RecyclableProxyFactory recyclableProxyFactory = new HeavyHittersConfig.RecyclableProxyFactory() {
            @Override
            public <R> CountMinSketch<R> create(final RecycledCollector<R> recycledCollector) {
                CountMinSketchTimedRecyclableFactory countMinSketchTimedRecyclableFactory = new CountMinSketchTimedRecyclableFactory(countMinSketchDefaultFactory, countMinSketchHeavyHittersConfig.getExpirationFactory(), recycledCollector);

                return countMinSketchTimedRecyclableFactory.create(params.estimatedSize, params.hashingFunctionCount, params.falsePositiveRatio, params.size, params.bits);
            }
        };

        return new HeavyHittersConfig.HeavyHittersProxyFactory() {
            @Override
            public <R> CountMinSketch<R> create(final HeavyHittersCollector<R> heavyHittersCollector) {
                return new CountMinSketchHeavyHitters<>(recyclableProxyFactory, ensureType(heavyHittersCollector), config.getTopLimit(), config.getEventLoop());
            }
        };
    }

    private <T> HeavyHittersConfig.HeavyHittersProxyFactory createMultiHeavyHittersProxyFactory(final HeavyHittersConfig<T> config, final int partitions, final Params params) {
        return new HeavyHittersConfig.HeavyHittersProxyFactory() {
            @Override
            public <R> CountMinSketch<R> create(final HeavyHittersCollector<R> heavyHittersCollector) {
                CountMinSketchPartitionFactory partitionFactory = new CountMinSketchPartitionFactory() {
                    @Override
                    public int getHashingFunctionCount() {
                        return countMinSketchDefaultFactory.getHashingFunctionCount();
                    }

                    @Override
                    public <RT> CountMinSketch<RT> create(final int index, final int estimatedSize, final int hashingFunctionCount, final double falsePositiveRatio, final long size, final int bits) {
                        Params params = Params.create(estimatedSize, hashingFunctionCount, falsePositiveRatio, size, bits);
                        HeavyHittersConfig.HeavyHittersProxyFactory heavyHittersProxyFactory = createSingleHeavyHittersProxyFactory(config, params);

                        return ensureType(heavyHittersProxyFactory.create(heavyHittersCollector));
                    }
                };

                CountMinSketchMultiFactory countMinSketchMultiFactory = new CountMinSketchMultiFactory(partitionFactory, partitions);

                return countMinSketchMultiFactory.create(params.estimatedSize, params.hashingFunctionCount, params.falsePositiveRatio, params.size, params.bits);
            }
        };
    }

    private <T> CountMinSketchFactory createAggregatedHeavyHittersFactory(final HeavyHittersConfig<T> config, final int aggregateIndex, final HeavyHittersConfig.AggregateConfig<T> aggregateConfig) {
        return new CountMinSketchFactory() {
            @Override
            public int getHashingFunctionCount() {
                return countMinSketchDefaultFactory.getHashingFunctionCount();
            }

            @Override
            public <R> CountMinSketch<R> create(final int estimatedSize, final int hashingFunctionCount, final double falsePositiveRatio, final long size, final int bits) {
                int estimatedSizeOverridden = Optional.ofNullable(aggregateConfig.getEstimatedSizeOverride())
                        .orElse(config.getTopLimit() * (aggregateIndex + 2));

                int hashingFunctionCountOverridden = Optional.ofNullable(aggregateConfig.getHashingFunctionCountOverride())
                        .orElse(hashingFunctionCount);

                int bitsOverridden = Optional.ofNullable(aggregateConfig.getBitsOverride())
                        .orElse(bits + aggregateIndex + 1);

                return countMinSketchDefaultFactory.create(estimatedSizeOverridden, hashingFunctionCountOverridden, falsePositiveRatio, size, bitsOverridden);
            }
        };
    }

    private <T> HeavyHittersConfig.HeavyHittersProxyFactory createHeavyHittersTieredProxyFactory(final HeavyHittersConfig.HeavyHittersProxyFactory heavyHittersProxyFactory, final HeavyHittersConfig<T> config, final int aggregateIndex, final HeavyHittersConfig.AggregateConfig<T> aggregateConfig, final Params params) {
        return new HeavyHittersConfig.HeavyHittersProxyFactory() {
            @Override
            public <R> CountMinSketch<R> create(final HeavyHittersCollector<R> heavyHittersCollector) {
                CountMinSketchFactory aggregatedCountMinSketchFactory = createAggregatedHeavyHittersFactory(config, aggregateIndex, aggregateConfig);
                ObjectFactory<CountMinSketch<R>> aggregatedCountMinSketchFactoryProxy = aggregatedCountMinSketchFactory.createProxy(params.estimatedSize, params.hashingFunctionCount, params.falsePositiveRatio, params.size, params.bits);

                return new CountMinSketchHeavyHittersTiered<>(aggregatedCountMinSketchFactoryProxy, heavyHittersProxyFactory, heavyHittersCollector, ensureType(aggregateConfig.getFlushPredicate()), config.getEventLoop());
            }
        };
    }

    @Override
    public <T> CountMinSketch<T> create(final int estimatedSize, final int hashingFunctionCount, final double falsePositiveRatio, final long size, final int bits) {
        HeavyHittersConfig<T> config = ensureType(countMinSketchHeavyHittersConfig);
        Params params = Params.create(estimatedSize, hashingFunctionCount, falsePositiveRatio, size, bits);

        HeavyHittersConfig.HeavyHittersProxyFactory heavyHittersProxyFactory = config.getPartitions() == 1
                ? createSingleHeavyHittersProxyFactory(config, params)
                : createMultiHeavyHittersProxyFactory(config, config.getPartitions(), params);

        for (int i = 0, c = config.getAggregates().size(); i < c; i++) {
            HeavyHittersConfig.AggregateConfig<T> aggregateConfig = config.getAggregates().get(i);

            heavyHittersProxyFactory = createHeavyHittersTieredProxyFactory(heavyHittersProxyFactory, config, i, aggregateConfig, params);
        }

        return heavyHittersProxyFactory.create(config.getCollector());
    }

    @Builder
    private static final class Params {
        private final int estimatedSize;
        private final int hashingFunctionCount;
        private final double falsePositiveRatio;
        private final long size;
        private final int bits;

        public static Params create(final int estimatedSize, final int hashingFunctionCount, final double falsePositiveRatio, final long size, final int bits) {
            return Params.builder()
                    .estimatedSize(estimatedSize)
                    .hashingFunctionCount(hashingFunctionCount)
                    .falsePositiveRatio(falsePositiveRatio)
                    .size(size)
                    .size(bits)
                    .build();
        }
    }
}
