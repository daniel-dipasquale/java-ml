package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.common.SequentialIdFactoryLong;
import com.dipasquale.common.RandomSupportFloat;
import org.junit.Assert;
import org.junit.Test;

import java.util.UUID;

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
                        .populationSize(150)
                        .genomeIdFactory(() -> UUID.randomUUID().toString())
                        .genomeFactory(SettingsGenomeFactory.createDefault(2, 1, new float[]{1f}))
                        .speciesIdFactory(() -> UUID.randomUUID().toString())
                        .fitnessDeterminerFactory(FitnessDeterminerFactory.createLastValueFactory())
                        .environment(g -> {
                            float temporary = 0f;

                            for (int i = 0; i < inputs.length; i++) {
                                float[] output = g.activate(inputs[i]);

                                temporary += (float) Math.pow(outputExpected[i] - output[0], 2D);
                            }

                            return 4f - temporary;
                        })
                        .build())
                .nodes(SettingsNodeGeneSupport.builder()
                        .inputIdFactory(new SequentialIdFactoryLong())
                        .outputIdFactory(new SequentialIdFactoryLong())
                        .biasIdFactory(new SequentialIdFactoryLong())
                        .hiddenIdFactory(new SequentialIdFactoryLong())
                        .hiddenBias(SettingsFloatNumber.literal(0f))
                        .hiddenActivationFunction(SettingsActivationFunction.Sigmoid)
                        .build())
                .connections(SettingsConnectionGeneSupport.builder()
                        .recurrentConnectionsAllowed(true)
                        .multipleRecurrentCyclesAllowed(false)
                        .innovationIdFactory(new SequentialIdFactoryLong())
                        .weightFactory(SettingsFloatNumber.randomGaussian(-1f, 1f))
                        .weightPerturber(SettingsFloatNumber.literal(2.5f))
                        .build())
                .neuralNetwork(SettingsNeuralNetworkSupport.builder()
                        .type(SettingsNeuralNetworkType.Default)
                        .build())
                .random(SettingsRandom.builder()
                        .randomSupport(RandomSupportFloat.create())
                        .build())
                .mutation(SettingsMutation.builder()
                        .addNodeMutationsRate(SettingsFloatNumber.literal(0.03f))
                        .addConnectionMutationsRate(SettingsFloatNumber.literal(0.05f))
                        .perturbConnectionWeightRate(SettingsFloatNumber.literal(0.5f))
                        .replaceConnectionWeightRate(SettingsFloatNumber.literal(0.5f))
                        .disableConnectionExpressedRate(SettingsFloatNumber.literal(0.05f))
                        .build())
                .crossOver(SettingsCrossOver.builder()
                        .rate(SettingsFloatNumber.literal(0.2f))
                        .overrideExpressedRate(SettingsFloatNumber.literal(0.5f))
                        .useRandomParentWeightRate(SettingsFloatNumber.literal(0.6f))
                        .build())
                .speciation(SettingsSpeciation.builder()
                        .maximumSpecies(SettingsIntegerNumber.literal(40))
                        .maximumGenomes(SettingsIntegerNumber.literal(20))
                        .weightDifferenceCoefficient(SettingsFloatNumber.literal(0.4f))
                        .disjointCoefficient(SettingsFloatNumber.literal(1f))
                        .excessCoefficient(SettingsFloatNumber.literal(1f))
                        .compatibilityThreshold(SettingsFloatNumber.literal(3f))
                        .compatibilityThresholdModifier(SettingsFloatNumber.literal(1.28f))
                        .eugenicsThreshold(SettingsFloatNumber.literal(0.2f))
                        .elitistThreshold(SettingsFloatNumber.literal(0.01f))
                        .elitistThresholdMinimum(SettingsIntegerNumber.literal(1))
                        .interSpeciesMatingRate(SettingsFloatNumber.literal(0.001f))
                        .stagnationDropOffAge(SettingsIntegerNumber.literal(15))
                        .build())
                .build());

        NeatCollectiveClient neatClient = neat.getMostFit(); // TODO: population size is still flactuating, champion is poorly selected
        boolean success = false;

        for (int i1 = 0, c = 100; i1 < c && !success; i1++) {
            success = true;

            for (int i2 = 0; i2 < inputs.length && success; i2++) {
                float[] output = neatClient.activate(inputs[i2]);

                success = Float.compare(outputExpected[i2], (float) Math.round(output[0])) == 0;
            }

            if (!success) {
                neat.testFitness();
                neat.evolve();
            }
        }

        Assert.assertTrue(success);
    }
}
