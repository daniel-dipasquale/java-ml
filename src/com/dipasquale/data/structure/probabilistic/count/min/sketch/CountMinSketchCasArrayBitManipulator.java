package com.dipasquale.data.structure.probabilistic.count.min.sketch;

import com.dipasquale.common.ArgumentValidatorSupport;
import com.dipasquale.common.bit.BitManipulator;
import com.dipasquale.common.bit.concurrent.AtomicLongArrayBitManipulator;
import com.dipasquale.data.structure.probabilistic.MultiHashingFunction;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.concurrent.atomic.AtomicLongArray;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
final class CountMinSketchCasArrayBitManipulator<T> implements CountMinSketch<T> {
    private static final int MAXIMUM_SHIFTS = 32;
    private final MultiHashingFunction multiHashingFunction;
    private final List<AtomicLongArrayBitManipulator> dataBitManipulators;
    private final int hashingFunctionCount;

    CountMinSketchCasArrayBitManipulator(final MultiHashingFunction multiHashingFunction, final int size, final int hashingFunctionCount, final int bits) {
        this.multiHashingFunction = multiHashingFunction;
        this.dataBitManipulators = createDataBitManipulators(hashingFunctionCount, size, bits);
        this.hashingFunctionCount = hashingFunctionCount;
    }

    private static List<AtomicLongArrayBitManipulator> createDataBitManipulators(final int hashingFunctionCount, final int size, final int bits) {
        return IntStream.range(0, hashingFunctionCount)
                .mapToObj(i -> new AtomicLongArray(size))
                .map(a -> new AtomicLongArrayBitManipulator(a, bits))
                .collect(Collectors.toList());
    }

    private long selectOrUpdateData(final T item, final BitsRetriever bitsRetriever) {
        long value = Long.MIN_VALUE;
        int hashCode = item.hashCode();
        int hashFunctionIndex = Math.abs(hashCode) % hashingFunctionCount;
        long hashCodeMerged = multiHashingFunction.hashCode(hashCode, hashFunctionIndex);

        for (int i = 0; i < hashingFunctionCount; i++) {
            AtomicLongArrayBitManipulator dataBitManipulator = dataBitManipulators.get(i);
            long hashCodeFixed = hashCodeMerged >= 0L ? hashCodeMerged : hashCodeMerged >>> (1 + (int) (~hashCodeMerged % MAXIMUM_SHIFTS));
            long index = hashCodeFixed % dataBitManipulator.size();
            int shifts = 8 + (i + hashFunctionIndex) % MAXIMUM_SHIFTS;

            value = Math.min(value, bitsRetriever.get(dataBitManipulator, index));
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
        ArgumentValidatorSupport.ensureFalse(dataBitManipulators.get(0).isOutOfBounds(count), "count", "is out of bounds");

        return selectOrUpdateData(item, (bm, i) -> bm.getAndAdd(i, count));
    }

    @FunctionalInterface
    private interface BitsRetriever {
        long get(AtomicLongArrayBitManipulator dataBitManipulator, long index);
    }
}
