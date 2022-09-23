package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.common.fitness.FitnessFunction;
import com.dipasquale.ai.rl.neat.phenotype.GenomeActivator;

@FunctionalInterface
public interface SecludedNeatEnvironment extends FitnessFunction<GenomeActivator>, NeatEnvironment {
}
