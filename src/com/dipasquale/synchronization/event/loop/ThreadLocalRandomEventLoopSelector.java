package com.dipasquale.synchronization.event.loop;

import com.dipasquale.common.random.float1.RandomSupport;
import com.dipasquale.common.random.float1.ThreadLocalUniformRandomSupport;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class ThreadLocalRandomEventLoopSelector implements EventLoopSelector {
    private static final RandomSupport RANDOM_SUPPORT = new ThreadLocalUniformRandomSupport();
    private final int size;

    @Override
    public int next() {
        return RANDOM_SUPPORT.next(0, size);
    }

    @Override
    public int size() {
        return size;
    }
}
