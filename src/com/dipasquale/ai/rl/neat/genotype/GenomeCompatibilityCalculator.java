package com.dipasquale.ai.rl.neat.genotype;

@FunctionalInterface
public interface GenomeCompatibilityCalculator {
    double calculateCompatibility(DefaultGenome genome1, DefaultGenome genome2);
}
