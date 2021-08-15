package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.threading.wait.handle.WaitHandle;

import java.util.Iterator;
import java.util.function.Consumer;

public final class DefaultContextParallelismSupportSingleThread implements Context.ParallelismSupport {
    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public int numberOfThreads() {
        return 1;
    }

    @Override
    public <T> WaitHandle forEach(final Iterator<T> iterator, final Consumer<T> itemHandler) {
        while (iterator.hasNext()) {
            itemHandler.accept(iterator.next());
        }

        return WaitHandle.getEmpty();
    }
}
