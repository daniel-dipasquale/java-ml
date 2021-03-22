package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.common.RandomSupportFloat;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class ContextDefaultRandom implements Context.Random {
    private final RandomSupportFloat nextIndex;
    private final RandomSupportFloat isLessThan;

    @Override
    public int nextIndex(final int offset, final int count) {
        return nextIndex.next(offset, count);
    }

    @Override
    public boolean isLessThan(final float rate) {
        return isLessThan.isLessThan(rate);
    }
}
