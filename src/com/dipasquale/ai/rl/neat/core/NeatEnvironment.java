/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.ai.rl.neat.core;

import com.dipasquale.ai.common.fitness.FitnessFunction;
import com.dipasquale.ai.rl.neat.genotype.Genome;

import java.io.Serializable;

@FunctionalInterface
public interface NeatEnvironment extends FitnessFunction<Genome>, Serializable {
}
