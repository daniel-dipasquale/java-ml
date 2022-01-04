package com.dipasquale.ai.rl.neat.core;

import com.dipasquale.ai.rl.neat.phenotype.GenomeActivator;

@FunctionalInterface
public interface ContestNeatEnvironment extends NeatEnvironment {
    float[] test(GenomeActivator[] genomeActivator);
}
