package com.dipasquale.data.structure.probabilistic.bloom.filter.concurrent;

import com.dipasquale.common.bit.concurrent.SingleBitAtomicLongArrayBitManipulator;
import com.dipasquale.common.bit.int2.BitManipulator;
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
    private final BitManipulator bitManipulator;
    private final int hashingFunctions;

    private AtomicLongArrayBloomFilter(final HashingFunction hashingFunction, final AtomicLongArray array, final int hashingFunctions) {
        this.hashingFunction = hashingFunction;
        this.bitManipulator = new SingleBitAtomicLongArrayBitManipulator(array);
        this.hashingFunctions = hashingFunctions;
    }

    AtomicLongArrayBloomFilter(final HashingFunction hashingFunction, final int size, final int hashingFunctions) {
        this(hashingFunction, new AtomicLongArray(size), hashingFunctions);
    }

    private static long getNextHashCode(final long hashCode) {
        if (hashCode >= 0L) {
            return hashCode;
        }

        return hashCode >>> (1 + (int) (~hashCode % MAXIMUM_SHIFTS));
    }

    private BitSet selectOrUpdateData(final T item, final BitRetriever bitRetriever) {
        BitSet flags = new BitSet();
        int hashCode = item.hashCode();
        int hashFunctionIndex = Math.abs(hashCode) % hashingFunctions;
        long hashCodeMerged = hashingFunction.hashCode(hashCode, hashFunctionIndex);

        for (int i = 0; i < hashingFunctions; i++) {
            long hashCodeFixed = getNextHashCode(hashCodeMerged);
            long index = hashCodeFixed % bitManipulator.size();
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
        BitSet flags = selectOrUpdateData(item, index -> bitManipulator.extract(index) == 1L);

        return flags.cardinality() == hashingFunctions;
    }

    @Override
    public boolean add(final T item) {
        BitSet flags = selectOrUpdateData(item, index -> bitManipulator.getAndSet(index, 1L) == 1L);

        return flags.cardinality() < hashingFunctions;
    }

    @FunctionalInterface
    private interface BitRetriever {
        boolean get(long index);
    }
}
