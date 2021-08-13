/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.ai.common.fitness;

public interface FitnessDeterminer {
    float get();

    void add(float fitness);

    void clear();
}
