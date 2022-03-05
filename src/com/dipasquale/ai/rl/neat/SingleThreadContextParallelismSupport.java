package com.dipasquale.ai.rl.neat;

import com.dipasquale.synchronization.InterruptedRuntimeException;
import com.dipasquale.synchronization.wait.handle.StrategyWaitHandle;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class SingleThreadContextParallelismSupport implements Context.ParallelismSupport {
    private final InternalParameters params = new InternalParameters();
    private final Collection<Throwable> unhandledExceptions = Collections.newSetFromMap(new IdentityHashMap<>());

    @Override
    public Context.ParallelismParameters params() {
        return params;
    }

    @Override
    public <T> void forEach(final Iterator<T> iterator, final Consumer<T> itemHandler) {
        while (iterator.hasNext()) {
            try {
                itemHandler.accept(iterator.next());
            } catch (Throwable e) {
                unhandledExceptions.add(e);
            }
        }

        try {
            new StrategyWaitHandle(null, unhandledExceptions, true).await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

            throw new InterruptedRuntimeException("thread was interrupted", e);
        }
    }

    @Override
    public <T> void forEach(final List<T> list, final Consumer<T> itemHandler) {
        forEach(list.iterator(), itemHandler);
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
