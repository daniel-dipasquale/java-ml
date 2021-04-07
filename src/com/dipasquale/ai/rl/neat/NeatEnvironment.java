package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.rl.neat.genotype.Genome;

import java.io.Serializable;

@FunctionalInterface
public interface NeatEnvironment extends Serializable {
    float test(Genome genome);
}
