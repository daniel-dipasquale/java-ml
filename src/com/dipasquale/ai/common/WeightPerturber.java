package com.dipasquale.ai.common;

import com.dipasquale.common.FloatFactory;

import java.io.Serializable;

public interface WeightPerturber extends Serializable {
    float perturb(float value);

    WeightPerturber selectContended(boolean contended);

    static WeightPerturber create(final FloatFactory factory) {
        return new WeightPerturberDefault(factory);
    }
}
