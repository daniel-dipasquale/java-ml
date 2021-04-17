package com.aparapi;

import com.aparapi.device.Device;
import com.aparapi.internal.kernel.KernelManager;
import com.aparapi.internal.kernel.KernelPreferences;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;

public final class KernelTest {
    private static final int PRIME_SIZE = 100_000;
    private static final int NN_INPUTS = 2_000;
    private static final int NN_NEURONS = 1_000;
    private static final int NN_PREVIOUS_INPUTS = 4_000;
    private static final float NN_BIAS = 1f;

    @Test
    public void TEST_1() {
        KernelPreferences preferences = KernelManager.instance().getDefaultPreferences();

        System.out.println("-- Devices in preferred order --");

        for (Device device : preferences.getPreferredDevices(null)) {
            System.out.println("----------");
            System.out.println(device);
        }
    }

    @Test
    @Disabled
    public void TEST_2() {
        int size = PRIME_SIZE;
        int[] a = IntStream.range(2, size + 2).toArray();
        boolean[] primeNumbers = new boolean[size];
        long startTime = System.currentTimeMillis();

        for (int n = 0; n < size; n++) {
            int num = a[n];
            boolean prime = true;
            for (int i = 2; i < num; i++) {
                if (num % i == 0) {
                    prime = false;
                    //not using break for a fair comparision
                }
            }

            primeNumbers[n] = prime;
        }

        System.out.printf("time taken: %s ms%n", System.currentTimeMillis() - startTime);
        System.out.println(Arrays.toString(Arrays.copyOf(primeNumbers, 20)));
    }

    @Test
    @Disabled
    public void TEST_3() {
        int size = PRIME_SIZE;
        int[] a = IntStream.range(2, size + 2).toArray();
        boolean[] primeNumbers = new boolean[size];

        Kernel kernel = new Kernel() {
            @Override
            public void run() {
                int gid = getGlobalId();
                int num = a[gid];
                boolean prime = true;

                for (int i = 2; i < num; i++) {
                    if (num % i == 0) {
                        prime = false;
                    }
                }

                primeNumbers[gid] = prime;
            }
        };

        long startTime = System.currentTimeMillis();

        kernel.execute(Range.create(size));
        kernel.dispose();
        System.out.printf("time taken: %s ms%n", System.currentTimeMillis() - startTime);
        System.out.println(Arrays.toString(Arrays.copyOf(primeNumbers, 20)));
    }

    private static float[][] getRandomMatrix(final int rows, final int cols, final Random random) {
        float[][] output = new float[rows][cols];

        for (int i = 0; i < output.length; i++) {
            for (int j = 0; j < output[0].length; j++) {
                output[i][j] = 0.1f * (float) random.nextGaussian();
            }
        }

        return output;
    }

    private static float[][] getConstantMatrix(final int rows, final int cols, final float value) {
        float[][] output = new float[rows][cols];

        for (int i = 0; i < output.length; i++) {
            for (int j = 0; j < output[0].length; j++) {
                output[i][j] = 0.1f * value;
            }
        }

        return output;
    }

    private static float[][] dotProduct(final float[][] input1, final float[][] input2) {
        float[][] output = new float[input1.length][input2[0].length];

        for (int i = 0; i < output.length; i++) {
            for (int j = 0; j < output[0].length; j++) {
                float value = 0f;

                for (int k = 0; k < input1[0].length; k++) {
                    value += input1[i][k] * input2[k][j];
                }

                output[i][j] = value;
            }
        }

        return output;
    }

    private static float[][] add(final float[][] input1, final float[] input2) {
        float[][] output = new float[input1.length][input1[0].length];

        for (int i = 0; i < input1.length; i++) {
            for (int j = 0; j < input1[0].length; j++) {
                output[i][j] = input1[i][j] + input2[j];
            }
        }

        return output;
    }

    private float[][] forward(final float[][] inputs, final float[][] weights, final float[] biases) {
        return add(dotProduct(inputs, weights), biases);
    }

    @Test
    @Disabled
    public void TEST_4() {
        int input = NN_INPUTS;
        int neurons = NN_NEURONS;
        float[][] inputs = getConstantMatrix(NN_PREVIOUS_INPUTS, input, NN_BIAS);
        float[][] weights = getConstantMatrix(input, neurons, NN_BIAS);
        long startTime = System.currentTimeMillis();
        float[][] output = dotProduct(inputs, weights);

        System.out.printf("time taken: %s ms%n", System.currentTimeMillis() - startTime);
        System.out.println(Arrays.toString(output[0]));
        System.out.println(Arrays.toString(output[1]));
    }

    @Test
    @Disabled
    public void TEST_5() {
        int input = NN_INPUTS;
        int neurons = NN_NEURONS;
        float[][] inputs = getConstantMatrix(NN_PREVIOUS_INPUTS, input, NN_BIAS);
        float[][] weights = getConstantMatrix(input, neurons, NN_BIAS);
        float[][] output = new float[inputs.length][weights[0].length];

        Kernel kernelDotProduct = new Kernel() {
            @Override
            public void run() {
                int i = getGlobalId();

                for (int j = 0; j < output[0].length; j++) {
                    float value = 0f;

                    for (int k = 0; k < inputs[0].length; k++) {
                        value += inputs[i][k] * weights[k][j];
                    }

                    output[i][j] = value;
                }
            }
        };

        long startTime = System.currentTimeMillis();

        kernelDotProduct.execute(Range.create(output.length));
        kernelDotProduct.dispose();
        System.out.printf("time taken: %s ms%n", System.currentTimeMillis() - startTime);
        System.out.println(Arrays.toString(output[0]));
        System.out.println(Arrays.toString(output[1]));
    }

