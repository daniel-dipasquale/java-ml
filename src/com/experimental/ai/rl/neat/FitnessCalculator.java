package com.experimental.ai.rl.neat;

@FunctionalInterface
public interface FitnessCalculator {
    float calculate(Genome genome);
}
