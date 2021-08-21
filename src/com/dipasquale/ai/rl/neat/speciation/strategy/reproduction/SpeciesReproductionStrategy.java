package com.dipasquale.ai.rl.neat.speciation.strategy.reproduction;

@FunctionalInterface
public interface SpeciesReproductionStrategy {
    void reproduce(SpeciesReproductionContext context);
}
