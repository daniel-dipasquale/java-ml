package com.dipasquale.ai.rl.neat.core;

import java.io.Serializable;

public interface NeatTrainingHandler extends Serializable {
    boolean test(NeatActivator activator);
}
