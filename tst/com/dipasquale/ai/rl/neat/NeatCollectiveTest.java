package com.dipasquale.ai.rl.neat;

import org.junit.Assert;
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
                        .build())
                .build());

        NeatCollectiveClient neatClient = neat.getMostFit();
        boolean success = false;

        for (int i1 = 0, c = 90; i1 < c && !success; i1++) {
            success = true;

            for (int i2 = 0; i2 < inputs.length; i2++) {
                float[] output = neatClient.activate(inputs[i2]);

                success &= Float.compare(outputExpected[i2], (float) Math.round(output[0])) == 0;
            }

            neat.testFitness();
            neat.evolve();
        }

        Assert.assertTrue(success);
    }
}