    @Test
    @Disabled
    public void TEST_6() {
        int input = NN_INPUTS;
        int neurons = NN_NEURONS;
        float[][] inputs = getConstantMatrix(NN_PREVIOUS_INPUTS, input, NN_BIAS);
        float[][] weights = getConstantMatrix(input, neurons, NN_BIAS);
        float[][] output = new float[inputs.length][weights[0].length];

        Kernel kernelDotProduct = new Kernel() {
            @Override
            public void run() {
                int i = getGlobalId(0);
                int j = getGlobalId(1);

                float value = 0f;

                for (int k = 0; k < inputs[0].length; k++) {
                    value += inputs[i][k] * weights[k][j];
                }

                output[i][j] = value;
            }
        };

        long startTime = System.currentTimeMillis();

        kernelDotProduct.execute(Range.create2D(output.length, output[0].length));
        kernelDotProduct.dispose();
        System.out.printf("time taken: %s ms%n", System.currentTimeMillis() - startTime);
        System.out.println(Arrays.toString(output[0]));
        System.out.println(Arrays.toString(output[1]));
    }

    @Test
    public void TEST_7() {
        int input = NN_INPUTS;
        int neurons = NN_NEURONS;
        float[][] inputs = getConstantMatrix(NN_PREVIOUS_INPUTS, input, NN_BIAS);
        float[][] weights = getConstantMatrix(input, neurons, NN_BIAS);
        float[] biases = new float[neurons];
        long startTime = System.currentTimeMillis();
        float[][] output = forward(inputs, weights, biases);

        System.out.printf("time taken: %s ms%n", System.currentTimeMillis() - startTime);
        System.out.println(Arrays.toString(output[0]));
        System.out.println(Arrays.toString(output[1]));
    }

    @Test
    public void TEST_8() {
        int input = NN_INPUTS;
        int neurons = NN_NEURONS;
        float[][] inputs = getConstantMatrix(NN_PREVIOUS_INPUTS, input, NN_BIAS);
        float[][] weights = getConstantMatrix(input, neurons, NN_BIAS);
        float[] biases = new float[neurons];
        float[][] output1 = new float[inputs.length][weights[0].length];
        float[][] output2 = new float[inputs.length][weights[0].length];

        Kernel kernelDotProduct = new Kernel() {
            @Override
            public void run() {
                int i = getGlobalId();

                for (int j = 0; j < output1[0].length; j++) {
                    float value = 0f;

                    for (int k = 0; k < inputs[0].length; k++) {
                        value += inputs[i][k] * weights[k][j];
                    }

                    output1[i][j] = value;
                }
            }
        };

        Kernel kernelAdd = new Kernel() {
            @Override
            public void run() {
                int i = getGlobalId();

                for (int j = 0; j < output2[0].length; j++) {
                    output2[i][j] = output1[i][j] + biases[j];
                }
            }
        };

        long startTime = System.currentTimeMillis();

        kernelDotProduct.execute(Range.create(output1.length));
        kernelDotProduct.dispose();
        kernelAdd.execute(Range.create(output2.length));
        kernelAdd.dispose();
        System.out.printf("time taken: %s ms%n", System.currentTimeMillis() - startTime);
        System.out.println(Arrays.toString(output2[0]));
        System.out.println(Arrays.toString(output2[1]));
    }

    @Test
    public void TEST_9() {
        int input = NN_INPUTS;
        int neurons = NN_NEURONS;
        float[][] inputs = getConstantMatrix(NN_PREVIOUS_INPUTS, input, NN_BIAS);
        float[][] weights = getConstantMatrix(input, neurons, NN_BIAS);
        float[] biases = new float[neurons];
        float[][] output1 = new float[inputs.length][weights[0].length];
        float[][] output2 = new float[inputs.length][weights[0].length];

        Kernel kernelDotProduct = new Kernel() {
            @Override
            public void run() {
                int i = getGlobalId(0);
                int j = getGlobalId(1);
                float value = 0f;

                for (int k = 0; k < inputs[0].length; k++) {
                    value += inputs[i][k] * weights[k][j];
                }

                output1[i][j] = value;
            }
        };

        Kernel kernelAdd = new Kernel() {
            @Override
            public void run() {
                int i = getGlobalId(0);
                int j = getGlobalId(1);

                output2[i][j] = output1[i][j] + biases[j];
            }
        };

        long startTime = System.currentTimeMillis();

        kernelDotProduct.execute(Range.create2D(output1.length, output1[0].length));
        kernelDotProduct.dispose();
        kernelAdd.execute(Range.create2D(output2.length, output2[0].length));
        kernelAdd.dispose();
        System.out.printf("time taken: %s ms%n", System.currentTimeMillis() - startTime);
        System.out.println(Arrays.toString(output2[0]));
        System.out.println(Arrays.toString(output2[1]));
    }
}
