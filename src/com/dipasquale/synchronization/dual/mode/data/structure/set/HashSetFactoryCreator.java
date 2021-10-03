package com.dipasquale.synchronization.dual.mode.data.structure.set;

import com.dipasquale.common.factory.data.structure.set.SetFactory;
import com.dipasquale.synchronization.dual.mode.ConcurrencyLevelState;
import com.dipasquale.synchronization.dual.mode.DualModeFactory;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HashSetFactoryCreator implements DualModeFactory<SetFactory>, Serializable {
    @Serial
    private static final long serialVersionUID = 4579949282217329702L;
    private static final HashSetFactoryCreator INSTANCE = new HashSetFactoryCreator();

    public static HashSetFactoryCreator getInstance() {
        return INSTANCE;
    }

    @Override
    public SetFactory create(final ConcurrencyLevelState concurrencyLevelState) {
        return new HashSetFactory(concurrencyLevelState.getCurrent() > 0, concurrencyLevelState.getMaximum(), 16);
    }

    @Serial
    private Object readResolve() {
        return INSTANCE;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class HashSetFactory implements SetFactory, Serializable {
        @Serial
        private static final long serialVersionUID = -4319521640174513096L;
        private final boolean concurrent;
        private final int numberOfThreads;
        private final int initialCapacity;

        @Override
        public <T> Set<T> create(final Set<T> other) {
            if (concurrent) {
                Set<T> set = Collections.newSetFromMap(new ConcurrentHashMap<>(initialCapacity, 0.75f, numberOfThreads));

                if (other != null) {
                    set.addAll(other);
                }

                return set;
            }

            if (other == null) {
                return new HashSet<>(initialCapacity);
            }

            return new HashSet<>(other);
        }
    }
}
