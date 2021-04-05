package com.dipasquale.ai.common;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.io.Serial;

public final class SequentialIdFactoryDefault implements SequentialIdFactory {
    @Serial
    private static final long serialVersionUID = -2914528594779687249L;
    private long current = 0L;

    @Override
    public SequentialId create() {
        if (current == Long.MAX_VALUE) {
            throw new IllegalStateException("SequentialId reach its end");
        }

        long value = ++current;

        return new SequentialIdLong(value);
    }

    @Override
    public void reset() {
        current = 0L;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @EqualsAndHashCode
    private static final class SequentialIdLong implements SequentialId {
        @Serial
        private static final long serialVersionUID = -78487413947636387L;
        private final long value;

        private int compareTo(final SequentialIdLong other) {
            return Long.compare(value, other.value);
        }

        @Override
        public int compareTo(final SequentialId other) {
            if (other instanceof SequentialIdLong) {
                return compareTo((SequentialIdLong) other);
            }

            String message = String.format("unable to compare incompatible sequential ids, x: %s, y: %s", getClass().getTypeName(), other == null ? null : other.getClass().getTypeName());

            throw new IllegalStateException(message);
        }

        @Override
        public String toString() {
            return Long.toString(value);
        }
    }
}
