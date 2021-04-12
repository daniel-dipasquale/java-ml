package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.data.structure.map.SerializableInteroperableStateMap;
import com.dipasquale.threading.event.loop.EventLoopIterable;
import com.dipasquale.threading.wait.handle.WaitHandle;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Iterator;
import java.util.function.Consumer;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public final class ContextDefaultParallelismSupport implements Context.ParallelismSupport {
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
    public <T> WaitHandle forEach(final Iterator<T> iterator, final Consumer<T> action) {
        return parallelism.forEach(iterator, action);
    }

    public void save(final SerializableInteroperableStateMap state) {
    }

    public void load(final SerializableInteroperableStateMap state, final EventLoopIterable eventLoop) {
        if (eventLoop != null) {
            parallelism = new ContextDefaultParallelismSupportMultiThread(eventLoop);
        } else {
            parallelism = new ContextDefaultParallelismSupportSingleThread();
        }
    }
}