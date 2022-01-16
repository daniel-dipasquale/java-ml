package com.dipasquale.data.structure.probabilistic.count.min.sketch.concurrent;

import com.dipasquale.common.ArgumentValidatorSupport;
import com.dipasquale.common.bit.concurrent.AtomicLongArrayBitManipulator;
import com.dipasquale.common.bit.int2.BitManipulator;
import com.dipasquale.data.structure.probabilistic.HashingFunction;
import com.dipasquale.data.structure.probabilistic.count.min.sketch.CountMinSketch;

import java.util.List;
import java.util.concurrent.atomic.AtomicLongArray;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

final class AtomicLongArrayCountMinSketch<T> implements CountMinSketch<T> {
    private static final int MAXIMUM_SHIFTS = 32;
    private final HashingFunction multiHashingFunction;
    private final List<AtomicLongArrayBitManipulator> bitManipulators;
    private final int hashingFunctions;

    AtomicLongArrayCountMinSketch(final HashingFunction multiHashingFunction, final int size, final int hashingFunctions, final int bitsForCounter) {
        this.multiHashingFunction = multiHashingFunction;
        this.bitManipulators = createDataBitManipulators(hashingFunctions, size, bitsForCounter);
        this.hashingFunctions = hashingFunctions;
    }

    private static List<AtomicLongArrayBitManipulator> createDataBitManipulators(final int hashingFunctions, final int size, final int bitsForCounter) {
        return IntStream.range(0, hashingFunctions)
                .mapToObj(__ -> new AtomicLongArray(size))
                .map(array -> new AtomicLongArrayBitManipulator(array, bitsForCounter))
                .collect(Collectors.toList());
    }

    private long selectOrUpdateData(final T item, final BitsRetriever bitsRetriever) {
        long value = Long.MIN_VALUE;
        int hashCode = item.hashCode();
        int hashFunctionIndex = Math.abs(hashCode) % hashingFunctions;
        long hashCodeMerged = multiHashingFunction.hashCode(hashCode, hashFunctionIndex);

        for (int i = 0; i < hashingFunctions; i++) {
            AtomicLongArrayBitManipulator bitManipulator = bitManipulators.get(i);

            long hashCodeFixed = hashCodeMerged < 0L
                    ? hashCodeMerged >>> (1 + (int) (~hashCodeMerged % MAXIMUM_SHIFTS))
                    : hashCodeMerged;

            long index = hashCodeFixed % bitManipulator.size();
            int shifts = 8 + (i + hashFunctionIndex) % MAXIMUM_SHIFTS;

            value = Math.min(value, bitsRetriever.get(bitManipulator, index)); // TODO: verify whether this is needed
            hashCodeMerged = (hashCodeMerged >> MAXIMUM_SHIFTS) ^ (hashCodeMerged << shifts);
        }

        return value;
    }

    @Override
    public long get(final T item) {
        return selectOrUpdateData(item, BitManipulator::extract);
    }

    @Override
    public long put(final T item, final long count) {
        ArgumentValidatorSupport.ensureTrue(bitManipulators.get(0).isWithinBounds(count), "count", "is out of bounds");

        return selectOrUpdateData(item, (bitManipulator, index) -> bitManipulator.getAndAdd(index, count));
    }

    @FunctionalInterface
    private interface BitsRetriever {
        long get(AtomicLongArrayBitManipulator bitManipulator, long index);
    }
}
