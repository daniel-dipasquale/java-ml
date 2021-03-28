package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.common.FitnessDeterminerFactory;
import com.dipasquale.ai.common.SequentialIdFactoryLong;
import com.dipasquale.common.RandomSupportFloat;
import com.google.common.collect.ImmutableList;
import org.junit.Assert;
import org.junit.Test;

import java.util.UUID;

public final class NeatEvaluatorTest {
    @Test
    public void GIVEN_a_neat_evaluator_WHEN_finding_the_solution_for_xor_THEN_find_the_solution() {
        float[][] inputs = new float[][]{
                new float[]{1f, 1f}, // 0f
                new float[]{1f, 0f}, // 1f
                new float[]{0f, 1f}, // 1f
                new float[]{0f, 0f}  // 0f
        };

        float[] outputExpected = new float[]{0f, 1f, 1f, 0f};

        NeatEvaluator neat = Neat.createEvaluator(SettingsEvaluator.builder()
                .general(SettingsGeneralSupport.builder()
                        .populationSize(150)
                        .genomeIdFactory(() -> UUID.randomUUID().toString())
                        .genomeFactory(SettingsGenomeFactory.builder()
                                .inputs(SettingsIntegerNumber.literal(2))
                                .inputBias(SettingsFloatNumber.literal(0f))
                                .inputActivationFunction(SettingsEnum.literal(SettingsActivationFunction.IDENTITY))
                                .outputs(SettingsIntegerNumber.literal(1))
                                .outputBias(SettingsFloatNumber.random(-1f, 1f))
                                .outputActivationFunction(SettingsEnum.literal(SettingsOutputActivationFunction.COPY_FROM_HIDDEN))
                                .biases(ImmutableList.of())
                                .initialConnectionType(SettingsInitialConnectionType.ALL_INPUTS_AND_BIASES_TO_ALL_OUTPUTS)
                                .initialWeightType(SettingsInitialWeightType.RANDOM)
                                .build())
                        .speciesIdFactory(() -> UUID.randomUUID().toString())
                        .fitnessDeterminerFactory(FitnessDeterminerFactory.createLastValueFactory())
                        .environment(genome -> {
                            float temporary = 0f;

                            for (int i = 0; i < inputs.length; i++) {
                                float[] output = genome.activate(inputs[i]);

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
                        .hiddenBias(SettingsFloatNumber.random(-1f, 1f))
                        .hiddenActivationFunction(SettingsEnum.literal(SettingsActivationFunction.SIGMOID))
                        .build())
                .connections(SettingsConnectionGeneSupport.builder()
                        .innovationIdFactory(new SequentialIdFactoryLong())
                        .weightFactory(SettingsFloatNumber.random(-1f, 1f))
                        .weightPerturber(SettingsFloatNumber.literal(2.5f))
                        .build())
                .neuralNetwork(SettingsNeuralNetworkSupport.builder()
                        .type(SettingsNeuralNetworkType.FEED_FORWARD)
                        .build())
                .random(SettingsRandom.builder()
                        .nextIndexRandomSupport(RandomSupportFloat.create())
                        .isLessThanRandomSupport(RandomSupportFloat.create())
                        .build())
                .mutation(SettingsMutation.builder()
                        .addNodeMutationRate(SettingsFloatNumber.literal(0.05f))
                        .addConnectionMutationRate(SettingsFloatNumber.literal(0.1f))
                        .perturbConnectionsWeightRate(SettingsFloatNumber.literal(0.75f))
                        .replaceConnectionsWeightRate(SettingsFloatNumber.literal(0.5f))
                        .disableConnectionExpressedRate(SettingsFloatNumber.literal(0.05f))
                        .build())
                .crossOver(SettingsCrossOver.builder()
                        .mateOnlyFloatNumber(SettingsFloatNumber.literal(0.2f))
                        .mutateOnlyFloatNumber(SettingsFloatNumber.literal(0.25f))
                        .overrideConnectionExpressedRate(SettingsFloatNumber.literal(0.5f))
                        .useRandomParentConnectionWeightRate(SettingsFloatNumber.literal(0.6f))
                        .build())
                .speciation(SettingsSpeciation.builder()
                        .maximumSpecies(SettingsIntegerNumber.literal(20))
                        .maximumGenomes(SettingsIntegerNumber.literal(20))
                        .weightDifferenceCoefficient(SettingsFloatNumber.literal(0.5f))
                        .disjointCoefficient(SettingsFloatNumber.literal(1f))
                        .excessCoefficient(SettingsFloatNumber.literal(1f))
                        .compatibilityThreshold(SettingsFloatNumber.literal(3f))
                        .compatibilityThresholdModifier(SettingsFloatNumber.literal(1.2f))
                        .eugenicsThreshold(SettingsFloatNumber.literal(0.2f))
                        .elitistThreshold(SettingsFloatNumber.literal(0.01f))
                        .elitistThresholdMinimum(SettingsIntegerNumber.literal(2))
                        .interSpeciesMatingRate(SettingsFloatNumber.literal(0.001f))
                        .stagnationDropOffAge(SettingsIntegerNumber.literal(15))
                        .build())
                .build());

        boolean success = false;

        for (int i1 = 0, c = 5_000; i1 < c && !success; i1++) {
            success = true;

            for (int i2 = 0; i2 < inputs.length && success; i2++) {
                float[] output = neat.activate(inputs[i2]);

                success = Float.compare(outputExpected[i2], (float) Math.round(output[0])) == 0;
            }

            if (!success) {
                neat.testFitness();
                neat.evolve();
            }
        }

        System.out.printf("generation: %d%n", neat.getGeneration());
        System.out.printf("species: %d%n", neat.getSpeciesCount());
        System.out.printf("fitness: %f%n", neat.getMaximumFitness());
        Assert.assertTrue(success);
    }
}
