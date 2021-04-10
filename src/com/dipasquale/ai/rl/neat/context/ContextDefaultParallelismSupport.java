package com.dipasquale.ai.rl.neat.context;

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

    public void save(final ContextDefaultStateMap state) {
        state.put("parallelism.isEnabled", isEnabled());
    }

    public void load(final ContextDefaultStateMap state) {
        boolean isEnabled = state.get("parallelism.isEnabled");

        if (isEnabled) {
            parallelism = null;
        } else {
            parallelism = new ContextDefaultParallelismSupportSingleThread();
        }
    }
}
