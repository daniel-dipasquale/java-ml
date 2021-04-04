package com.dipasquale.threading.event.loop;

import com.dipasquale.common.RandomSupport;

public interface EventLoopSelector {
    int next();

    int size();

    static EventLoopSelector createRandom(final RandomSupport randomSupport, final int size) {
        return new EventLoopSelector() {
            @Override
            public int next() {
                return (int) randomSupport.next(0L, size);
            }

            @Override
            public int size() {
                return size;
            }
        };
    }

    static EventLoopSelector createRandom(final boolean contended, final int size) {
        if (contended) {
            return createRandom(RandomSupport.createConcurrent(), size);
        }

        return createRandom(RandomSupport.create(), size);
    }
}
