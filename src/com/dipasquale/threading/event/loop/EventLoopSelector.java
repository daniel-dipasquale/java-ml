package com.dipasquale.threading.event.loop;

import com.dipasquale.common.random.RandomSupportFloat;

public interface EventLoopSelector {
    int next();

    int size();

    static EventLoopSelector createRandom(final RandomSupportFloat randomSupport, final int size) {
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
            return createRandom(EventLoopConstants.RANDOM_SUPPORT_UNIFORM, size);
        }

        return createRandom(EventLoopConstants.RANDOM_SUPPORT_UNIFORM_CONCURRENT, size);
    }
}
