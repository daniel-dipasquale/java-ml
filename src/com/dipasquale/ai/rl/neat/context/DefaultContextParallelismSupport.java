package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.common.SerializableInteroperableStateMap;
import com.dipasquale.threading.event.loop.IterableEventLoop;
import com.dipasquale.threading.wait.handle.WaitHandle;
import lombok.AllArgsConstructor;

import java.util.Iterator;
import java.util.function.Consumer;

@AllArgsConstructor
public final class DefaultContextParallelismSupport implements Context.ParallelismSupport {
    private Context.ParallelismSupport parallelism;

    @Override
    public boolean isEnabled() {
        return parallelism.isEnabled();
    }

    @Override
    public int numberOfThreads() {
        return parallelism.numberOfThreads();
    }

    @Override
    public <T> WaitHandle forEach(final Iterator<T> iterator, final Consumer<T> itemHandler) {
        return parallelism.forEach(iterator, itemHandler);
    }

    public void save(final SerializableInteroperableStateMap state) {
    }

    public void load(final SerializableInteroperableStateMap state, final IterableEventLoop eventLoop) {
        if (eventLoop != null) {
            parallelism = new DefaultContextParallelismSupportMultiThread(eventLoop);
        } else {
            parallelism = new DefaultContextParallelismSupportSingleThread();
        }
    }
}
