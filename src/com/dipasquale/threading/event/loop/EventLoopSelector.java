package com.dipasquale.threading.event.loop;

import com.dipasquale.common.random.float1.RandomSupport;

public interface EventLoopSelector {
    int next();

    int size();

    static EventLoopSelector createRandom(final RandomSupport randomSupport, final int size) {
        return new EventLoopSelector() {
            @Override
            public int next() {
                return randomSupport.next(0, size);
            }

            @Override
            public int size() {
                return size;
            }
        };
    }

    static EventLoopSelector createRandom(final boolean contended, final int size) {
        if (!contended) {
            return createRandom(Constants.RANDOM_SUPPORT_UNIFORM, size);
        }

        return createRandom(Constants.RANDOM_SUPPORT_UNIFORM_CONCURRENT, size);
    }
}
