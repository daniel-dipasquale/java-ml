package com.dipasquale.ai.rl.neat;

import com.dipasquale.common.FloatValue;
import com.dipasquale.common.StandardFloatValue;
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
final class DefaultNeatContextParallelismSupportSingleThread implements NeatContext.ParallelismSupport {
    private final InternalParameters params = new InternalParameters();
    private final Collection<Throwable> unhandledExceptions = Collections.newSetFromMap(new IdentityHashMap<>());

    @Override
    public NeatContext.ParallelismParameters getParams() {
        return params;
    }

    @Override
    public FloatValue createFloatValue(final float initialValue) {
        return new StandardFloatValue(initialValue);
    }

    @Override
    public <T> void forEach(final Iterator<T> iterator, final Consumer<T> elementHandler) {
        while (iterator.hasNext()) {
            try {
                elementHandler.accept(iterator.next());
            } catch (Throwable e) {
                unhandledExceptions.add(e);
            }
        }

        try {
            StrategyWaitHandle waitHandle = new StrategyWaitHandle(null, unhandledExceptions);

            waitHandle.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

            throw new InterruptedRuntimeException("thread was interrupted", e);
        } finally {
            unhandledExceptions.clear();
        }
    }

    @Override
    public <T> void forEach(final List<T> list, final Consumer<T> elementHandler) {
        forEach(list.iterator(), elementHandler);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class InternalParameters implements NeatContext.ParallelismParameters {
        @Override
        public boolean isEnabled() {
            return false;
        }

        @Override
        public int getNumberOfThreads() {
            return 1;
        }
    }
}
