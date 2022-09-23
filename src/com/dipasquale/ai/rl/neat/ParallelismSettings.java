package com.dipasquale.ai.rl.neat;

import com.dipasquale.synchronization.event.loop.ParallelEventLoop;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public final class ParallelismSettings {
    private final ParallelEventLoop eventLoop;

    public boolean isEnabled() {
        return eventLoop != null;
    }

    static Set<Long> getThreadIds(final ParallelEventLoop eventLoop) {
        if (eventLoop == null) {
            return Set.of();
        }

        return eventLoop.getThreadIds();
    }

    public Set<Long> getThreadIds() {
        return getThreadIds(eventLoop);
    }

    ContextObjectParallelismSupport create() {
        if (!isEnabled()) {
            Context.ParallelismSupport parallelismSupport = new SingleThreadContextParallelismSupport();

            return new ContextObjectParallelismSupport(parallelismSupport);
        }

        Context.ParallelismSupport parallelismSupport = new MultiThreadContextParallelismSupport(eventLoop);

        return new ContextObjectParallelismSupport(parallelismSupport);
    }
}
