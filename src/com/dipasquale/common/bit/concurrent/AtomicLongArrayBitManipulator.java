package com.dipasquale.common.bit.concurrent;

import com.dipasquale.common.bit.int2.BitManipulator;
import com.dipasquale.common.bit.int2.BitManipulatorSupport;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.atomic.AtomicLongArray;

public final class AtomicLongArrayBitManipulator implements BitManipulator {
    private static final long MAXIMUM_BITS = 64L;
    private static final long IGNORED_VALUE = -1L;
    private final AtomicLongArray array;
    private final long recordSize;
    private final long size;
    private final BitManipulatorSupport bitManipulatorSupport;

    public AtomicLongArrayBitManipulator(final AtomicLongArray array, final int bits) {
        long recordSize = MAXIMUM_BITS / (long) bits;

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
    public boolean isWithinBounds(final long value) {
        return bitManipulatorSupport.isWithinBounds(value);
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
        long bitIndex = getBitIndex(offset);

        return bitManipulatorSupport.extract(value, bitIndex);
    }

    @Override
    public long merge(final long offset, final long value) {
        int arrayIndex = getArrayIndex(offset);
        long bitIndex = getBitIndex(offset);

        return array.accumulateAndGet(arrayIndex, -1L, (oldMask, __) -> bitManipulatorSupport.merge(oldMask, bitIndex, value));
    }

    private AccumulatorAudit compareAndSwap(final long offset, final long value, final Accumulator accumulator) {
        AccumulatorAudit accumulatorAudit = new AccumulatorAudit();
        int arrayIndex = getArrayIndex(offset);
        long bitIndex = getBitIndex(offset);

        array.accumulateAndGet(arrayIndex, IGNORED_VALUE, (oldMask, __) -> {
            accumulatorAudit.oldValue = bitManipulatorSupport.extract(oldMask, bitIndex);

            long newValue = accumulator.accumulate(accumulatorAudit.oldValue, value);
            long newMask = bitManipulatorSupport.merge(oldMask, bitIndex, newValue);

            accumulatorAudit.newValue = bitManipulatorSupport.extract(newMask, bitIndex);

            return newMask;
        });

        return accumulatorAudit;
    }

    @Override
    public long setAndGet(final long offset, final long value) {
        return compareAndSwap(offset, value, (__, newValue) -> newValue).newValue;
    }

    @Override
    public long getAndSet(final long offset, final long value) {
        return compareAndSwap(offset, value, (__, newValue) -> newValue).oldValue;
    }

    @Override
    public long accumulateAndGet(final long offset, final long value, final Accumulator accumulator) {
        return compareAndSwap(offset, value, accumulator).newValue;
    }

    @Override
    public long getAndAccumulate(final long offset, final long value, final Accumulator accumulator) {
        return compareAndSwap(offset, value, accumulator).oldValue;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class AccumulatorAudit {
        private long oldValue = 0L;
        private long newValue = 0L;
    }
}