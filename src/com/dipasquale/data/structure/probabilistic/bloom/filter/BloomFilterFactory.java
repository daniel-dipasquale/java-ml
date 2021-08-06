package com.dipasquale.data.structure.probabilistic.bloom.filter;

import com.dipasquale.common.ArgumentValidatorSupport;
import com.dipasquale.common.bit.BitManipulatorSupport;
import com.dipasquale.common.factory.ObjectFactory;

import java.io.Serializable;

public interface BloomFilterFactory { // TODO: consider enclosing parameters to single object
    int getHashingFunctionCount();

    default int getSizePerRecord() {
        return BitManipulatorSupport.MAXIMUM_BITS;
    }

    <T> BloomFilter<T> create(int estimatedSize, int hashingFunctionCount, double falsePositiveRatio, long size);

    default <T> ObjectFactory<BloomFilter<T>> createProxy(final int estimatedSize, final int hashingFunctionCount, final double falsePositiveRatio, final long size) {
        return (ObjectFactory<BloomFilter<T>> & Serializable) () -> create(estimatedSize, hashingFunctionCount, falsePositiveRatio, size);
    }

    private static long calculateSize(final int estimatedSize, final int hashingFunctionCount, final double falsePositiveRatio) {
        double sizeStep1 = (double) -hashingFunctionCount / Math.log(1D - Math.exp(Math.log(falsePositiveRatio) / (double) hashingFunctionCount));
        double sizeStep2 = (double) estimatedSize * sizeStep1;

        return (long) Math.ceil(sizeStep2); // NOTE: based on: https://hur.st/bloomfilter
    }

    private static long calculateSize(final int estimatedSize, final double falsePositiveRatio) {
        double sizeStep1 = (double) estimatedSize * Math.log(falsePositiveRatio) / Math.log(1D / Math.pow(2D, Math.log(2D)));

        return (long) Math.ceil(sizeStep1);
    }

    private <T> BloomFilter<T> createEstimatedIfValid(final int estimatedSize, final int hashingFunctionCount, final double falsePositiveRatio, final long size) {
        ArgumentValidatorSupport.ensureGreaterThanZero(estimatedSize, "estimatedSize");
        ArgumentValidatorSupport.ensureGreaterThanZero(hashingFunctionCount, "hashingFunctionCount");
        ArgumentValidatorSupport.ensureLessThanOrEqualTo(hashingFunctionCount, getHashingFunctionCount(), "hashingFunctionCount");
        ArgumentValidatorSupport.ensureGreaterThanOrEqualToZero(falsePositiveRatio, "falsePositiveRatio");
        ArgumentValidatorSupport.ensureLessThanOrEqualTo(falsePositiveRatio, 1D, "falsePositiveRatio");
        ArgumentValidatorSupport.ensureGreaterThanZero(size, "size", String.format("estimatedSize '%d', hashingFunctionCount '%d' and falsePositiveRatio '%f' yields a size lesser than 0", estimatedSize, hashingFunctionCount, falsePositiveRatio));

        double sizeStep1 = (double) size / (double) getSizePerRecord();
        long sizeFixed = (long) Math.ceil(sizeStep1);

        return create(estimatedSize, hashingFunctionCount, falsePositiveRatio, sizeFixed);
    }

    default <T> BloomFilter<T> createEstimated(final int estimatedSize, final int hashingFunctionCount, final double falsePositiveRatio) {
        long size = calculateSize(estimatedSize, hashingFunctionCount, falsePositiveRatio);

        return createEstimatedIfValid(estimatedSize, hashingFunctionCount, falsePositiveRatio, size);
    }

    default <T> BloomFilter<T> createEstimated(final int estimatedSize, final int hashingFunctionCount) {
        return createEstimated(estimatedSize, hashingFunctionCount, Math.pow(estimatedSize + 1, -1D));
    }

    default <T> BloomFilter<T> createEstimated(final int estimatedSize, final double falsePositiveRatio) {
        long size = calculateSize(estimatedSize, falsePositiveRatio);
        double sizeByEstimated = (double) size / (double) estimatedSize;
        int hashingFunctionCount = (int) Math.round(Math.log(2D) * sizeByEstimated);

        return createEstimatedIfValid(estimatedSize, hashingFunctionCount, falsePositiveRatio, size);
    }

    default <T> BloomFilter<T> createEstimated(final int estimatedSize) {
        return createEstimated(estimatedSize, Math.pow(estimatedSize + 1, -1D));
    }
}
