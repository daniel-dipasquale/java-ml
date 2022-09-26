package com.dipasquale.ai.rl.neat.speciation.strategy.fitness;

@FunctionalInterface
public interface FitnessEvaluationStrategy {
    void evaluate(FitnessEvaluationContext context);
}
