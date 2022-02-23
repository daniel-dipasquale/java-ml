package com.dipasquale.common.random.float2;

@FunctionalInterface
public interface UnivariateDistributionSupport {
    double nextRandom(double bias, double weight);
}
