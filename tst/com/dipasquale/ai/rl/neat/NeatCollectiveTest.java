package com.dipasquale.ai.rl.neat;

import org.junit.Test;

public final class NeatCollectiveTest {
    @Test
    public void TEST_1() {
        float[][] inputs = new float[][]{
                new float[]{1f, 1f}, // 0f
                new float[]{1f, 0f}, // 1f
                new float[]{0f, 1f}, // 1f
                new float[]{0f, 0f}  // 0f
        };

        float[] outputExpected = new float[]{0f, 1f, 1f, 0f};

        NeatCollective neat = Neat.createCollective(SettingsCollective.builder()
                .general(SettingsGeneralSupport.builder()
                        .genomeFactory(SettingsGenomeFactory.createDefault(2, 1, new float[]{1f}))
                        .environment(g -> {
                            float temporary = 0f;

                            for (int i = 0; i < inputs.length; i++) {
                                float[] output = g.activate(inputs[i]);

                                temporary += (float) Math.pow(outputExpected[i] - output[0], 2D);
                            }

                            return 4f - temporary;
                        })
                        .build())
                .connections(SettingsConnectionGeneSupport.builder()
                        .recurrentConnectionsAllowed(false)
                        .weight(SettingsFloatNumber.randomGaussian(-1f, 1f))
                        .build())
                .build());

        for (int i = 0, c = 30; i < c; i++) {
            neat.testFitness();
            neat.evolve();
        }
    }
}
