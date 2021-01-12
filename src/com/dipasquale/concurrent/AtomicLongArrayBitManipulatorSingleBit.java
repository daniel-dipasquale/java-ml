package com.dipasquale.concurrent;

import com.dipasquale.common.BitManipulator;
import com.dipasquale.common.BitManipulatorSupport;
import lombok.ToString;

import java.util.concurrent.atomic.AtomicLongArray;

public final class AtomicLongArrayBitManipulatorSingleBit implements BitManipulator {
    private static final int RECORD_SIZE = 64;
    private final AtomicLongArray array;
    private final long size;

    public AtomicLongArrayBitManipulatorSingleBit(final AtomicLongArray array) {
        this.array = array;
        this.size = (long) array.length() * RECORD_SIZE;
    }

    @Override
    public long size() {
        return size;
    }

    @Override
    public boolean isOutOfBounds(final long value) {
        return value < 0L || value > 1L;
    }

    private int getArrayIndex(final long offset) {
        return (int) (offset >>> 6);
    }

    private long getBitMask(final long offset) {
        return 1L << (int) (offset % 64);
    }

    private long getValueFromBitMask(final long value, final long offset) {
        return (value & getBitMask(offset)) == 0L ? 0L : 1L;
    }

    @Override
    public long extract(final long offset) {
        int arrayIndex = getArrayIndex(offset);
        long value = array.get(arrayIndex);

        return getValueFromBitMask(value, offset);
    }

    private static long mergeOutOfCas(final long value, final long oldMask, final long newMask) {
        if (value == 1L) {
            return oldMask | newMask;
        }

        return oldMask & ~newMask;
    }

    private long merge(final int arrayIndex, final long value, final long mask) {
        return array.accumulateAndGet(arrayIndex, mask, (o, n) -> mergeOutOfCas(value, o, n));
    }

    @Override
    public long merge(final long offset, final long value) {
        int arrayIndex = getArrayIndex(offset);
        long valueNew = value % 2;

        return merge(arrayIndex, valueNew, getBitMask(offset));
    }

    private ValueGatherer compareAndSwap(final long offset, final long value, final BitManipulatorSupport.Accumulator accumulator) {
        ValueGatherer valueGatherer = new ValueGatherer();
        int arrayIndex = getArrayIndex(offset);
        long valueNew = value % 2;

        array.accumulateAndGet(arrayIndex, getBitMask(offset), (o, n) -> {
            long valueOld = getValueFromBitMask(o, offset);

            valueGatherer.valueOld = valueOld;
            valueGatherer.valueNew = accumulator.accumulate(valueOld, valueNew);

            return mergeOutOfCas(valueGatherer.valueNew, o, n);
        });

        return valueGatherer;
    }

    @Override
    public long setAndGet(final long offset, final long value) {
        return compareAndSwap(offset, value, (o, n) -> n).valueNew;
    }

    @Override
    public long getAndSet(final long offset, final long value) {
        return compareAndSwap(offset, value, (o, n) -> n).valueOld;
    }

    @Override
    public long accumulateAndGet(final long offset, final long value, final BitManipulatorSupport.Accumulator accumulator) {
        return compareAndSwap(offset, value, accumulator).valueNew;
    }

    @Override
    public long getAndAccumulate(final long offset, final long value, final BitManipulatorSupport.Accumulator accumulator) {
        return compareAndSwap(offset, value, accumulator).valueOld;
    }

    @ToString
    private static final class ValueGatherer {
        private long valueOld;
        private long valueNew;
    }
}

/*
        int index = (int) (offset >>> 6);

        if (index == 0 || index >= array.length() - 1) {
            synchronized (System.out) {
                System.out.printf("offset: %d, index: %d, length: %d%n", offset, index, array.length());
            }
        }

        return index;
 */