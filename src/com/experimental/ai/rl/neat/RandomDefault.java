package com.experimental.ai.rl.neat;

import com.dipasquale.common.RandomSupportFloat;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class RandomDefault implements Context.Random {
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
    public boolean isAtMost(final float rate) {
        return isAtMost.isAtMost(rate);
    }
}
