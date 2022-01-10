package com.dipasquale.ai.common.function.loss;

@FunctionalInterface
public interface LossFunction {
    LossFunction MEAN_SQUARE_ERROR = (target, output) -> {
        float difference = 0f;

        for (int i = 0; i < target.length; i++) {
            difference += (float) Math.pow(target[i] - output[i], 2D);
        }

        return (float) Math.sqrt(difference / target.length);
    };

    float calculate(float[] target, float[] output);
}
