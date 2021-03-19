package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.rl.neat.genotype.Genome;

@FunctionalInterface
public interface Environment {
    float test(Genome genome);
}
