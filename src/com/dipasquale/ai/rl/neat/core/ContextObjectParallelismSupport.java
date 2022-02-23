package com.dipasquale.ai.rl.neat.core;

import com.dipasquale.synchronization.event.loop.IterableEventLoop;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
final class ContextObjectParallelismSupport implements Context.ParallelismSupport {
    private Context.ParallelismSupport parallelism;

    @Override
    public Context.ParallelismParameters params() {
        return parallelism.params();
    }

    @Override
    public <T> void forEach(final Iterator<T> iterator, final Consumer<T> itemHandler) {
        parallelism.forEach(iterator, itemHandler);
    }

    @Override
    public <T> void forEach(final List<T> list, final Consumer<T> itemHandler) {
        parallelism.forEach(list, itemHandler);
    }

    public void load(final IterableEventLoop eventLoop) {
        if (eventLoop != null) {
            parallelism = new MultiThreadContextParallelismSupport(eventLoop);
        } else {
            parallelism = new SingleThreadContextParallelismSupport();
        }
    }
}
