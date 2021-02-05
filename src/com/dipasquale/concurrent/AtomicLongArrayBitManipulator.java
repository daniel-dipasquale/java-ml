package com.dipasquale.concurrent;

import com.dipasquale.common.BitManipulator;
import com.dipasquale.common.BitManipulatorSupport;

import java.util.concurrent.atomic.AtomicLongArray;

public final class AtomicLongArrayBitManipulator implements BitManipulator {
    private static final int LONG_SIZE = 64;
    private final AtomicLongArray array;
    private final long recordSize;
    private final long size;
    private final BitManipulatorSupport bitManipulatorSupport;

    public AtomicLongArrayBitManipulator(final AtomicLongArray array, final int bits) {
        long recordSize = (long) LONG_SIZE / (long) bits;

        this.array = array;
        this.recordSize = recordSize;
        this.size = (long) array.length() * recordSize;
        this.bitManipulatorSupport = BitManipulatorSupport.create(bits);
    }

    @Override
    public long size() {
        return size;
    }

    @Override
    public boolean isOutOfBounds(final long value) {
        return bitManipulatorSupport.isOutOfBounds(value);
    }

    private int getArrayIndex(final long offset) {
        return (int) (offset / recordSize);
    }

    private long getBitIndex(final long offset) {
        return offset % recordSize;
    }

    @Override
    public long extract(final long offset) {
        int arrayIndex = getArrayIndex(offset);
        long value = array.get(arrayIndex);
        long bitsIndex = getBitIndex(offset);

        return bitManipulatorSupport.extract(value, bitsIndex);
    }

    @Override
    public long merge(final long offset, final long value) {
        int arrayIndex = getArrayIndex(offset);
        long bitsIndex = getBitIndex(offset);

        return array.accumulateAndGet(arrayIndex, -1L, (om, nm) -> bitManipulatorSupport.merge(om, bitsIndex, value));
    }

    private ValueGatherer compareAndSwap(final long offset, final long value, final BitManipulatorSupport.Accumulator accumulator) {
        ValueGatherer valueGatherer = new ValueGatherer();
        int arrayIndex = getArrayIndex(offset);
        long bitsIndex = getBitIndex(offset);

        array.accumulateAndGet(arrayIndex, -1L, (om, nm) -> {
            valueGatherer.valueOld = bitManipulatorSupport.extract(om, bitsIndex);

            long valueNew = accumulator.accumulate(valueGatherer.valueOld, value);
            long mergedMask = bitManipulatorSupport.merge(om, bitsIndex, valueNew);

            valueGatherer.valueNew = bitManipulatorSupport.extract(mergedMask, bitsIndex);

            return mergedMask;
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

    private static final class ValueGatherer {
        private long valueOld;
        private long valueNew;
    }
}