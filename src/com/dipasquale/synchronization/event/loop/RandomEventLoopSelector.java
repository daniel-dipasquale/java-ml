package com.dipasquale.synchronization.event.loop;

import com.dipasquale.common.random.RandomSupport;
import com.dipasquale.common.random.UniformRandomSupport;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class RandomEventLoopSelector implements EventLoopSelector {
    private static final RandomSupport RANDOM_SUPPORT = new UniformRandomSupport();
    private static final RandomEventLoopSelector INSTANCE = new RandomEventLoopSelector();

    public static RandomEventLoopSelector getInstance() {
        return INSTANCE;
    }

    @Override
    public int nextIndex(final List<EventLoop> eventLoops) {
        int size = eventLoops.size();

        return RANDOM_SUPPORT.nextInteger(0, size);
    }
}
