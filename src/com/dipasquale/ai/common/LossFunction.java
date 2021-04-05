package com.dipasquale.ai.common;

import java.io.Serializable;

@FunctionalInterface
public interface LossFunction extends Serializable {
    LossFunction MEAN_SQUARE_ERROR = (t, o) -> {
        float difference = 0f;

        for (int i = 0; i < t.length; i++) {
            difference += (float) Math.pow(t[i] - o[i], 2D);
        }

        return (float) Math.sqrt(difference / t.length);
    };

    float calculate(float[] target, float[] output);
}
