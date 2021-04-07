package com.dipasquale.ai.common;

import com.dipasquale.common.FloatFactory;

import java.io.Serializable;

@FunctionalInterface
public interface WeightPerturber extends Serializable {
    float perturb(float value);

    static WeightPerturber create(final FloatFactory factory) {
        return new WeightPerturberDefault(factory);
    }
}
