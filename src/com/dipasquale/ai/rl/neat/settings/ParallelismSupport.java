package com.dipasquale.ai.rl.neat.settings;

import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.ai.rl.neat.context.DefaultContextParallelismSupport;
import com.dipasquale.ai.rl.neat.context.MultiThreadContextParallelismSupport;
import com.dipasquale.ai.rl.neat.context.SingleThreadContextParallelismSupport;
import com.dipasquale.synchronization.dual.mode.data.structure.deque.DualModeDequeFactory;
import com.dipasquale.synchronization.dual.mode.data.structure.map.DualModeMapFactory;
import com.dipasquale.synchronization.event.loop.IterableEventLoop;
import lombok.Builder;
import lombok.Getter;

public final class ParallelismSupport {
    private final IterableEventLoop eventLoop;
    @Getter
    private final DualModeMapFactory mapFactory;
    @Getter
    private final DualModeDequeFactory dequeFactory;

    private ParallelismSupport(final IterableEventLoop eventLoop, final int concurrencyLevel, final int maximumConcurrencyLevel) {
        this.eventLoop = eventLoop;
        this.mapFactory = new DualModeMapFactory(concurrencyLevel, maximumConcurrencyLevel);
        this.dequeFactory = new DualModeDequeFactory(concurrencyLevel, maximumConcurrencyLevel);
    }

    @Builder
    private ParallelismSupport(final IterableEventLoop eventLoop) {
        this(eventLoop, getConcurrencyLevel(eventLoop), getMaximumConcurrencyLevel(eventLoop));
    }

    public static int getConcurrencyLevel(final IterableEventLoop eventLoop) {
        if (eventLoop == null) {
            return 0;
        }

        return eventLoop.getConcurrencyLevel();
    }

    private static int getMaximumConcurrencyLevel(final IterableEventLoop eventLoop) {
        return Math.max(getConcurrencyLevel(eventLoop), Runtime.getRuntime().availableProcessors());
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
