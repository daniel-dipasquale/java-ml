package com.dipasquale.ai.rl.neat;

public interface NeatTrainingPolicy {
    NeatTrainingResult test(NeatActivator activator);

    void reset();

    NeatTrainingPolicy createClone();
}
