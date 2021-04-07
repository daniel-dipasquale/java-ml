package com.dipasquale.ai.rl.neat.genotype;

import java.io.Serializable;

@FunctionalInterface
public interface GenomeCompatibilityCalculator extends Serializable {
    double calculateCompatibility(GenomeDefault genome1, GenomeDefault genome2);
}
