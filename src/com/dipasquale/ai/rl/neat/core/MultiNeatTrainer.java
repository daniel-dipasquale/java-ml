package com.dipasquale.ai.rl.neat.core;

public interface MultiNeatTrainer extends NeatTrainer {
    NeatTrainer extractSingle(EvaluatorOverrideSettings settings);
}
