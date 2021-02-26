package com.dipasquale.ai.rl.neat;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class CrossOverDefault implements Context.CrossOver {
    private final float rate;
    private final float disableExpressedInheritanceRate;

    @Override
    public float rate() {
        return rate;
    }

    @Override
    public float disableExpressedInheritanceRate() {
        return disableExpressedInheritanceRate;
    }
}
