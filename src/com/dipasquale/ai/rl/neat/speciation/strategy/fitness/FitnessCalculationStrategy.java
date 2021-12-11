package com.dipasquale.ai.rl.neat.speciation.strategy.fitness;

@FunctionalInterface
public interface FitnessCalculationStrategy {
    void calculate(FitnessCalculationContext context);
}
