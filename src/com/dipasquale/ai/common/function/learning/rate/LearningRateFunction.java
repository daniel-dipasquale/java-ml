/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.ai.common.function.learning.rate;

@FunctionalInterface
public interface LearningRateFunction {
    float calculate(float rate, int size); // https://github.com/wagenaartje/neataptic/blob/master/src/methods/rate.js
}
