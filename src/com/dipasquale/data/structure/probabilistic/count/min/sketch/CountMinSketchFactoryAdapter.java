/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.data.structure.probabilistic.count.min.sketch;

import com.dipasquale.data.structure.probabilistic.bloom.filter.BloomFilter;
import com.dipasquale.data.structure.probabilistic.bloom.filter.BloomFilterFactory;
import lombok.AccessLevel;
import lombok.Generated;
import lombok.RequiredArgsConstructor;

final class CountMinSketchFactoryAdapter {
    @Generated
    private CountMinSketchFactoryAdapter() {
    }

    private static <T> CountMinSketch<T> extract(final BloomFilter<T> bloomFilter) {
        return ((BloomFilterAdapter<T>) bloomFilter).countMinSketch;
    }

    public static <T> CountMinSketch<T> createEstimated(final CountMinSketchFactory countMinSketchFactory, final int estimatedSize, final int hashingFunctions, final double falsePositiveRatio, final int bitsForCounter) {
        BloomFilterFactoryAdapter bloomFilterFactory = new BloomFilterFactoryAdapter(countMinSketchFactory, hashingFunctions, bitsForCounter);

        return extract(bloomFilterFactory.createEstimated(estimatedSize, 1, falsePositiveRatio));
    }

    public static <T> CountMinSketch<T> createEstimated(final CountMinSketchFactory countMinSketchFactory, final int estimatedSize, final int hashingFunctions, final int bitsForCounter) {
        BloomFilterFactoryAdapter bloomFilterFactory = new BloomFilterFactoryAdapter(countMinSketchFactory, hashingFunctions, bitsForCounter);

        return extract(bloomFilterFactory.createEstimated(estimatedSize, 1));
    }

    public static <T> CountMinSketch<T> createEstimated(final CountMinSketchFactory countMinSketchFactory, final int estimatedSize, final double falsePositiveRatio, final int bitsForCounter) {
        BloomFilterFactoryAdapter bloomFilterFactory = new BloomFilterFactoryAdapter(countMinSketchFactory, 0, bitsForCounter);

        return extract(bloomFilterFactory.createEstimated(estimatedSize, 1, falsePositiveRatio));
    }

    public static <T> CountMinSketch<T> createEstimated(final CountMinSketchFactory countMinSketchFactory, final int estimatedSize, final int bitsForCounter) {
        BloomFilterFactory bloomFilterFactory = new BloomFilterFactoryAdapter(countMinSketchFactory, 0, bitsForCounter);

        return extract(bloomFilterFactory.createEstimated(estimatedSize, 1));
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
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

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class BloomFilterFactoryAdapter implements BloomFilterFactory {
        private final CountMinSketchFactory countMinSketchFactory;
        private final int hashingFunctionsDesired;
        private final int bitsForCounter;

        @Override
        public int getSizePerRecord() {
            return Math.floorDiv(BloomFilterFactory.super.getSizePerRecord(), bitsForCounter);
        }

        private int getHashingFunctions(final int estimatedSize, final long size) {
            if (hashingFunctionsDesired > 0) {
                return hashingFunctionsDesired;
            }

            double sizeFixed = (double) size;

            return (int) Math.ceil(sizeFixed / estimatedSize * Math.log(2D));
        }

        @Override
        public <T> BloomFilter<T> create(final int estimatedSize, final int hashingFunctions, final double falsePositiveRatio, final long size) {
            int hashingFunctionsFixed = getHashingFunctions(estimatedSize, size);
            CountMinSketch<T> countMinSketch = countMinSketchFactory.create(estimatedSize, hashingFunctionsFixed, falsePositiveRatio, size, bitsForCounter);

            return new BloomFilterAdapter<>(countMinSketch);
        }
    }
}
