package com.dipasquale.ai.rl.neat.core;

import com.dipasquale.ai.rl.neat.phenotype.GenomeActivator;

import java.util.List;

@FunctionalInterface
public interface ContestNeatEnvironment extends NeatEnvironment {
    float[] test(List<GenomeActivator> genomeActivator);
}
