package com.dipasquale.common.bit.int1;

public interface BitManipulator {
    int size();

    boolean isOutOfBounds(int value);

    int extract(int offset);

    int merge(int offset, int value);

    default int setAndGet(final int offset, final int value) {
        merge(offset, value);

        return extract(offset);
    }

    default int getAndSet(final int offset, final int value) {
        int oldValue = extract(offset);

        merge(offset, value);

        return oldValue;
    }

    default int accumulateAndGet(final int offset, final int value, final BitManipulatorSupport.Accumulator accumulator) {
        int oldValue = extract(offset);
        int newValue = accumulator.accumulate(oldValue, value);

        return setAndGet(offset, newValue);
    }

    default int getAndAccumulate(final int offset, final int value, final BitManipulatorSupport.Accumulator accumulator) {
        int oldValue = extract(offset);
        int newValue = accumulator.accumulate(oldValue, value);

        merge(offset, newValue);

        return oldValue;
    }

    default int addAndGet(final int offset, final int delta) {
        return accumulateAndGet(offset, delta, Integer::sum);
    }

    default int getAndAdd(final int offset, final int delta) {
        return getAndAccumulate(offset, delta, Integer::sum);
    }

    default int incrementAndGet(final int offset) {
        return addAndGet(offset, 1);
    }

    default int getAndIncrement(final int offset) {
        return getAndAdd(offset, 1);
    }

    default int decrementAndGet(final int offset) {
        return addAndGet(offset, -1);
    }

    default int getAndDecrement(final int offset) {
        return getAndAdd(offset, -1);
    }
}
