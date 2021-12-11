package com.dipasquale.ai.rl.neat.speciation.strategy.reproduction;

@FunctionalInterface
public interface ReproductionStrategy {
    void reproduce(ReproductionContext context);
}
