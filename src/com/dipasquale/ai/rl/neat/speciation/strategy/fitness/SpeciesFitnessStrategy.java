package com.dipasquale.ai.rl.neat.speciation.strategy.fitness;

@FunctionalInterface
public interface SpeciesFitnessStrategy {
    void update(SpeciesFitnessContext context);
}
