package com.dipasquale.common.bit.int2;

public interface BitManipulator {
    long size();

    boolean isWithinBounds(long value);

    long extract(long offset);

    long merge(long offset, long value);

    default long setAndGet(final long offset, final long value) {
        merge(offset, value);

        return extract(offset);
    }

    default long getAndSet(final long offset, final long value) {
        long oldValue = extract(offset);

        merge(offset, value);

        return oldValue;
    }

    default long accumulateAndGet(final long offset, final long value, final Accumulator accumulator) {
        long oldValue = extract(offset);
        long newValue = accumulator.accumulate(oldValue, value);

        return setAndGet(offset, newValue);
    }

    default long getAndAccumulate(final long offset, final long value, final Accumulator accumulator) {
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

    @FunctionalInterface
    interface Accumulator {
        long accumulate(long oldValue, long newValue);
    }
}
