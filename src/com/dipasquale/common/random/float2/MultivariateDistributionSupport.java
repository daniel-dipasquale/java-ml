package com.dipasquale.common.random.float2;

@FunctionalInterface
public interface MultivariateDistributionSupport {
    double[] nextRandom(double[] biases);
}
