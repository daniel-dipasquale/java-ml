package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.common.fitness.FitnessFunction;
import com.dipasquale.ai.rl.neat.phenotype.GenomeActivator;

@FunctionalInterface
public interface IsolatedNeatEnvironment extends FitnessFunction<GenomeActivator>, NeatEnvironment {
}
