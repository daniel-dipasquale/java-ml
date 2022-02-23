package com.dipasquale.common.random.float2;

import java.io.Serial;
import java.io.Serializable;
import java.util.Random;

public final class GaussianDistributionSupport implements UnivariateDistributionSupport, Serializable { // TODO: copied from https://github.com/calaylin/privacy-detective/blob/2a826245d0c94e4e257d5b26cb3c8583fe67c35a/twitter-privacy-analytics/mallet-2.0.7/src/cc/mallet/util/Randoms.java
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
