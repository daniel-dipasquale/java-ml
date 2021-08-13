/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.ai.rl.neat.core;

public interface NeatActivator {
    int getGeneration();

    int getSpeciesCount();

    float getFitness();

    float[] activate(float[] inputs);
}
