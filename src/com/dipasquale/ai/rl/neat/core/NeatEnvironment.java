package com.dipasquale.ai.rl.neat.core;

import com.dipasquale.ai.common.fitness.FitnessFunction;
import com.dipasquale.ai.rl.neat.phenotype.GenomeActivator;

import java.io.Serializable;

@FunctionalInterface
public interface NeatEnvironment extends FitnessFunction<GenomeActivator>, Serializable {
}
