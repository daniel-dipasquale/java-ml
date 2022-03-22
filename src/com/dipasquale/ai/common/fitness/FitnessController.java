package com.dipasquale.ai.common.fitness;

public interface FitnessController {
    float get();

    void add(float fitness);

    void clear();
}
