package com.dipasquale.common.bit.concurrent;

import com.dipasquale.common.bit.int2.BitManipulator;
import lombok.Generated;
import lombok.ToString;

import java.util.concurrent.atomic.AtomicLongArray;

public final class SingleBitAtomicLongArrayBitManipulator implements BitManipulator {
    private static final long RECORD_SIZE = 64L;
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
    public boolean isWithinBounds(final long value) {
        return value >= 0L && value <= 1L;
    }

    private int getArrayIndex(final long offset) {
        return (int) (offset >>> 6);
    }

    private long createBitMask(final long offset) {
        return 1L << (int) (offset % 64);
    }

    private long extract(final long mask, final long offset) {
        return (mask & createBitMask(offset)) == 0L ? 0L : 1L;
    }

    @Override
    public long extract(final long offset) {
        int arrayIndex = getArrayIndex(offset);
        long mask = array.get(arrayIndex);

        return extract(mask, offset);
    }

    private static long merge(final long value, final long oldMask, final long newMask) {
        if (value == 1L) {
            return oldMask | newMask;
        }

        return oldMask & ~newMask;
    }

    private long merge(final int arrayIndex, final long value, final long mask) {
        return array.accumulateAndGet(arrayIndex, mask, (om, nm) -> merge(value, om, nm));
    }

    @Override
    public long merge(final long offset, final long value) {
        int arrayIndex = getArrayIndex(offset);
        long newValue = value % 2;

        return merge(arrayIndex, newValue, createBitMask(offset));
    }

    private AccumulatorAudit compareAndSwap(final long offset, final long value, final Accumulator accumulator) {
        AccumulatorAudit accumulatorAudit = new AccumulatorAudit();
        int arrayIndex = getArrayIndex(offset);
        long valueFixed = value % 2;

        array.accumulateAndGet(arrayIndex, createBitMask(offset), (oldMask, newMask) -> {
            accumulatorAudit.oldValue = extract(oldMask, offset);
            accumulatorAudit.newValue = accumulator.accumulate(accumulatorAudit.oldValue, valueFixed) % 2;

            return merge(accumulatorAudit.newValue, oldMask, newMask);
        });

        return accumulatorAudit;
    }

    @Override
    public long setAndGet(final long offset, final long value) {
        return compareAndSwap(offset, value, (o, n) -> n).newValue;
    }

    @Override
    public long getAndSet(final long offset, final long value) {
        return compareAndSwap(offset, value, (o, n) -> n).oldValue;
    }

    @Override
    public long accumulateAndGet(final long offset, final long value, final Accumulator accumulator) {
        return compareAndSwap(offset, value, accumulator).newValue;
    }

    @Override
    public long getAndAccumulate(final long offset, final long value, final Accumulator accumulator) {
        return compareAndSwap(offset, value, accumulator).oldValue;
    }

    @Generated
    @ToString
    private static final class AccumulatorAudit {
        private long oldValue;
        private long newValue;
    }
}