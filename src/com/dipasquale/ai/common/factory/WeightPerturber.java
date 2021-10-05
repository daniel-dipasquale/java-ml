package com.dipasquale.ai.common.factory;

@FunctionalInterface
public interface WeightPerturber {
    float perturb(float value);
}
