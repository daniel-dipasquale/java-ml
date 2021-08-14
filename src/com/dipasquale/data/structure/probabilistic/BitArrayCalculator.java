package com.dipasquale.data.structure.probabilistic;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

public final class BitArrayCalculator {
    @Generated
    private BitArrayCalculator() {
    }

    public static Result readjust(final int count, final int estimatedSize, final long size) {
        boolean isInBounds = size <= (long) Integer.MAX_VALUE;

        if (isInBounds && count == 1) {
            return new Result(1, estimatedSize, size);
        }

        if (isInBounds) {
            return new Result(count, estimatedSize, size);
        }

        double sizeDouble = (double) size;
        double partitions = Math.ceil(sizeDouble / (double) Integer.MAX_VALUE);
        int estimatedSizeFixed = (int) Math.ceil((double) estimatedSize / partitions);
        long sizeFixed = (long) Math.ceil(sizeDouble / partitions);
        int countFixed = (int) Math.ceil((double) count * partitions);

        return new Result(countFixed, estimatedSizeFixed, sizeFixed);
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
