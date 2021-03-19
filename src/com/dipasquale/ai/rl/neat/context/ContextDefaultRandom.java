package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.common.RandomSupportFloat;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class ContextDefaultRandom implements Context.Random {
    private final RandomSupportFloat nextIndex;
    private final RandomSupportFloat next;
    private final RandomSupportFloat isAtMost;

    @Override
    public int nextIndex(final int offset, final int count) {
        return nextIndex.next(offset, count);
    }

    @Override
    public float next() {
        return next.next();
    }

    @Override
    public float next(final float min, final float max) {
        return next.next(min, max);
    }

    @Override
    public boolean isLessThan(final float rate) {
        return isAtMost.isLessThan(rate);
    }
}
