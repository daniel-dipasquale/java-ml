package com.dipasquale.common;

public interface BitManipulatorSupport {
    int MAXIMUM_BITS = 64;

    long size();

    boolean isOutOfBounds(long value);

    long extract(long raw, long offset);

    long merge(long raw, long offset, long value);

    default long setAndGet(final long raw, final long offset, final long value) {
        merge(raw, offset, value);

        return value;
    }

    default long getAndSet(final long raw, final long offset, final long value) {
        long valueOld = extract(raw, offset);

        merge(raw, offset, value);

        return valueOld;
    }

    default long accumulateAndGet(final long raw, final long offset, final long value, final Accumulator accumulator) {
        long valueOld = extract(raw, offset);
        long valueNew = accumulator.accumulate(valueOld, value);

        return setAndGet(raw, offset, valueNew);
    }

    default long getAndAccumulate(final long raw, final long offset, final long value, final Accumulator accumulator) {
        long oldValue = extract(raw, offset);
        long newValue = accumulator.accumulate(oldValue, value);

        merge(raw, offset, newValue);

        return oldValue;
    }

    default long addAndGet(final long raw, final long offset, final long delta) {
        return accumulateAndGet(raw, offset, delta, Long::sum);
    }

    default long getAndAdd(final long raw, final long offset, final long delta) {
        return getAndAccumulate(raw, offset, delta, Long::sum);
    }

    default long incrementAndGet(final long raw, final long offset) {
        return addAndGet(raw, offset, 1L);
    }

    default long getAndIncrement(final long raw, final long offset) {
        return getAndAdd(raw, offset, 1L);
    }

    default long decrementAndGet(final long raw, final long offset) {
        return addAndGet(raw, offset, -1L);
    }

    default long getAndDecrement(final long raw, final long offset) {
        return getAndAdd(raw, offset, -1L);
    }

    static BitManipulatorSupport create(final int bits) {
        ArgumentValidator.ensureGreaterThanZero(bits, "bits");
        ArgumentValidator.ensureLessThanOrEqualTo(bits, MAXIMUM_BITS, "bits");

        if (bits == MAXIMUM_BITS) {
            return new BitManipulatorSupport64();
        }

        return new BitManipulatorSupportDefault(bits);
    }

    @FunctionalInterface
    interface Accumulator {
        long accumulate(long oldValue, long newValue);
    }
}
