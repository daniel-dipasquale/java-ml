package com.dipasquale.data.structure.probabilistic.count.min.sketch;

import com.dipasquale.data.structure.probabilistic.bloom.filter.BloomFilter;
import com.dipasquale.data.structure.probabilistic.bloom.filter.BloomFilterFactory;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class CountMinSketchFactoryAdapter {
    private static final CountMinSketchFactoryAdapter INSTANCE = new CountMinSketchFactoryAdapter();

    public static CountMinSketchFactoryAdapter getInstance() {
        return INSTANCE;
    }

    private static <T> CountMinSketch<T> extract(final BloomFilter<T> bloomFilter) {
        return ((BloomFilterAdapter<T>) bloomFilter).countMinSketch;
    }

    public <T> CountMinSketch<T> createEstimated(final CountMinSketchFactory countMinSketchFactory, final int estimatedSize, final int hashingFunctionCount, final double falsePositiveRatio, final int bits) {
        BloomFilterFactoryAdapter bloomFilterFactory = new BloomFilterFactoryAdapter(countMinSketchFactory, hashingFunctionCount, bits);

        return extract(bloomFilterFactory.createEstimated(estimatedSize, 1, falsePositiveRatio));
    }

    public <T> CountMinSketch<T> createEstimated(final CountMinSketchFactory countMinSketchFactory, final int estimatedSize, final int hashingFunctionCount, final int bits) {
        BloomFilterFactoryAdapter bloomFilterFactory = new BloomFilterFactoryAdapter(countMinSketchFactory, hashingFunctionCount, bits);

        return extract(bloomFilterFactory.createEstimated(estimatedSize, 1));
    }

    public <T> CountMinSketch<T> createEstimated(final CountMinSketchFactory countMinSketchFactory, final int estimatedSize, final double falsePositiveRatio, final int bits) {
        BloomFilterFactoryAdapter bloomFilterFactory = new BloomFilterFactoryAdapter(countMinSketchFactory, 0, bits);

        return extract(bloomFilterFactory.createEstimated(estimatedSize, 1, falsePositiveRatio));
    }

    public <T> CountMinSketch<T> createEstimated(final CountMinSketchFactory countMinSketchFactory, final int estimatedSize, final int bits) {
        BloomFilterFactory bloomFilterFactory = new BloomFilterFactoryAdapter(countMinSketchFactory, 0, bits);

        return extract(bloomFilterFactory.createEstimated(estimatedSize, 1));
    }

    @RequiredArgsConstructor
    private static final class BloomFilterAdapter<T> implements BloomFilter<T> {
        private final CountMinSketch<T> countMinSketch;

        @Override
        public boolean mightContain(final T item) {
            return countMinSketch.mightContain(item);
        }

        @Override
        public boolean add(final T item) {
            return countMinSketch.put(item) > 0L;
        }
    }

    @RequiredArgsConstructor
    private static final class BloomFilterFactoryAdapter implements BloomFilterFactory {
        private final CountMinSketchFactory countMinSketchFactory;
        private final int hashingFunctionCountDesired;
        private final int bits;

        @Override
        public int getHashingFunctionCount() {
            return countMinSketchFactory.getHashingFunctionCount();
        }

        @Override
        public int getSizePerRecord() {
            return Math.floorDiv(BloomFilterFactory.super.getSizePerRecord(), bits);
        }

        private int getHashingFunctionCount(final int estimatedSize, final long size) {
            if (hashingFunctionCountDesired > 0) {
                return hashingFunctionCountDesired;
            }

            return (int) Math.ceil(((double) size / estimatedSize) * Math.log(2D));
        }

        @Override
        public <T> BloomFilter<T> create(final int estimatedSize, final int hashingFunctionCount, final double falsePositiveRatio, final long size) {
            int hashingFunctionCountFixed = getHashingFunctionCount(estimatedSize, size);
            CountMinSketch<T> countMinSketch = countMinSketchFactory.create(estimatedSize, hashingFunctionCountFixed, falsePositiveRatio, size, bits);

            return new BloomFilterAdapter<>(countMinSketch);
        }
    }
}
