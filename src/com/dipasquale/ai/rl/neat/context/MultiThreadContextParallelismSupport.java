package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.synchronization.event.loop.IterableEventLoop;
import com.dipasquale.synchronization.wait.handle.InteractiveWaitHandle;
import com.dipasquale.synchronization.wait.handle.WaitHandle;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.function.Consumer;

@RequiredArgsConstructor
public final class MultiThreadContextParallelismSupport implements Context.ParallelismSupport {
    private final DefaultParameters params = new DefaultParameters();
    private final IterableEventLoop eventLoop;

    @Override
    public Context.ParallelismParameters params() {
        return params;
    }

    @Override
    public <T> WaitHandle forEach(final Iterator<T> iterator, final Consumer<T> itemHandler) {
        Collection<Throwable> unhandledExceptions = Collections.synchronizedSet(Collections.newSetFromMap(new IdentityHashMap<>()));
        InteractiveWaitHandle invokedWaitHandle = eventLoop.queue(iterator, itemHandler, unhandledExceptions::add);

        return new ParallelismWaitHandle(invokedWaitHandle, unhandledExceptions);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private final class DefaultParameters implements Context.ParallelismParameters {
        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public int numberOfThreads() {
            return eventLoop.getConcurrencyLevel();
        }
    }
}
