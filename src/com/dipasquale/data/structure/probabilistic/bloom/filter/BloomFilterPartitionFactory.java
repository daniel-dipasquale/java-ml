package com.dipasquale.data.structure.probabilistic.bloom.filter;

public interface BloomFilterPartitionFactory {
    int getMaximumHashFunctions();

    <T> BloomFilter<T> create(int index, int estimatedSize, int hashFunctions, double falsePositiveRatio, long size);

    static BloomFilterPartitionFactory create(final BloomFilterFactory bloomFilterFactory) {
        return new BloomFilterPartitionFactory() {
            @Override
            public int getMaximumHashFunctions() {
                return bloomFilterFactory.getMaximumHashFunctions();
            }

            @Override
            public <T> BloomFilter<T> create(final int index, final int estimatedSize, final int hashFunctions, final double falsePositiveRatio, final long size) {
                return bloomFilterFactory.create(estimatedSize, hashFunctions, falsePositiveRatio, size);
            }
        };
    }

    @FunctionalInterface
    interface Proxy {
        <T> BloomFilter<T> create(int index);

        static Proxy create(final BloomFilterPartitionFactory bloomFilterPartitionFactory, final int estimatedSize, final int hashFunctions, final double falsePositiveRatio, final long size) {
            return new Proxy() {
                @Override
                public <T> BloomFilter<T> create(final int index) {
                    return bloomFilterPartitionFactory.create(index, estimatedSize, hashFunctions, falsePositiveRatio, size);
                }
            };
        }
    }
}
