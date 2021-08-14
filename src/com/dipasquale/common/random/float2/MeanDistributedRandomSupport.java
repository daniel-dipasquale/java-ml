package com.dipasquale.common.random.float2;

import java.io.Serial;
import java.io.Serializable;

public final class MeanDistributedRandomSupport implements RandomSupport, Serializable { // clever idea from: https://stackoverflow.com/questions/30492259/get-a-random-number-focused-on-center
    @Serial
    private static final long serialVersionUID = -1870741538456358942L;
    private final RandomSupport randomSupport;
    private final double concentration;
    private final double multiplier;

    public MeanDistributedRandomSupport(final RandomSupport randomSupport, final int concentration) {
        this.randomSupport = randomSupport;
        this.concentration = concentration;
        this.multiplier = 1D / (double) concentration;
    }

    @Override
    public double next() {
        double random = 0D;

        for (int i = 0; i < concentration; i++) {
            random += randomSupport.next() * multiplier;
        }

        return random;
    }
}
