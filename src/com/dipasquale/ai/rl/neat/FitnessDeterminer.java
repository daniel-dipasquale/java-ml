package com.dipasquale.ai.rl.neat;

interface FitnessDeterminer {
    float get();

    void add(float fitness);

    void clear();

    @FunctionalInterface
    interface Factory {
        FitnessDeterminer create();
    }
}
