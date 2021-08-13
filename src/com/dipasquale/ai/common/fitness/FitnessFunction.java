/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.ai.common.fitness;

@FunctionalInterface
public interface FitnessFunction<T> {
    float test(T value);
}
