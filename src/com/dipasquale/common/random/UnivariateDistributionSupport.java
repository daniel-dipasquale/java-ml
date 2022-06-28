package com.dipasquale.common.random;

@FunctionalInterface
public interface UnivariateDistributionSupport {
    double nextRandom(double bias, double weight);
}
