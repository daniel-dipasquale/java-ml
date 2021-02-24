package com.experimental.ai;

@FunctionalInterface
public interface LossFunction {
    float calculate(float[] target, float[] output); // https://github.com/wagenaartje/neataptic/blob/master/src/methods/cost.js
}
