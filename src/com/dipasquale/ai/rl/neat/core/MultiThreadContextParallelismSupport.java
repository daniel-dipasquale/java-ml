package com.dipasquale.ai.rl.neat.core;

import com.dipasquale.synchronization.event.loop.IterableEventLoop;
import com.dipasquale.synchronization.wait.handle.InteractiveWaitHandle;
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

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class MultiThreadContextParallelismSupport implements Context.ParallelismSupport {
    private final InternalParameters params = new InternalParameters();
    private final IterableEventLoop eventLoop;

    @Override
    public Context.ParallelismParameters params() {
        return params;
    }

    private static Collection<Throwable> createUnhandledExceptionsContainer() {
        return Collections.synchronizedSet(Collections.newSetFromMap(new IdentityHashMap<>()));
    }

    @Override
    public <T> WaitHandle forEach(final Iterator<T> iterator, final Consumer<T> itemHandler) {
        Collection<Throwable> unhandledExceptions = createUnhandledExceptionsContainer();
        InteractiveWaitHandle invokedWaitHandle = eventLoop.queue(iterator, itemHandler, unhandledExceptions::add);

        return new StrategyWaitHandle(invokedWaitHandle, unhandledExceptions);
    }

    @Override
    public <T> WaitHandle forEach(final List<T> list, final Consumer<T> itemHandler) {
        Collection<Throwable> unhandledExceptions = createUnhandledExceptionsContainer();
        InteractiveWaitHandle invokedWaitHandle = eventLoop.queue(list, itemHandler, unhandledExceptions::add);

        return new StrategyWaitHandle(invokedWaitHandle, unhandledExceptions);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private final class InternalParameters implements Context.ParallelismParameters {
        @Override
        public boolean enabled() {
            return true;
        }

        @Override
        public int numberOfThreads() {
            return eventLoop.getConcurrencyLevel();
        }
    }
}