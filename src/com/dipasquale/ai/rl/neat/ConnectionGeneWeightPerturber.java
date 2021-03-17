package com.dipasquale.ai.rl.neat;

@FunctionalInterface
public interface ConnectionGeneWeightPerturber {
    float next(float value);
}
