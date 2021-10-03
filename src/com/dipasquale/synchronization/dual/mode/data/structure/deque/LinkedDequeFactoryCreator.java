package com.dipasquale.synchronization.dual.mode.data.structure.deque;

import com.dipasquale.common.factory.data.structure.deque.DequeFactory;
import com.dipasquale.synchronization.dual.mode.ConcurrencyLevelState;
import com.dipasquale.synchronization.dual.mode.DualModeFactory;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedDeque;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class LinkedDequeFactoryCreator implements DualModeFactory<DequeFactory>, Serializable {
    @Serial
    private static final long serialVersionUID = 1655780460540539395L;
    private static final LinkedDequeFactoryCreator INSTANCE = new LinkedDequeFactoryCreator();

    public static LinkedDequeFactoryCreator getInstance() {
        return INSTANCE;
    }

    @Override
    public DequeFactory create(final ConcurrencyLevelState concurrencyLevelState) {
        return new LinkedDequeFactory(concurrencyLevelState.getCurrent() > 0);
    }

    @Serial
    private Object readResolve() {
        return INSTANCE;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class LinkedDequeFactory implements DequeFactory, Serializable {
        @Serial
        private static final long serialVersionUID = 5483251986252952651L;
        private final boolean concurrent;

        @Override
        public <T> Deque<T> create(final Deque<T> other) {
            if (concurrent && other != null) {
                return new ConcurrentLinkedDeque<>(other);
            }

            if (concurrent) {
                return new ConcurrentLinkedDeque<>();
            }

            if (other != null) {
                return new LinkedList<>(other);
            }

            return new LinkedList<>();
        }
    }
}
