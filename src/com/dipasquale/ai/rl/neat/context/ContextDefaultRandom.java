package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.common.RandomSupportFloat;
import lombok.RequiredArgsConstructor;

import java.io.Serial;

@RequiredArgsConstructor
public final class ContextDefaultRandom implements Context.Random {
    @Serial
    private static final long serialVersionUID = -742853288111610513L;
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
