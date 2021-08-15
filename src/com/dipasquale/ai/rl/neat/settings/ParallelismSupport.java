package com.dipasquale.ai.rl.neat.settings;

import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.ai.rl.neat.context.DefaultContextParallelismSupport;
import com.dipasquale.ai.rl.neat.context.DefaultContextParallelismSupportMultiThread;
import com.dipasquale.ai.rl.neat.context.DefaultContextParallelismSupportSingleThread;
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

    DefaultContextParallelismSupport create() {
        if (!isEnabled()) {
            Context.ParallelismSupport parallelism = new DefaultContextParallelismSupportSingleThread();

            return new DefaultContextParallelismSupport(parallelism);
        }

        Context.ParallelismSupport parallelism = new DefaultContextParallelismSupportMultiThread(eventLoop);

        return new DefaultContextParallelismSupport(parallelism);
    }
}
