package com.dipasquale.ai.common;

public interface FitnessDeterminer {
    float get();

    void add(float fitness);

    void clear();
}
