package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.rl.neat.phenotype.GenomeActivator;

import java.util.List;

@FunctionalInterface
public interface ContestedNeatEnvironment extends NeatEnvironment {
    float[] test(List<GenomeActivator> genomeActivators, int round);
}
