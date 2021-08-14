package com.dipasquale.common.random.float1;

import java.io.Serial;
import java.io.Serializable;

public final class MeanDistributedRandomSupport implements RandomSupport, Serializable { // clever idea from: https://stackoverflow.com/questions/30492259/get-a-random-number-focused-on-center
    @Serial
    private static final long serialVersionUID = -2580047376540693357L;
    private final RandomSupport randomSupport;
    private final float concentration;
    private final float multiplier;

    public MeanDistributedRandomSupport(final RandomSupport randomSupport, final int concentration) {
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
