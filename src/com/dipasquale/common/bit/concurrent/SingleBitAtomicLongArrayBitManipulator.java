package com.dipasquale.common.bit.concurrent;

import com.dipasquale.common.bit.int2.BitManipulator;
import com.dipasquale.common.bit.int2.BitManipulatorSupport;
import lombok.Generated;
import lombok.ToString;

import java.util.concurrent.atomic.AtomicLongArray;

public final class SingleBitAtomicLongArrayBitManipulator implements BitManipulator {
    private static final int RECORD_SIZE = 64;
    private final AtomicLongArray array;
    private final long size;

    public SingleBitAtomicLongArrayBitManipulator(final AtomicLongArray array) {
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

    private long createBitMask(final long offset) {
        return 1L << (int) (offset % 64);
    }

    private long getValueFromBitMask(final long mask, final long offset) {
        return (mask & createBitMask(offset)) == 0L ? 0L : 1L;
    }

    @Override
    public long extract(final long offset) {
        int arrayIndex = getArrayIndex(offset);
        long mask = array.get(arrayIndex);

        return getValueFromBitMask(mask, offset);
    }

    private static long mergeOutOfCas(final long value, final long oldMask, final long newMask) {
        if (value == 1L) {
            return oldMask | newMask;
        }

        return oldMask & ~newMask;
    }

    private long merge(final int arrayIndex, final long value, final long mask) {
        return array.accumulateAndGet(arrayIndex, mask, (om, nm) -> mergeOutOfCas(value, om, nm));
    }

    @Override
    public long merge(final long offset, final long value) {
        int arrayIndex = getArrayIndex(offset);
        long valueNew = value % 2;

        return merge(arrayIndex, valueNew, createBitMask(offset));
    }

    private ValueGatherer compareAndSwap(final long offset, final long value, final BitManipulatorSupport.Accumulator accumulator) {
        ValueGatherer valueGatherer = new ValueGatherer();
        int arrayIndex = getArrayIndex(offset);
        long valueFixed = value % 2;

        array.accumulateAndGet(arrayIndex, createBitMask(offset), (om, nm) -> {
            valueGatherer.valueOld = getValueFromBitMask(om, offset);
            valueGatherer.valueNew = accumulator.accumulate(valueGatherer.valueOld, valueFixed) % 2;

            return mergeOutOfCas(valueGatherer.valueNew, om, nm);
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

    @Generated
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