package com.dipasquale.ai.rl.neat.settings;

import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.ai.rl.neat.context.MultiThreadContextParallelismSupport;
import com.dipasquale.ai.rl.neat.context.SingleThreadContextParallelismSupport;
import com.dipasquale.ai.rl.neat.context.StrategyContextParallelismSupport;
import com.dipasquale.threading.event.loop.IterableEventLoop;
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

    StrategyContextParallelismSupport create() {
        if (!isEnabled()) {
            Context.ParallelismSupport parallelism = new SingleThreadContextParallelismSupport();

            return new StrategyContextParallelismSupport(parallelism);
        }

        Context.ParallelismSupport parallelism = new MultiThreadContextParallelismSupport(eventLoop);

        return new StrategyContextParallelismSupport(parallelism);
    }
}
