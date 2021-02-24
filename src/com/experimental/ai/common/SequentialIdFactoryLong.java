package com.experimental.ai.common;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@ToString
public final class SequentialIdFactoryLong implements SequentialIdFactory<SequentialIdFactoryLong.Id> {
    private static final long DELTA = 1L;
    private final String name;
    private long current = 0L;

    @Override
    public SequentialIdFactoryLong.Id next() {
        current += DELTA;

        if (current < 0L) {
            String message = String.format("CounterLong has ran out of ids to dispatch: %d", current);

            throw new IllegalStateException(message);
        }

        return new Id(name, current);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Id implements Comparable<Id> {
        private final String name;
        private final long number;

        @Override
        public int compareTo(final Id id) {
            int comparison = name.compareTo(id.name);

            if (comparison != 0) {
                return comparison;
            }

            return Long.compare(number, id.number);
        }
    }
}
