package com.dipasquale.ai.common.fitness;

public interface FitnessDeterminer {
    float get();

    void add(float fitness);

    void clear();
}
