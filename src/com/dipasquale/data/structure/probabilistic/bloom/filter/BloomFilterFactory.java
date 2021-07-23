package com.dipasquale.data.structure.probabilistic.bloom.filter;

import com.dipasquale.common.ArgumentValidatorSupport;
import com.dipasquale.common.BitManipulatorSupport;
import com.dipasquale.common.ObjectFactory;

public interface BloomFilterFactory { // TODO: enclose parameters to single object
    int getMaximumHashFunctions();

    default int getSizePerRecord() {
        return BitManipulatorSupport.MAXIMUM_BITS;
    }

    <T> BloomFilter<T> create(int estimatedSize, int hashFunctions, double falsePositiveRatio, long size);

    default <T> ObjectFactory<BloomFilter<T>> createProxy(final int estimatedSize, final int hashFunctions, final double falsePositiveRatio, final long size) {
        return () -> create(estimatedSize, hashFunctions, falsePositiveRatio, size);
    }

    private static long calculateSize(final int estimatedSize, final int hashFunctions, final double falsePositiveRatio) {
        double sizeStep1 = (double) -hashFunctions / Math.log(1D - Math.exp(Math.log(falsePositiveRatio) / (double) hashFunctions));
        double sizeStep2 = (double) estimatedSize * sizeStep1;

        return (long) Math.ceil(sizeStep2); // NOTE: based on: https://hur.st/bloomfilter
    }

    private static long calculateSize(final int estimatedSize, final double falsePositiveRatio) {
        double sizeStep1 = (double) estimatedSize * Math.log(falsePositiveRatio) / Math.log(1D / Math.pow(2D, Math.log(2D)));

        return (long) Math.ceil(sizeStep1);
    }

    private <T> BloomFilter<T> createEstimatedIfValid(final int estimatedSize, final int hashFunctions, final double falsePositiveRatio, final long size) {
        ArgumentValidatorSupport.ensureGreaterThanZero(estimatedSize, "estimatedSize");
        ArgumentValidatorSupport.ensureGreaterThanZero(hashFunctions, "hashFunctions");
        ArgumentValidatorSupport.ensureLessThanOrEqualTo(hashFunctions, getMaximumHashFunctions(), "hashFunctions");
        ArgumentValidatorSupport.ensureGreaterThanOrEqualToZero(falsePositiveRatio, "falsePositiveRatio");
        ArgumentValidatorSupport.ensureLessThanOrEqualTo(falsePositiveRatio, 1D, "falsePositiveRatio");
        ArgumentValidatorSupport.ensureGreaterThanZero(size, "size", String.format("estimatedSize '%d', hashFunctions '%d' and falsePositiveRatio '%f' yields a size lesser than 0", estimatedSize, hashFunctions, falsePositiveRatio));

        double sizeStep1 = (double) size / (double) getSizePerRecord();
        long sizeFixed = (long) Math.ceil(sizeStep1);

        return create(estimatedSize, hashFunctions, falsePositiveRatio, sizeFixed);
    }

    default <T> BloomFilter<T> createEstimated(final int estimatedSize, final int hashFunctions, final double falsePositiveRatio) {
        long size = calculateSize(estimatedSize, hashFunctions, falsePositiveRatio);

        return createEstimatedIfValid(estimatedSize, hashFunctions, falsePositiveRatio, size);
    }

    default <T> BloomFilter<T> createEstimated(final int estimatedSize, final int hashFunctions) {
        return createEstimated(estimatedSize, hashFunctions, Math.pow(estimatedSize + 1, -1D));
    }

    default <T> BloomFilter<T> createEstimated(final int estimatedSize, final double falsePositiveRatio) {
        long size = calculateSize(estimatedSize, falsePositiveRatio);
        double sizeByEstimated = (double) size / (double) estimatedSize;
        int hashFunctions = (int) Math.round(Math.log(2D) * sizeByEstimated);

        return createEstimatedIfValid(estimatedSize, hashFunctions, falsePositiveRatio, size);
    }

    default <T> BloomFilter<T> createEstimated(final int estimatedSize) {
        return createEstimated(estimatedSize, Math.pow(estimatedSize + 1, -1D));
    }
}
