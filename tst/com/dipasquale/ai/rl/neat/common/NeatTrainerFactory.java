package com.dipasquale.ai.rl.neat.common;

import com.dipasquale.ai.rl.neat.NeatSettings;
import com.dipasquale.ai.rl.neat.NeatTrainer;
import com.dipasquale.ai.rl.neat.NeatTrainingPolicy;

@FunctionalInterface
public interface NeatTrainerFactory {
    NeatTrainer create(NeatSettings settings, NeatTrainingPolicy trainingPolicy);
}
