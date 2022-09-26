package com.dipasquale.ai.rl.neat;

import com.dipasquale.common.FloatValue;
import com.dipasquale.synchronization.event.loop.ParallelEventLoop;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class DefaultNeatContextParallelismSupport implements NeatContext.ParallelismSupport {
    private final NeatContext.ParallelismSupport parallelism;

    @Override
    public NeatContext.ParallelismParameters getParams() {
        return parallelism.getParams();
    }

    @Override
    public FloatValue createFloatValue(final float initialValue) {
        return parallelism.createFloatValue(initialValue);
    }

    @Override
    public <T> void forEach(final Iterator<T> iterator, final Consumer<T> elementHandler) {
        parallelism.forEach(iterator, elementHandler);
    }

    @Override
    public <T> void forEach(final List<T> list, final Consumer<T> elementHandler) {
        parallelism.forEach(list, elementHandler);
    }

    private static NeatContext.ParallelismSupport createStrategy(final ParallelEventLoop eventLoop) {
        if (eventLoop == null) {
            return new DefaultNeatContextParallelismSupportSingleThread();
        }

        return new DefaultNeatContextParallelismSupportMultiThread(eventLoop);
    }

    static DefaultNeatContextParallelismSupport create(final ParallelEventLoop eventLoop) {
        return new DefaultNeatContextParallelismSupport(createStrategy(eventLoop));
    }
}
