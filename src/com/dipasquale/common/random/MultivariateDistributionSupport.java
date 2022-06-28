package com.dipasquale.common.random;

@FunctionalInterface
public interface MultivariateDistributionSupport {
    double[] nextRandom(double[] biases);
}
