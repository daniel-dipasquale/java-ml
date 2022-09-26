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

    static Set<Long> extractThreadIds(final ParallelEventLoop eventLoop) {
        if (eventLoop == null) {
            return Set.of();
        }

        return eventLoop.getThreadIds();
    }

    public Set<Long> extractThreadIds() {
        return extractThreadIds(eventLoop);
    }

    DefaultNeatContextParallelismSupport create() {
        if (!isEnabled()) {
            NeatContext.ParallelismSupport parallelismSupport = new DefaultNeatContextParallelismSupportSingleThread();

            return new DefaultNeatContextParallelismSupport(parallelismSupport);
        }

        NeatContext.ParallelismSupport parallelismSupport = new DefaultNeatContextParallelismSupportMultiThread(eventLoop);

        return new DefaultNeatContextParallelismSupport(parallelismSupport);
    }
}
