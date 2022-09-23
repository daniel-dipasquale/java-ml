package com.dipasquale.ai.rl.neat;

public interface ParallelNeatTrainer extends NeatTrainer {
    NeatTrainer cloneMostEfficientTrainer(NeatSettingsOverride settings);
}
