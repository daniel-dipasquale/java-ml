package com.dipasquale.common.bit.int1;

import com.dipasquale.common.ArgumentValidatorSupport;

public interface BitManipulatorSupport {
    int MAXIMUM_BITS = 32;

    int size();

    boolean isOutOfBounds(int value);

    int extract(int raw, int offset);

    int merge(int raw, int offset, int value);

    default int setAndGet(final int raw, final int offset, final int value) {
        merge(raw, offset, value);

        return value;
    }

    default int getAndSet(final int raw, final int offset, final int value) {
        int oldValue = extract(raw, offset);

        merge(raw, offset, value);

        return oldValue;
    }

    default int accumulateAndGet(final int raw, final int offset, final int value, final Accumulator accumulator) {
        int oldValue = extract(raw, offset);
        int newValue = accumulator.accumulate(oldValue, value);

        return setAndGet(raw, offset, newValue);
    }

    default int getAndAccumulate(final int raw, final int offset, final int value, final Accumulator accumulator) {
        int oldValue = extract(raw, offset);
        int newValue = accumulator.accumulate(oldValue, value);

        merge(raw, offset, newValue);

        return oldValue;
    }

    default int addAndGet(final int raw, final int offset, final int delta) {
        return accumulateAndGet(raw, offset, delta, Integer::sum);
    }

    default int getAndAdd(final int raw, final int offset, final int delta) {
        return getAndAccumulate(raw, offset, delta, Integer::sum);
    }

    default int incrementAndGet(final int raw, final int offset) {
        return addAndGet(raw, offset, 1);
    }

    default int getAndIncrement(final int raw, final int offset) {
        return getAndAdd(raw, offset, 1);
    }

    default int decrementAndGet(final int raw, final int offset) {
        return addAndGet(raw, offset, -1);
    }

    default int getAndDecrement(final int raw, final int offset) {
        return getAndAdd(raw, offset, -1);
    }

    static BitManipulatorSupport create(final int bits) {
        ArgumentValidatorSupport.ensureGreaterThanZero(bits, "bits");
        ArgumentValidatorSupport.ensureLessThanOrEqualTo(bits, MAXIMUM_BITS, "bits");

        if (bits == MAXIMUM_BITS) {
            return new Only32BitManipulatorSupport();
        }

        return new NBitManipulatorSupport(bits);
    }

    @FunctionalInterface
    interface Accumulator {
        int accumulate(int oldValue, int newValue);
    }
}
