package com.dipasquale.synchronization.event.loop;

import com.dipasquale.common.random.float1.RandomSupport;
import com.dipasquale.common.random.float1.ThreadLocalUniformRandomSupport;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ThreadLocalRandomEventLoopSelector implements EventLoopSelector {
    private static final RandomSupport RANDOM_SUPPORT = ThreadLocalUniformRandomSupport.getInstance();
    private static final ThreadLocalRandomEventLoopSelector INSTANCE = new ThreadLocalRandomEventLoopSelector();

    public static ThreadLocalRandomEventLoopSelector getInstance() {
        return INSTANCE;
    }

    @Override
    public int nextIndex(final List<EventLoop> eventLoops) {
        int size = eventLoops.size();

        return RANDOM_SUPPORT.next(0, size);
    }
}
