package com.dipasquale.ai.rl.neat.common;

import com.dipasquale.ai.rl.neat.NeatEnvironment;
import com.dipasquale.ai.rl.neat.NeatTrainingAssessor;

public interface NeatObjective<T extends NeatEnvironment> {
    T getEnvironment();

    NeatTrainingAssessor getTrainingAssessor();
}
