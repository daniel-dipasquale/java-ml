package com.dipasquale.ai.rl.neat;

@FunctionalInterface
public interface FitnessCalculator {
    float calculate(Genome genome);
}
