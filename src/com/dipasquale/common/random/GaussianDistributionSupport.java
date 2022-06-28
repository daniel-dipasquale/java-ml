package com.dipasquale.common.random;

import java.io.Serial;
import java.io.Serializable;
import java.util.Random;

public final class GaussianDistributionSupport implements UnivariateDistributionSupport, Serializable {
    @Serial
    private static final long serialVersionUID = 6939798706382359903L;
    private final Random random;

    public GaussianDistributionSupport() {
        this.random = new Random();
    }

    public GaussianDistributionSupport(final long seed) {
        this.random = new Random(seed);
    }

    @Override
    public double nextRandom(final double mean, final double standardDeviation) {
        return random.nextGaussian(mean, standardDeviation);
    }
}
