package com.dipasquale.data.structure.probabilistic.bloom.filter;

import com.dipasquale.common.ArgumentValidatorSupport;

public interface BloomFilterFactory {
    default int getSizePerRecord() {
        return 64;
    }

    <T> BloomFilter<T> create(int estimatedSize, int hashingFunctions, double falsePositiveRatio, long size);

    private static long calculateSize(final int estimatedSize, final int hashingFunctions, final double falsePositiveRatio) {
        double size1 = (double) -hashingFunctions / Math.log(1D - Math.exp(Math.log(falsePositiveRatio) / (double) hashingFunctions));
        double size2 = (double) estimatedSize * size1;

        return (long) Math.ceil(size2); // NOTE: based on: https://hur.st/bloomfilter
    }

    private static long calculateSize(final int estimatedSize, final double falsePositiveRatio) {
        double size1 = (double) estimatedSize * Math.log(falsePositiveRatio) / Math.log(1D / Math.pow(2D, Math.log(2D)));

        return (long) Math.ceil(size1);
    }

    private <T> BloomFilter<T> createEstimatedIfValid(final int estimatedSize, final int hashingFunctions, final double falsePositiveRatio, final long size) {
        ArgumentValidatorSupport.ensureGreaterThanZero(estimatedSize, "estimatedSize");
        ArgumentValidatorSupport.ensureGreaterThanZero(hashingFunctions, "hashingFunctions");
        ArgumentValidatorSupport.ensureGreaterThanOrEqualToZero(falsePositiveRatio, "falsePositiveRatio");
        ArgumentValidatorSupport.ensureLessThanOrEqualTo(falsePositiveRatio, 1D, "falsePositiveRatio");
        ArgumentValidatorSupport.ensureGreaterThanZero(size, "size", String.format("estimatedSize '%d', hashingFunctions '%d' and falsePositiveRatio '%f' yields a size lesser than 0", estimatedSize, hashingFunctions, falsePositiveRatio));

        long sizeFixed = (size + getSizePerRecord() - 1) / getSizePerRecord();

        return create(estimatedSize, hashingFunctions, falsePositiveRatio, sizeFixed);
    }

    default <T> BloomFilter<T> createEstimated(final int estimatedSize, final int hashingFunctions, final double falsePositiveRatio) {
        long size = calculateSize(estimatedSize, hashingFunctions, falsePositiveRatio);

        return createEstimatedIfValid(estimatedSize, hashingFunctions, falsePositiveRatio, size);
    }

    default <T> BloomFilter<T> createEstimated(final int estimatedSize, final int hashingFunctions) {
        return createEstimated(estimatedSize, hashingFunctions, Math.pow(estimatedSize + 1, -1D));
    }

    default <T> BloomFilter<T> createEstimated(final int estimatedSize, final double falsePositiveRatio) {
        long size = calculateSize(estimatedSize, falsePositiveRatio);
        double sizeByEstimated = (double) size / (double) estimatedSize;
        int hashingFunctions = (int) Math.round(Math.log(2D) * sizeByEstimated);

        return createEstimatedIfValid(estimatedSize, hashingFunctions, falsePositiveRatio, size);
    }

    default <T> BloomFilter<T> createEstimated(final int estimatedSize) {
        return createEstimated(estimatedSize, Math.pow(estimatedSize + 1, -1D));
    }
}
