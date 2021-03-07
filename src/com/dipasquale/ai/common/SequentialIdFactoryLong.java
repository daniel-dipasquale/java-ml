package com.dipasquale.ai.common;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

public final class SequentialIdFactoryLong implements SequentialIdFactory {
    private long current = 0L;

    @Override
    public SequentialId next() {
        if (current == Long.MAX_VALUE) {
            throw new IllegalStateException("SequentialId reach its end");
        }

        long value = ++current;

        return new SequentialIdLong(value);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @EqualsAndHashCode
    @ToString
    private static final class SequentialIdLong implements SequentialId {
        private final long value;

        private int compareTo(final SequentialIdLong other) {
            return Long.compare(value, other.value);
        }

        @Override
        public int compareTo(final SequentialId other) {
            if (other instanceof SequentialIdLong) {
                return compareTo((SequentialIdLong) other);
            }

            int comparison = Integer.compare(System.identityHashCode(getClass()), System.identityHashCode(other.getClass()));

            if (comparison != 0) {
                return comparison;
            }

            return -1;
        }
    }
}
