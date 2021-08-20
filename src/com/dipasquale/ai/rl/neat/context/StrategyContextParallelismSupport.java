package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.common.SerializableInteroperableStateMap;
import com.dipasquale.threading.event.loop.IterableEventLoop;
import com.dipasquale.threading.wait.handle.WaitHandle;
import lombok.AllArgsConstructor;

import java.util.Iterator;
import java.util.function.Consumer;

@AllArgsConstructor
public final class StrategyContextParallelismSupport implements Context.ParallelismSupport {
    private Context.ParallelismSupport parallelism;

    @Override
    public Context.ParallelismParameters params() {
        return parallelism.params();
    }

    @Override
    public <T> WaitHandle forEach(final Iterator<T> iterator, final Consumer<T> itemHandler) {
        return parallelism.forEach(iterator, itemHandler);
    }

    public void save(final SerializableInteroperableStateMap state) {
    }

    public void load(final SerializableInteroperableStateMap state, final IterableEventLoop eventLoop) {
        if (eventLoop != null) {
            parallelism = new MultiThreadContextParallelismSupport(eventLoop);
        } else {
            parallelism = new SingleThreadContextParallelismSupport();
        }
    }
}
