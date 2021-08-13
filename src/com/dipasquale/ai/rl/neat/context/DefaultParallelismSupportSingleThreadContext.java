/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.threading.wait.handle.WaitHandle;

import java.util.Iterator;
import java.util.function.Consumer;

public final class DefaultParallelismSupportSingleThreadContext implements Context.ParallelismSupport {
    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public int numberOfThreads() {
        return 1;
    }

    @Override
    public <T> WaitHandle forEach(final Iterator<T> iterator, final Consumer<T> action) {
        while (iterator.hasNext()) {
            action.accept(iterator.next());
        }

        return WaitHandle.getEmpty();
    }
}
