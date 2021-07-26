package com.dipasquale.ai.rl.neat.settings;

import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.ai.rl.neat.context.DefaultParallelismSupportContext;
import com.dipasquale.ai.rl.neat.context.DefaultParallelismSupportMultiThreadContext;
import com.dipasquale.ai.rl.neat.context.DefaultParallelismSupportSingleThreadContext;
import com.dipasquale.threading.event.loop.EventLoopIterable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public final class ParallelismSupportSettings {
    @Builder.Default
    private final EventLoopIterable eventLoop = null;

    boolean isEnabled() {
        return eventLoop != null;
    }

    DefaultParallelismSupportContext create() {
        if (!isEnabled()) {
            Context.ParallelismSupport parallelism = new DefaultParallelismSupportSingleThreadContext();

            return new DefaultParallelismSupportContext(parallelism);
        }

        Context.ParallelismSupport parallelism = new DefaultParallelismSupportMultiThreadContext(eventLoop);

        return new DefaultParallelismSupportContext(parallelism);
    }
}
