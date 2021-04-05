package com.dipasquale.ai.common;

import java.io.Serializable;

@FunctionalInterface
public interface LearningRatePolicyFunction extends Serializable {
    float calculate(float rate, int size); // https://github.com/wagenaartje/neataptic/blob/master/src/methods/rate.js
}
