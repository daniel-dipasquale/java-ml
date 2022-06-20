package com.dipasquale.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Comparator;
import java.util.function.Supplier;

@RequiredArgsConstructor
@Getter
public final class OptimalPairScouter<TRanking extends Comparable<TRanking>, TValue> {
    private final Comparator<TRanking> comparator;
    private TRanking ranking = null;
    private TValue value = null;
    private int replacedCount;

    private void replace(final TRanking newRanking, final TValue newValue) {
        ranking = newRanking;
        value = newValue;
        replacedCount++;
    }

    public boolean replaceIfHigherRanking(final TRanking candidateRanking, final TValue candidateValue) {
        if (ranking != null && comparator.compare(candidateRanking, ranking) <= 0) {
            return false;
        }

        replace(candidateRanking, candidateValue);

        return true;
    }

    public boolean computeIfHigherRanking(final TRanking candidateRanking, final Supplier<TValue> candidateValueSupplier) {
        if (ranking != null && comparator.compare(candidateRanking, ranking) <= 0) {
            return false;
        }

        replace(candidateRanking, candidateValueSupplier.get());

        return true;
    }
}
