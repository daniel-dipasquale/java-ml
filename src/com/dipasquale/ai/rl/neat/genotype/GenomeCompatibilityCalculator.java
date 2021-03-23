package com.dipasquale.ai.rl.neat.genotype;

@FunctionalInterface
public interface GenomeCompatibilityCalculator {
    double calculateCompatibility(GenomeDefault genome1, GenomeDefault genome2);
}
