package com.dipasquale.ai.common;

@FunctionalInterface
public interface ActivationFunction {
    ActivationFunction Identity = i -> i;
    ActivationFunction ReLU = i -> Math.max(0f, i);
    ActivationFunction Sigmoid = i -> 1f / (1f + (float) Math.exp(-i));
    ActivationFunction TanH = i -> (float) Math.tanh(i);

    float forward(float input);

    default float[] forward(final float[] input) {
        float[] output = new float[input.length];

        for (int i = 0; i < input.length; i++) {
            output[i] = forward(input[i]);
        }

        return output;
    }

    default float[][] forward(final float[][] input) {
        float[][] output = new float[input.length][input[0].length];

        for (int i1 = 0, c1 = input.length, c2 = input[0].length; i1 < c1; i1++) {
            for (int i2 = 0; i2 < c2; i2++) {
                output[i1][i2] = forward(input[i1][i2]);
            }
        }

        return output;
    }
}