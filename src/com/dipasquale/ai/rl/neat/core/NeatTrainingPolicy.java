package com.dipasquale.ai.rl.neat.core;

public interface NeatTrainingPolicy {
    NeatTrainingResult test(NeatActivator activator);

    void reset();
}
