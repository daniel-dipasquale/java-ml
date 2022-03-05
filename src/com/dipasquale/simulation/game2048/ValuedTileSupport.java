package com.dipasquale.simulation.game2048;

import com.dipasquale.common.random.float1.RandomSupport;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class ValuedTileSupport {
    private static final int MAXIMUM_SPAWNING_VALUE = 2;
    private final RandomSupport locationRandomSupport;
    private final RandomSupport valueRandomSupport;

    public int generateId(final int minimum, final int maximum) {
        return locationRandomSupport.next(minimum, maximum);
    }

    public int generateValue(final float threshold) {
        if (!valueRandomSupport.isLessThan(threshold)) {
            return MAXIMUM_SPAWNING_VALUE;
        }

        return MAXIMUM_SPAWNING_VALUE - 1;
    }
}
