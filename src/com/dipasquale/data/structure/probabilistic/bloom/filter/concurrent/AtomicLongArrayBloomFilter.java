package com.dipasquale.data.structure.probabilistic.bloom.filter.concurrent;

import com.dipasquale.common.bit.BitManipulator;
import com.dipasquale.common.bit.concurrent.SingleBitAtomicLongArrayBitManipulator;
import com.dipasquale.data.structure.probabilistic.HashingFunction;
import com.dipasquale.data.structure.probabilistic.bloom.filter.BloomFilter;

import java.io.Serial;
import java.io.Serializable;
import java.util.BitSet;
import java.util.concurrent.atomic.AtomicLongArray;

final class AtomicLongArrayBloomFilter<T> implements BloomFilter<T>, Serializable {
    @Serial
    private static final long serialVersionUID = 1962455033871627455L;
    private static final int MAXIMUM_SHIFTS = 32;
    private final HashingFunction hashingFunction;
    private final BitManipulator dataBitManipulator;
    private final int hashingFunctions;

    AtomicLongArrayBloomFilter(final HashingFunction hashingFunction, final int size, final int hashingFunctions) {
        AtomicLongArray array = new AtomicLongArray(size);

        this.hashingFunction = hashingFunction;
        this.dataBitManipulator = new SingleBitAtomicLongArrayBitManipulator(array);
        this.hashingFunctions = hashingFunctions;
    }

    private BitSet selectOrUpdateData(final T item, final BitRetriever bitRetriever) {
        BitSet flags = new BitSet();
        int hashCode = item.hashCode();
        int hashFunctionIndex = Math.abs(hashCode) % hashingFunctions;
        long hashCodeMerged = hashingFunction.hashCode(hashCode, hashFunctionIndex);

        for (int i = 0; i < hashingFunctions; i++) {
            long hashCodeFixed = hashCodeMerged >= 0L
                    ? hashCodeMerged
                    : hashCodeMerged >>> (1 + (int) (~hashCodeMerged % MAXIMUM_SHIFTS));

            long index = hashCodeFixed % dataBitManipulator.size();
            int shifts = 8 + (i + hashFunctionIndex) % MAXIMUM_SHIFTS;

            if (bitRetriever.get(index)) {
                flags.set(i);
            }

            hashCodeMerged = (hashCodeMerged >> MAXIMUM_SHIFTS) ^ (hashCodeMerged << shifts);
        }

        return flags;
    }

    @Override
    public boolean mightContain(final T item) {
        BitSet flags = selectOrUpdateData(item, i -> dataBitManipulator.extract(i) == 1L);

        return flags.cardinality() == hashingFunctions;
    }

    @Override
    public boolean add(final T item) {
        BitSet flags = selectOrUpdateData(item, i -> dataBitManipulator.getAndSet(i, 1L) == 1L);

        return flags.cardinality() < hashingFunctions;
    }

    @FunctionalInterface
    private interface BitRetriever {
        boolean get(long index);
    }
}
