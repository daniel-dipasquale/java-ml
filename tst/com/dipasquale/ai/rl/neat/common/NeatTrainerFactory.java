package com.dipasquale.ai.rl.neat.common;

import com.dipasquale.ai.rl.neat.EvaluatorSettings;
import com.dipasquale.ai.rl.neat.NeatTrainer;
import com.dipasquale.ai.rl.neat.NeatTrainingPolicy;

@FunctionalInterface
public interface NeatTrainerFactory {
    NeatTrainer create(EvaluatorSettings settings, NeatTrainingPolicy trainingPolicy);
}
