package com.dipasquale.ai.rl.neat;

import java.io.Serializable;

@FunctionalInterface
public interface NeatTrainingAssessor extends Serializable {
    boolean test(NeatActivator activator);
}
