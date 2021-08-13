/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.ai.rl.neat.settings;

import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.ai.rl.neat.context.DefaultParallelismSupportContext;
import com.dipasquale.ai.rl.neat.context.DefaultParallelismSupportMultiThreadContext;
import com.dipasquale.ai.rl.neat.context.DefaultParallelismSupportSingleThreadContext;
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

    DefaultParallelismSupportContext create() {
        if (!isEnabled()) {
            Context.ParallelismSupport parallelism = new DefaultParallelismSupportSingleThreadContext();

            return new DefaultParallelismSupportContext(parallelism);
        }

        Context.ParallelismSupport parallelism = new DefaultParallelismSupportMultiThreadContext(eventLoop);

        return new DefaultParallelismSupportContext(parallelism);
    }
}
