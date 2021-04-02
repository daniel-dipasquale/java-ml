package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.common.SequentialId;
import com.dipasquale.ai.common.SequentialIdFactory;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class SequentialIdFactorySynchronized implements SequentialIdFactory {
    private final String name;
    private final SequentialIdFactory sequentialIdFactory;

    private SequentialId createSequentialId() {
        synchronized (sequentialIdFactory) {
            return sequentialIdFactory.next();
        }
    }

    @Override
    public SequentialId next() {
        SequentialId sequentialId = createSequentialId();

        return new SequentialIdInternal(name, sequentialId);
    }

    @Override
    public void reset() {
        synchronized (sequentialIdFactory) {
            sequentialIdFactory.reset();
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @EqualsAndHashCode
    private static final class SequentialIdInternal implements SequentialId {
        private final String name;
        private final SequentialId sequentialId;

        private int compareTo(final SequentialIdInternal other) {
            int comparison = name.compareTo(other.name);

            if (comparison != 0) {
                return comparison;
            }

            return sequentialId.compareTo(other.sequentialId);
        }

        @Override
        public int compareTo(final SequentialId other) {
            if (other instanceof SequentialIdInternal) {
                return compareTo((SequentialIdInternal) other);
            }

            String message = String.format("unable to compare incompatible sequential ids, x: %s, y: %s", getClass().getTypeName(), other == null ? null : other.getClass().getTypeName());

            throw new IllegalStateException(message);
        }

        @Override
        public String toString() {
            return String.format("%s:%s", name, sequentialId);
        }
    }
}
