package com.dipasquale.ai.rl.neat.context;

@FunctionalInterface
public interface ConnectionGeneWeightPerturber {
    float next(float value);
}
