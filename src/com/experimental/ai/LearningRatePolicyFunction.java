package com.experimental.ai;

@FunctionalInterface
public interface LearningRatePolicyFunction {
    float calculate(float rate, int size); // https://github.com/wagenaartje/neataptic/blob/master/src/methods/rate.js
}
