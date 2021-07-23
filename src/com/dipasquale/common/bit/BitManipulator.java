package com.dipasquale.common.bit;

public interface BitManipulator {
    long size();

    boolean isOutOfBounds(long value);

    long extract(long offset);

    long merge(long offset, long value);

    default long setAndGet(final long offset, final long value) {
        merge(offset, value);

        return extract(offset);
    }

    default long getAndSet(final long offset, final long value) {
        long valueOld = extract(offset);

        merge(offset, value);

        return valueOld;
    }

    default long accumulateAndGet(final long offset, final long value, final BitManipulatorSupport.Accumulator accumulator) {
        long valueOld = extract(offset);
        long valueNew = accumulator.accumulate(valueOld, value);

        return setAndGet(offset, valueNew);
    }

    default long getAndAccumulate(final long offset, final long value, final BitManipulatorSupport.Accumulator accumulator) {
        long oldValue = extract(offset);
        long newValue = accumulator.accumulate(oldValue, value);

        merge(offset, newValue);

        return oldValue;
    }

    default long addAndGet(final long offset, final long delta) {
        return accumulateAndGet(offset, delta, Long::sum);
    }

    default long getAndAdd(final long offset, final long delta) {
        return getAndAccumulate(offset, delta, Long::sum);
    }

    default long incrementAndGet(final long offset) {
        return addAndGet(offset, 1L);
    }

    default long getAndIncrement(final long offset) {
        return getAndAdd(offset, 1L);
    }

    default long decrementAndGet(final long offset) {
        return addAndGet(offset, -1L);
    }

    default long getAndDecrement(final long offset) {
        return getAndAdd(offset, -1L);
    }
}
