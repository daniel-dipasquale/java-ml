package com.dipasquale.data.structure.probabilistic;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BitArrayCalculator {
    private static final long MAXIMUM_VALUE = Integer.MAX_VALUE;

    private static int calculateEstimatedSize(final long estimatedSize, final long count) {
        long estimatedSizeFixed = (estimatedSize + count - 1L) / count;

        return (int) estimatedSizeFixed;
    }

    private static long calculateSize(final long size, final long count) {
        return (size + count - 1L) / count;
    }

    public static Result readjust(final int count, final int estimatedSize, final long size) {
        boolean inBounds = size <= MAXIMUM_VALUE;

        if (inBounds && count == 1) {
            return new Result(1, estimatedSize, size);
        }

        if (inBounds) {
            int estimatedSizeFixed = calculateEstimatedSize(estimatedSize, count);
            long sizeFixed = calculateSize(size, count);

            return new Result(count, estimatedSizeFixed, sizeFixed);
        }

        long partitions = (size + MAXIMUM_VALUE - 1L) / MAXIMUM_VALUE;
        long countFixed = (long) count * partitions;
        int estimatedSizeFixed = calculateEstimatedSize(estimatedSize, countFixed);
        long sizeFixed = calculateSize(size, countFixed);

        return new Result((int) countFixed, estimatedSizeFixed, sizeFixed);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder(access = AccessLevel.PACKAGE)
    @Getter
    @EqualsAndHashCode
    @ToString
    public static final class Result {
        private final int count;
        private final int estimatedSize;
        private final long size;
    }
}
