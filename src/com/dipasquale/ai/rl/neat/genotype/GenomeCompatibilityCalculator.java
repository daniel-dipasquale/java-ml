package com.dipasquale.ai.rl.neat.genotype;

@FunctionalInterface
public interface GenomeCompatibilityCalculator {
    float calculateCompatibility(GenomeDefault genome1, GenomeDefault genome2);
}
