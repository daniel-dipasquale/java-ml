package com.dipasquale.ai.rl.neat.genotype;

@FunctionalInterface
public interface GenomeCompatibilityCalculator {
    double calculateCompatibility(Genome genome1, Genome genome2);
}
