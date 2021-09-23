package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.common.serialization.SerializableStateGroup;
import com.dipasquale.synchronization.event.loop.IterableEventLoop;
import com.dipasquale.synchronization.wait.handle.WaitHandle;
import lombok.AllArgsConstructor;

import java.util.Iterator;
import java.util.function.Consumer;

@AllArgsConstructor
public final class DefaultContextParallelismSupport implements Context.ParallelismSupport {
    private Context.ParallelismSupport parallelismSupport;

    @Override
    public Context.ParallelismParameters params() {
        return parallelismSupport.params();
    }

    @Override
    public <T> WaitHandle forEach(final Iterator<T> iterator, final Consumer<T> itemHandler) {
        return parallelismSupport.forEach(iterator, itemHandler);
    }

    public void save(final SerializableStateGroup state) {
    }

    public void load(final SerializableStateGroup state, final IterableEventLoop eventLoop) {
        if (eventLoop != null) {
            parallelismSupport = new MultiThreadContextParallelismSupport(eventLoop);
        } else {
            parallelismSupport = new SingleThreadContextParallelismSupport();
        }
    }
}
