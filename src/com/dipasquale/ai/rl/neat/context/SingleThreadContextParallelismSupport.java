package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.synchronization.wait.handle.StrategyWaitHandle;
import com.dipasquale.synchronization.wait.handle.WaitHandle;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public final class SingleThreadContextParallelismSupport implements Context.ParallelismSupport {
    private final InternalParameters params = new InternalParameters();
    private final Collection<Throwable> unhandledExceptions = Collections.newSetFromMap(new IdentityHashMap<>());

    @Override
    public Context.ParallelismParameters params() {
        return params;
    }

    @Override
    public <T> WaitHandle forEach(final Iterator<T> iterator, final Consumer<T> itemHandler) {
        unhandledExceptions.clear();

        while (iterator.hasNext()) {
            try {
                itemHandler.accept(iterator.next());
            } catch (Throwable e) {
                unhandledExceptions.add(e);
            }
        }

        return new StrategyWaitHandle(unhandledExceptions);
    }

    @Override
    public <T> WaitHandle forEach(final List<T> list, final Consumer<T> itemHandler) {
        return forEach(list.iterator(), itemHandler);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class InternalParameters implements Context.ParallelismParameters {
        @Override
        public boolean enabled() {
            return false;
        }

        @Override
        public int numberOfThreads() {
            return 1;
        }
    }
}
