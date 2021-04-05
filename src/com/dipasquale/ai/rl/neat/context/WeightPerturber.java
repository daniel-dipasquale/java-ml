package com.dipasquale.ai.rl.neat.context;

@FunctionalInterface
public interface WeightPerturber {
    float next(float value);
}
