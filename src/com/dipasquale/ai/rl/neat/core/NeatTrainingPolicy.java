package com.dipasquale.ai.rl.neat.core;

public interface NeatTrainingPolicy {
    NeatTrainingResult test(NeatActivator activator);

    void reset();

    default NeatTrainingResult testOnce(final NeatActivator activator) {
        try {
            return test(activator);
        } finally {
            reset();
        }
    }
}
