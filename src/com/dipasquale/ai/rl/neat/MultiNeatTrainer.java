package com.dipasquale.ai.rl.neat;

public interface MultiNeatTrainer extends NeatTrainer {
    NeatTrainer cloneMostEfficientTrainer(EvaluatorOverrideSettings settings);
}
