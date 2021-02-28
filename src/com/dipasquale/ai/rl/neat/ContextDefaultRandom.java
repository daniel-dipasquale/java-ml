package com.dipasquale.ai.rl.neat;

import com.dipasquale.common.RandomSupportFloat;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class ContextDefaultRandom implements Context.Random {
    private final RandomSupportFloat nextIndex;
    private final RandomSupportFloat next;
    private final RandomSupportFloat isAtMost;

    @Override
    public int nextIndex(final int count) {
        return nextIndex.next(0, count);
    }

    @Override
    public float next() {
        return next.next();
    }

    @Override
    public boolean isLessThan(final float rate) {
        return isAtMost.isLessThan(rate);
    }
}
