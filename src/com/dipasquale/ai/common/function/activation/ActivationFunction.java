/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.ai.common.function.activation;

public interface ActivationFunction {
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

    String toString();
}