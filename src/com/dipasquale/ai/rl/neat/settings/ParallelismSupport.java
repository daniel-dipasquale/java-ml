package com.dipasquale.ai.rl.neat.settings;

import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.ai.rl.neat.context.DefaultContextParallelismSupport;
import com.dipasquale.ai.rl.neat.context.MultiThreadContextParallelismSupport;
import com.dipasquale.ai.rl.neat.context.SingleThreadContextParallelismSupport;
import com.dipasquale.synchronization.event.loop.IterableEventLoop;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public final class ParallelismSupport {
    @Builder.Default
    private final IterableEventLoop eventLoop = null;

    boolean isEnabled() {
        return eventLoop != null;
    }

    int getNumberOfThreads() {
        if (eventLoop == null) {
            return 1;
        }

        return eventLoop.getConcurrencyLevel();
    }

    DefaultContextParallelismSupport create() {
        if (!isEnabled()) {
            Context.ParallelismSupport parallelism = new SingleThreadContextParallelismSupport();

            return new DefaultContextParallelismSupport(parallelism);
        }

        Context.ParallelismSupport parallelism = new MultiThreadContextParallelismSupport(eventLoop);

        return new DefaultContextParallelismSupport(parallelism);
    }
}
