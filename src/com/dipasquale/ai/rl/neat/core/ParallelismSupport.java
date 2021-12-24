package com.dipasquale.ai.rl.neat.core;

import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.ai.rl.neat.context.DefaultContextParallelismSupport;
import com.dipasquale.ai.rl.neat.context.MultiThreadContextParallelismSupport;
import com.dipasquale.ai.rl.neat.context.SingleThreadContextParallelismSupport;
import com.dipasquale.synchronization.event.loop.IterableEventLoop;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public final class ParallelismSupport {
    private final IterableEventLoop eventLoop;

    public static int getConcurrencyLevel(final IterableEventLoop eventLoop) {
        if (eventLoop == null) {
            return 0;
        }

        return eventLoop.getConcurrencyLevel();
    }

    public boolean isEnabled() {
        return eventLoop != null;
    }

    public int getConcurrencyLevel() {
        return getConcurrencyLevel(eventLoop);
    }

    DefaultContextParallelismSupport create() {
        if (!isEnabled()) {
            Context.ParallelismSupport parallelismSupport = new SingleThreadContextParallelismSupport();

            return new DefaultContextParallelismSupport(parallelismSupport);
        }

        Context.ParallelismSupport parallelismSupport = new MultiThreadContextParallelismSupport(eventLoop);

        return new DefaultContextParallelismSupport(parallelismSupport);
    }
}
