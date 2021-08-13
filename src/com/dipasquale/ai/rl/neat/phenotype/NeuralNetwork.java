/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.ai.rl.neat.phenotype;

public interface NeuralNetwork {
    float[] activate(float[] input);

    void reset();
}
