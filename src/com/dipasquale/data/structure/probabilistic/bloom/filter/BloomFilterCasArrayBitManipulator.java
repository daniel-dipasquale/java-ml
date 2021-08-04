package com.dipasquale.data.structure.probabilistic.bloom.filter;

import com.dipasquale.common.bit.BitManipulator;
import com.dipasquale.common.bit.concurrent.SingleBitAtomicLongArrayBitManipulator;
import com.dipasquale.data.structure.probabilistic.MultiFunctionHashing;

import java.util.BitSet;
import java.util.concurrent.atomic.AtomicLongArray;

final class BloomFilterCasArrayBitManipulator<T> implements BloomFilter<T> {
    private static final int MAXIMUM_SHIFTS = 32;
    private final MultiFunctionHashing multiFunctionHashing;
    private final BitManipulator dataBitManipulator;
    private final int hashFunctions;

    BloomFilterCasArrayBitManipulator(final MultiFunctionHashing multiFunctionHashing, final int size, final int hashFunctions) {
        AtomicLongArray array = new AtomicLongArray(size);

        this.multiFunctionHashing = multiFunctionHashing;
        this.dataBitManipulator = new SingleBitAtomicLongArrayBitManipulator(array);
        this.hashFunctions = hashFunctions;
    }

    private BitSet selectOrUpdateData(final T item, final BitRetriever bitRetriever) {
        BitSet flags = new BitSet();
        int hashCode = item.hashCode();
        int hashFunction = Math.abs(hashCode) % hashFunctions;
        long hashCodeMerged = multiFunctionHashing.hashCode(hashCode, hashFunction);

        for (int i = 0; i < hashFunctions; i++) {
            long hashCodeFixed = hashCodeMerged >= 0L ? hashCodeMerged : hashCodeMerged >>> (1 + (int) (~hashCodeMerged % MAXIMUM_SHIFTS));
            long index = hashCodeFixed % dataBitManipulator.size();
            int shifts = 8 + (i + hashFunction) % MAXIMUM_SHIFTS;

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

        return flags.cardinality() == hashFunctions;
    }

    @Override
    public boolean add(final T item) {
        BitSet flags = selectOrUpdateData(item, i -> dataBitManipulator.getAndSet(i, 1L) == 1L);

        return flags.cardinality() < hashFunctions;
    }

    @FunctionalInterface
    private interface BitRetriever {
        boolean get(long index);
    }
}
