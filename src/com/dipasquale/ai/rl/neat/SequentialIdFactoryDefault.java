package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.common.SequentialId;
import com.dipasquale.ai.common.SequentialIdFactory;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class SequentialIdFactoryDefault implements SequentialIdFactory {
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

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @EqualsAndHashCode
    @ToString
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

            int comparison = Integer.compare(System.identityHashCode(getClass()), System.identityHashCode(other.getClass()));

            if (comparison != 0) {
                return comparison;
            }

            return -1;
        }
    }
}
