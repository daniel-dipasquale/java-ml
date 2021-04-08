package com.dipasquale.common;

import java.io.Serial;

final class RandomSupportFloatMeanDistribution implements RandomSupportFloat { // clever idea from: https://stackoverflow.com/questions/30492259/get-a-random-number-focused-on-center
    @Serial
    private static final long serialVersionUID = -2580047376540693357L;
    private final RandomSupportFloat randomSupport;
    private final float concentration;
    private final float multiplier;

    RandomSupportFloatMeanDistribution(final RandomSupportFloat randomSupport, final int concentration) {
        float concentrationFixed = (float) concentration;

        this.randomSupport = randomSupport;
        this.concentration = concentrationFixed;
        this.multiplier = 1f / concentrationFixed;
    }

    @Override
    public float next() {
        float random = 0f;

        for (int i = 0; i < concentration; i++) {
            random += randomSupport.next() * multiplier;
        }

        return random;
    }
}
