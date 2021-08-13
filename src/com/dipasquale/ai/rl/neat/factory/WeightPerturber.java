/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.ai.rl.neat.factory;

public interface WeightPerturber {
    float perturb(float value);
}
