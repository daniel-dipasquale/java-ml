package com.dipasquale.ai.rl.neat;

import com.dipasquale.common.FloatValue;
import com.dipasquale.common.concurrent.AtomicFloatValue;
import com.dipasquale.synchronization.InterruptedRuntimeException;
import com.dipasquale.synchronization.event.loop.ElementHandler;
import com.dipasquale.synchronization.event.loop.ParallelEventLoop;
import com.dipasquale.synchronization.wait.handle.InteractiveWaitHandle;
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
final class MultiThreadContextParallelismSupport implements Context.ParallelismSupport {
    private final InternalParameters params = new InternalParameters();
    private final ParallelEventLoop eventLoop;

    @Override
    public Context.ParallelismParameters params() {
        return params;
    }

    @Override
    public FloatValue createFloatValue(final float initialValue) {
        return new AtomicFloatValue(initialValue);
    }

    private static void forEach(final InteractiveWaitHandleFactory interactiveWaitHandleFactory) {
        Collection<Throwable> unhandledExceptions = Collections.synchronizedSet(Collections.newSetFromMap(new IdentityHashMap<>()));
        InteractiveWaitHandle invokedWaitHandle = interactiveWaitHandleFactory.create(unhandledExceptions);

        try {
            new StrategyWaitHandle(invokedWaitHandle, unhandledExceptions).await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

            throw new InterruptedRuntimeException("thread was interrupted", e);
        }
    }

    @Override
    public <T> void forEach(final Iterator<T> iterator, final Consumer<T> elementHandler) {
        forEach(unhandledExceptions -> eventLoop.queue(iterator, ElementHandler.adapt(elementHandler), unhandledExceptions::add));
    }

    @Override
    public <T> void forEach(final List<T> list, final Consumer<T> elementHandler) {
        forEach(unhandledExceptions -> eventLoop.queue(list, ElementHandler.adapt(elementHandler), unhandledExceptions::add));
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private final class InternalParameters implements Context.ParallelismParameters {
        @Override
        public boolean enabled() {
            return true;
        }

        @Override
        public int numberOfThreads() {
            return eventLoop.getThreadIds().size();
        }
    }

    @FunctionalInterface
    private interface InteractiveWaitHandleFactory {
        InteractiveWaitHandle create(Collection<Throwable> unhandledExceptions);
    }
}
