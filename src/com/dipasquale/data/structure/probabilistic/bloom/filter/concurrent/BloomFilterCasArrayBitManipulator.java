package com.dipasquale.data.structure.probabilistic.bloom.filter.concurrent;

import com.dipasquale.common.BitManipulator;
import com.dipasquale.concurrent.AtomicLongArrayBitManipulatorSingleBit;
import com.dipasquale.data.structure.probabilistic.MultiFunctionHashing;
import com.dipasquale.data.structure.probabilistic.bloom.filter.BloomFilter;

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
        this.dataBitManipulator = new AtomicLongArrayBitManipulatorSingleBit(array);
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

//    private BitSet selectOrUpdateData(final T item, final BitRetriever bitRetriever) {
//        BitSet flags = new BitSet();
//        int hashCode = item.hashCode();
//        int hashFunction = Math.abs(hashCode) % hashFunctions;
//        long hashCodeMerged = multiFunctionHashing.hashCode(hashCode, hashFunction);
//
//        for (int i = 0; i < hashFunctions; i++) {
////            long hashCodeFixed = Math.abs(hashCodeMerged);
//            long hashCodeFixed = hashCodeMerged >= 0L ? hashCodeMerged : hashCodeMerged >>> (1 + (int) (~hashCodeMerged % MAXIMUM_SHIFTS));
////            long hashCodeFixed = hashCodeMerged >= 0L ? hashCodeMerged : ~hashCodeMerged;
//            long index = hashCodeFixed % dataBitManipulator.size();
//            int shifts = 8 + (i + hashFunction) % MAXIMUM_SHIFTS;
//
//            if (bitRetriever.get(index)) {
//                flags.set(i);
//            }
//
////            hashCodeMerged += (hashCodeMerged >> shifts) << shifts;
//            hashCodeMerged = (hashCodeMerged >> MAXIMUM_SHIFTS) ^ (hashCodeMerged << shifts);
////            hashCodeMerged = (hashCodeMerged << 5) | (hashCodeMerged >>> 27);
//        }
//
//        return flags;
//    }

//    private BitSet selectOrUpdateData(final T item, final BitRetriever bitRetriever) {
//        BitSet flags = new BitSet();
//        int hashCode = item.hashCode();
//        int hashFunction = Math.abs(hashCode) % hashFunctions;
//        long hashCodeMerged = multiFunctionHashing.hashCode(hashCode, hashFunction);
//
//        for (int i = 0; i < hashFunctions; i++) {
////            long hashCodeFixed = Math.abs(hashCodeMerged);
//            long hashCodeFixed = hashCodeMerged >= 0L ? hashCodeMerged : hashCodeMerged >>> (1 + (int) (~hashCodeMerged % 30));
//            long index = hashCodeFixed % dataBitManipulator.size();
//            final int shifts = 8;
////            int shifts = 8 + (i + hashFunction) % MAXIMUM_SHIFTS;
//
//            if (bitRetriever.get(index)) {
//                flags.set(i);
//            }
//
////            hashCodeMerged += (hashCodeMerged >> shifts) << shifts;
//            hashCodeMerged += ~(hashCodeMerged >> shifts) ^ (hashCodeMerged << shifts);
//        }
//
//        return flags;
//    }

//    private BitSet selectOrUpdateData(final T item, final BitRetriever bitRetriever) {
//        BitSet flags = new BitSet();
//        int hashCode = item.hashCode();
//        int hashFunction = Math.abs(hashCode) % hashFunctions;
//        long hashCodeFull = multiFunctionHashing.hashCode(hashCode, hashFunction);
//        int hashCode1 = (int) hashCodeFull;
//        int hashCode2 = (int) (hashCodeFull >>> MAXIMUM_SHIFTS);
//
//        for (int i = 0; i < hashFunctions; i++) {
//            int hashCodeMerged = hashCode1 + i * hashCode2 - hashFunction;
//
//            if (hashCodeMerged < 0) {
//                hashCodeMerged = ~hashCodeMerged;
//            }
//
//            long index = (((long) hashCodeMerged << MAXIMUM_SHIFTS) + hashCodeMerged) % dataBitManipulator.size();
//
//            if (bitRetriever.get(index)) {
//                flags.set(i);
//            }
//
//            hashCode1 -= hashCode1 + 8 * i + hashFunction;
//            hashCode2 += hashCode2 - 8 * i + hashFunction;
//        }
//
//        return flags;
//    }

//    private BitSet selectOrUpdateData(final T item, final BitRetriever bitRetriever) {
//        BitSet flags = new BitSet();
//        int hashCode = item.hashCode();
//        int hashFunction = Math.abs(hashCode) % hashFunctions;
//        long hashCodeMerged = multiFunctionHashing.hashCode(hashCode, hashFunction);
//
//        for (int i = 0; i < hashFunctions; i++) {
//            long hashCodeFixed = hashCodeMerged >= 0L ? hashCodeMerged : ~hashCodeMerged;
//            long index = hashCodeFixed % dataBitManipulator.size();
//            int shifts = 8 + (i + hashFunction) % MAXIMUM_SHIFTS;
//
//            if (bitRetriever.get(index)) {
//                flags.set(i);
//            }
//
//            hashCodeMerged += hashCodeMerged & (hashCodeMerged >> shifts);
//        }
//
//        return flags;
//    }