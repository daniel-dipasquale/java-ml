package com.dipasquale.data.structure.probabilistic;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DataStructureMultiCalculator {
    @Getter
    private static final DataStructureMultiCalculator instance = new DataStructureMultiCalculator();

    public Result readjust(final int count, final int estimatedSize, final long size) {
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

    @Builder(access = AccessLevel.PACKAGE)
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    @EqualsAndHashCode
    @ToString
    public static final class Result {
        private final int count;
        private final int estimatedSize;
        private final long size;
    }
}
