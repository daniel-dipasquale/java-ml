package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.common.SequentialIdFactoryLong;
import com.dipasquale.common.RandomSupportFloat;
import com.dipasquale.simulation.cart.pole.CartPoleEnvironment;
import com.google.common.collect.ImmutableList;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class NeatEvaluatorTest {
    private static final int NUMBER_OF_THREADS = Math.max(1, Runtime.getRuntime().availableProcessors() - 1);
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    @AfterClass
    public static void afterClass() {
        EXECUTOR_SERVICE.shutdown();
    }

    private static SettingsEvaluator createXorEvaluatorSettings(final float[][] inputs, final float[] expectedOutputs, final boolean shouldUseParallelism) {
        return SettingsEvaluator.builder()
                .general(SettingsGeneralEvaluatorSupport.builder()
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
                        .environment(genome -> {
                            float temporary = 0f;

                            for (int i = 0; i < inputs.length; i++) {
                                float[] output = genome.activate(inputs[i]);

                                temporary += (float) Math.pow(expectedOutputs[i] - output[0], 2D);
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
                        .nextIndexRandomSupport(RandomSupportFloat.createConcurrent())
                        .isLessThanRandomSupport(RandomSupportFloat.createConcurrent())
                        .build())
                .parallelism(SettingsParallelism.builder()
                        .executorService(shouldUseParallelism ? EXECUTOR_SERVICE : null)
                        .numberOfThreads(shouldUseParallelism ? NUMBER_OF_THREADS : 1)
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
                .build();
    }

    private void assertTheXorProblem(final boolean shouldUseParallelism) {
        float[][] inputs = new float[][]{
                new float[]{1f, 1f}, // 0f
                new float[]{1f, 0f}, // 1f
                new float[]{0f, 1f}, // 1f
                new float[]{0f, 0f}  // 0f
        };

        float[] expectedOutputs = new float[]{0f, 1f, 1f, 0f};
        NeatEvaluator neat = Neat.createEvaluator(createXorEvaluatorSettings(inputs, expectedOutputs, shouldUseParallelism));
        boolean success = false;

        try {
            for (int i1 = 0, c = 500; i1 < c && !success; i1++) {
                success = true;

                for (int i2 = 0; i2 < inputs.length && success; i2++) {
                    float[] output = neat.activate(inputs[i2]);

                    success = Float.compare(expectedOutputs[i2], (float) Math.round(output[0])) == 0;
                }

                if (!success) {
                    neat.evaluateFitness();
                    neat.evolve();
                }
            }
        } finally {
            neat.shutdown();
        }

        System.out.printf("generation: %d%n", neat.getGeneration());
        System.out.printf("species: %d%n", neat.getSpeciesCount());
        System.out.printf("fitness: %f%n", neat.getMaximumFitness());
        Assert.assertTrue(success);
    }

    @Test
    public void GIVEN_a_single_threaded_neat_evaluator_WHEN_finding_the_solution_for_the_xor_problem_THEN_find_the_solution() {
        assertTheXorProblem(false);
    }

    @Test
    public void GIVEN_a_multi_threaded_neat_evaluator_WHEN_finding_the_solution_for_the_xor_problem_THEN_find_the_solution() {
        assertTheXorProblem(true);
    }

    private static float[] convertToFloat(final double[] input) {
        float[] output = new float[input.length];

        for (int i = 0; i < input.length; i++) {
            output[i] = (float) input[i];
        }

        return output;
    }

    private static SettingsEvaluator createCartSinglePoleEvaluatorSettings(final double timeSpentGoal, final boolean shouldUseParallelism) {
        return SettingsEvaluator.builder()
                .general(SettingsGeneralEvaluatorSupport.builder()
                        .populationSize(150)
                        .genomeIdFactory(() -> UUID.randomUUID().toString())
                        .genomeFactory(SettingsGenomeFactory.builder()
                                .inputs(SettingsIntegerNumber.literal(4))
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
                        .environment(genome -> {
                            float minimumTimeSpent = Float.MAX_VALUE;

                            for (int i = 0, attempts = 5; i < attempts; i++) {
                                CartPoleEnvironment environment = CartPoleEnvironment.builder()
                                        .build();

                                while (!environment.isLimitHit() && Double.compare(environment.getTimeSpent(), timeSpentGoal) < 0) {
                                    float[] input = convertToFloat(environment.getState());
                                    float[] output = genome.activate(input);

                                    environment.stepInDiscrete(output[0]);
                                }

                                minimumTimeSpent = Math.min(minimumTimeSpent, (float) environment.getTimeSpent());
                            }

                            if (Float.compare(minimumTimeSpent, Float.MAX_VALUE) == 0) {
                                return 0f;
                            }

                            return minimumTimeSpent;
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
                        .weightPerturber(SettingsFloatNumber.literal(1.8f))
                        .build())
                .neuralNetwork(SettingsNeuralNetworkSupport.builder()
                        .type(SettingsNeuralNetworkType.FEED_FORWARD)
                        .build())
                .random(SettingsRandom.builder()
                        .nextIndexRandomSupport(RandomSupportFloat.createConcurrent())
                        .isLessThanRandomSupport(RandomSupportFloat.createConcurrent())
                        .build())
                .parallelism(SettingsParallelism.builder()
                        .executorService(shouldUseParallelism ? EXECUTOR_SERVICE : null)
                        .numberOfThreads(shouldUseParallelism ? NUMBER_OF_THREADS : 1)
                        .build())
                .mutation(SettingsMutation.builder()
                        .addNodeMutationRate(SettingsFloatNumber.literal(0.01f))
                        .addConnectionMutationRate(SettingsFloatNumber.literal(0.05f))
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
                        .compatibilityThreshold(SettingsFloatNumber.literal(4f))
                        .compatibilityThresholdModifier(SettingsFloatNumber.literal(1.2f))
                        .eugenicsThreshold(SettingsFloatNumber.literal(0.4f))
                        .elitistThreshold(SettingsFloatNumber.literal(0.01f))
                        .elitistThresholdMinimum(SettingsIntegerNumber.literal(2))
                        .interSpeciesMatingRate(SettingsFloatNumber.literal(0.001f))
                        .stagnationDropOffAge(SettingsIntegerNumber.literal(15))
                        .build())
                .build();
    }

    private void assertTheCartSinglePoleProblem(final boolean shouldUseParallelism) {
        double timeSpentGoal = 60D;
        NeatEvaluator neat = Neat.createEvaluator(createCartSinglePoleEvaluatorSettings(timeSpentGoal, shouldUseParallelism));
        boolean success = false;

        try {
            for (int i1 = 0, c = 750; i1 < c && !success; i1++) {
                success = true;

                for (int i2 = 0, attempts = 10; i2 < attempts && success; i2++) {
                    CartPoleEnvironment environment = CartPoleEnvironment.builder()
                            .build();

                    while (!environment.isLimitHit() && Double.compare(environment.getTimeSpent(), timeSpentGoal) < 0) {
                        float[] input = convertToFloat(environment.getState());
                        float[] output = neat.activate(input);

                        environment.stepInDiscrete(output[0]);
                    }

                    success = Double.compare(environment.getTimeSpent(), timeSpentGoal) >= 0;
                }

                if (!success) {
                    neat.evaluateFitness();
                    neat.evolve();
                }
            }
        } finally {
            neat.shutdown();
        }

        System.out.printf("generation: %d%n", neat.getGeneration());
        System.out.printf("species: %d%n", neat.getSpeciesCount());
        System.out.printf("fitness: %f%n", neat.getMaximumFitness());
        Assert.assertTrue(success);
    }

    @Test
    public void GIVEN_a_single_threaded_neat_evaluator_WHEN_finding_the_solution_the_cart_single_pole_problem_in_a_discrete_environment_THEN_find_the_solution() {
        assertTheCartSinglePoleProblem(false);
    }

    @Test
    public void GIVEN_a_multi_threaded_neat_evaluator_WHEN_finding_the_solution_the_cart_single_pole_problem_in_a_discrete_environment_THEN_find_the_solution() {
        assertTheCartSinglePoleProblem(true);
    }
}
