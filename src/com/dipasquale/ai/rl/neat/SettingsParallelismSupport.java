package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.ai.rl.neat.context.ContextDefaultParallelismSupport;
import com.dipasquale.ai.rl.neat.context.ContextDefaultParallelismSupportMultiThread;
import com.dipasquale.ai.rl.neat.context.ContextDefaultParallelismSupportSingleThread;
import com.dipasquale.common.random.RandomSupportFloat;
import com.dipasquale.threading.event.loop.EventLoopIterable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public final class SettingsParallelismSupport {
    @Builder.Default
    private final EventLoopIterable eventLoop = null;

    boolean isEnabled() {
        return eventLoop != null;
    }

    int getNumberOfThreads() {
        if (!isEnabled()) {
            return 1;
        }

        return eventLoop.getConcurrencyLevel();
    }

    RandomSupportFloat getRandomSupport(final SettingsRandomType type) {
        return SettingsConstants.getRandomSupport(type, isEnabled());
    }

    ContextDefaultParallelismSupport create() {
        if (!isEnabled()) {
            Context.ParallelismSupport parallelism = new ContextDefaultParallelismSupportSingleThread();

            return new ContextDefaultParallelismSupport(parallelism);
        }

        Context.ParallelismSupport parallelism = new ContextDefaultParallelismSupportMultiThread(eventLoop);

        return new ContextDefaultParallelismSupport(parallelism);
    }
}
