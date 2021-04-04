package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.common.FitnessDeterminerFactory;
import com.dipasquale.ai.rl.neat.genotype.Genome;
import com.dipasquale.common.DateTimeSupport;
import com.dipasquale.common.test.JvmWarmup;
import com.dipasquale.simulation.cart.pole.CartPoleEnvironment;
import com.dipasquale.threading.event.loop.EventLoop;
import com.dipasquale.threading.event.loop.EventLoopIterator;
import com.dipasquale.threading.event.loop.EventLoopIteratorSettings;
import com.google.common.collect.ImmutableList;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class NeatTest {
    private static final int NUMBER_OF_THREADS = Math.max(1, Runtime.getRuntime().availableProcessors() - 1);
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    private static final List<Throwable> EXCEPTIONS = Collections.synchronizedList(new ArrayList<>());

    private static final EventLoopIterator EVENT_LOOP_STREAM = EventLoop.createForIterators(EventLoopIteratorSettings.builder()
            .executorService(EXECUTOR_SERVICE)
            .numberOfThreads(NUMBER_OF_THREADS)
            .exceptionLogger(EXCEPTIONS::add)
            .dateTimeSupport(DateTimeSupport.createMilliseconds())
            .build());

    @BeforeClass
    public static void beforeClass() {
        JvmWarmup.start(250_000);
    }

    @AfterClass
    public static void afterClass() {
        EVENT_LOOP_STREAM.shutdown();
        EXECUTOR_SERVICE.shutdown();
    }

    private static NeatSetup createXorEvaluatorTest(final boolean shouldUseParallelism) {
        int populationSize = 150;

        float[][] inputs = new float[][]{
                new float[]{1f, 1f}, // 0f
                new float[]{1f, 0f}, // 1f
                new float[]{0f, 1f}, // 1f
                new float[]{0f, 0f}  // 0f
        };

        float[] expectedOutputs = new float[]{0f, 1f, 1f, 0f};

        NeatEnvironmentContainer environmentContainer = NeatEnvironmentContainer.builder()
                .shouldUseParallelism(shouldUseParallelism)
                .environment(genome -> {
                    float temporary = 0f;

                    for (int i = 0; i < inputs.length; i++) {
                        float[] output = genome.activate(inputs[i]);

                        temporary += (float) Math.pow(expectedOutputs[i] - output[0], 2D);
                    }

                    return 4f - temporary;
                })
                .build();

        NeatEvaluatorTrainingPolicy trainingPolicy = na -> {
            boolean success = true;

            for (int i = 0; success && i < inputs.length; i++) {
                float[] output = na.activate(inputs[i]);

                success = Float.compare(expectedOutputs[i], (float) Math.round(output[0])) == 0;
            }

            if (success) {
                return NeatEvaluatorTrainingResult.WORKING_SOLUTION_FOUND;
            }

            return NeatEvaluatorTrainingResult.EVALUATE_FITNESS_AND_EVOLVE;
        };

        return NeatSetup.builder()
                .populationSize(populationSize)
                .genomeIds(environmentContainer.genomeIds)
                .settings(SettingsEvaluator.builder()
                        .general(SettingsGeneralEvaluatorSupport.builder()
                                .populationSize(populationSize)
                                .genomeFactory(SettingsGenomeFactory.builder()
                                        .inputs(SettingsIntegerNumber.literal(2))
                                        .inputBias(SettingsFloatNumber.literal(0f))
                                        .inputActivationFunction(SettingsEnum.literal(SettingsActivationFunction.IDENTITY))
                                        .outputs(SettingsIntegerNumber.literal(1))
                                        .outputBias(SettingsFloatNumber.random(SettingsRandomType.UNIFORM, -1f, 1f))
                                        .outputActivationFunction(SettingsEnum.literal(SettingsOutputActivationFunction.COPY_FROM_HIDDEN))
                                        .biases(ImmutableList.of())
                                        .initialConnectionType(SettingsInitialConnectionType.ALL_INPUTS_AND_BIASES_TO_ALL_OUTPUTS)
                                        .initialWeightType(SettingsInitialWeightType.RANDOM)
                                        .build())
                                .fitnessDeterminerFactory(FitnessDeterminerFactory.createLastValueFactory())
                                .environment(environmentContainer)
                                .build())
                        .nodes(SettingsNodeGeneSupport.builder()
                                .hiddenBias(SettingsFloatNumber.random(SettingsRandomType.UNIFORM, -1f, 1f))
                                .hiddenActivationFunction(SettingsEnum.literal(SettingsActivationFunction.SIGMOID))
                                .build())
                        .connections(SettingsConnectionGeneSupport.builder()
                                .weightFactory(SettingsFloatNumber.random(SettingsRandomType.UNIFORM, -1f, 1f))
                                .weightPerturber(SettingsFloatNumber.literal(2.5f))
                                .build())
                        .neuralNetwork(SettingsNeuralNetworkSupport.builder()
                                .type(SettingsNeuralNetworkType.FEED_FORWARD)
                                .build())
                        .random(SettingsRandom.builder()
                                .nextIndex(SettingsRandomType.UNIFORM)
                                .isLessThan(SettingsRandomType.UNIFORM)
                                .build())
                        .parallelism(SettingsParallelism.builder()
                                .eventLoopIterator(shouldUseParallelism ? EVENT_LOOP_STREAM : null)
                                .build())
                        .mutation(SettingsMutation.builder()
                                .addNodeMutationRate(SettingsFloatNumber.literal(0.05f))
                                .addConnectionMutationRate(SettingsFloatNumber.literal(0.1f))
                                .perturbConnectionsWeightRate(SettingsFloatNumber.literal(0.75f))
                                .replaceConnectionsWeightRate(SettingsFloatNumber.literal(0.5f))
                                .disableConnectionExpressedRate(SettingsFloatNumber.literal(0.05f))
                                .build())
                        .crossOver(SettingsCrossOver.builder()
                                .mateOnlyRate(SettingsFloatNumber.literal(0.2f))
                                .mutateOnlyRate(SettingsFloatNumber.literal(0.25f))
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
                                .stagnationDropOffAge(SettingsIntegerNumber.literal(15))
                                .interSpeciesMatingRate(SettingsFloatNumber.literal(0.001f))
                                .build())
                        .build())
                .trainingPolicy(NeatEvaluatorTrainingPolicies.builder()
                        .add(NeatEvaluatorTrainingPolicy.maximumGenerations(2_000, NeatEvaluatorTrainingResult.EVALUATE_FITNESS_AND_EVOLVE))
                        .add(trainingPolicy)
                        .build())
                .build();
    }

    private void assertTheXorProblem(final boolean shouldUseParallelism) {
        NeatSetup neatSetup = createXorEvaluatorTest(shouldUseParallelism);
        NeatEvaluatorTrainer neat = Neat.createEvaluatorTrainer(neatSetup.settings);
        boolean success = neat.train(neatSetup.trainingPolicy);

        System.out.printf("=========================================%n");
        System.out.printf("XOR (%s):%n", shouldUseParallelism ? "parallel" : "single");
        System.out.printf("=========================================%n");
        System.out.printf("generation: %d%n", neat.getGeneration());
        System.out.printf("species: %d%n", neat.getSpeciesCount());
        System.out.printf("fitness: %f%n", neat.getMaximumFitness());
        Assert.assertTrue(success);
        Assert.assertEquals(neatSetup.populationSize, neatSetup.genomeIds.size());
    }

    @Test
    public void GIVEN_a_single_threaded_neat_evaluator_WHEN_finding_the_solution_to_the_first_problem_which_is_xor_THEN_find_the_solution() {
        assertTheXorProblem(false);
    }

    @Test
    public void GIVEN_a_multi_threaded_neat_evaluator_WHEN_finding_the_solution_to_the_first_problem_which_is_xor_THEN_find_the_solution() {
        assertTheXorProblem(true);
    }

    private static float[] convertToFloat(final double[] input) {
        float[] output = new float[input.length];

        for (int i = 0; i < input.length; i++) {
            output[i] = (float) input[i];
        }

        return output;
    }

    private static NeatSetup createSinglePoleBalancingTest(final double timeSpentGoal, final boolean shouldUseParallelism) {
        int populationSize = 150;

        NeatEnvironmentContainer environmentContainer = NeatEnvironmentContainer.builder()
                .shouldUseParallelism(shouldUseParallelism)
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
                .build();

        NeatEvaluatorTrainingPolicy trainingPolicy = na -> {
            boolean success = true;

            for (int i = 0, attempts = 10; success && i < attempts; i++) {
                CartPoleEnvironment environment = CartPoleEnvironment.builder()
                        .build();

                while (!environment.isLimitHit() && Double.compare(environment.getTimeSpent(), timeSpentGoal) < 0) {
                    float[] input = convertToFloat(environment.getState());
                    float[] output = na.activate(input);

                    environment.stepInDiscrete(output[0]);
                }

                success = Double.compare(environment.getTimeSpent(), timeSpentGoal) >= 0;
            }

            if (success) {
                return NeatEvaluatorTrainingResult.WORKING_SOLUTION_FOUND;
            }

            return NeatEvaluatorTrainingResult.EVALUATE_FITNESS_AND_EVOLVE;
        };

        return NeatSetup.builder()
                .populationSize(populationSize)
                .genomeIds(environmentContainer.genomeIds)
                .settings(SettingsEvaluator.builder()
                        .general(SettingsGeneralEvaluatorSupport.builder()
                                .populationSize(populationSize)
                                .genomeFactory(SettingsGenomeFactory.builder()
                                        .inputs(SettingsIntegerNumber.literal(4))
                                        .inputBias(SettingsFloatNumber.literal(0f))
                                        .inputActivationFunction(SettingsEnum.literal(SettingsActivationFunction.IDENTITY))
                                        .outputs(SettingsIntegerNumber.literal(1))
                                        .outputBias(SettingsFloatNumber.random(SettingsRandomType.UNIFORM, -1f, 1f))
                                        .outputActivationFunction(SettingsEnum.literal(SettingsOutputActivationFunction.COPY_FROM_HIDDEN))
                                        .biases(ImmutableList.of())
                                        .initialConnectionType(SettingsInitialConnectionType.ALL_INPUTS_AND_BIASES_TO_ALL_OUTPUTS)
                                        .initialWeightType(SettingsInitialWeightType.RANDOM)
                                        .build())
                                .fitnessDeterminerFactory(FitnessDeterminerFactory.createLastValueFactory())
                                .environment(environmentContainer)
                                .build())
                        .nodes(SettingsNodeGeneSupport.builder()
                                .hiddenBias(SettingsFloatNumber.random(SettingsRandomType.UNIFORM, -1f, 1f))
                                .hiddenActivationFunction(SettingsEnum.literal(SettingsActivationFunction.SIGMOID))
                                .build())
                        .connections(SettingsConnectionGeneSupport.builder()
                                .weightFactory(SettingsFloatNumber.random(SettingsRandomType.UNIFORM, -1f, 1f))
                                .weightPerturber(SettingsFloatNumber.literal(1.8f))
                                .build())
                        .neuralNetwork(SettingsNeuralNetworkSupport.builder()
                                .type(SettingsNeuralNetworkType.FEED_FORWARD)
                                .build())
                        .random(SettingsRandom.builder()
                                .nextIndex(SettingsRandomType.UNIFORM)
                                .isLessThan(SettingsRandomType.UNIFORM)
                                .build())
                        .parallelism(SettingsParallelism.builder()
                                .eventLoopIterator(shouldUseParallelism ? EVENT_LOOP_STREAM : null)
                                .build())
                        .mutation(SettingsMutation.builder()
                                .addNodeMutationRate(SettingsFloatNumber.literal(0.01f))
                                .addConnectionMutationRate(SettingsFloatNumber.literal(0.05f))
                                .perturbConnectionsWeightRate(SettingsFloatNumber.literal(0.75f))
                                .replaceConnectionsWeightRate(SettingsFloatNumber.literal(0.5f))
                                .disableConnectionExpressedRate(SettingsFloatNumber.literal(0.05f))
                                .build())
                        .crossOver(SettingsCrossOver.builder()
                                .mateOnlyRate(SettingsFloatNumber.literal(0.2f))
                                .mutateOnlyRate(SettingsFloatNumber.literal(0.25f))
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
                                .stagnationDropOffAge(SettingsIntegerNumber.literal(15))
                                .interSpeciesMatingRate(SettingsFloatNumber.literal(0.001f))
                                .build())
                        .build())
                .trainingPolicy(NeatEvaluatorTrainingPolicies.builder()
                        .add(NeatEvaluatorTrainingPolicy.maximumGenerations(2_000, NeatEvaluatorTrainingResult.EVALUATE_FITNESS_AND_EVOLVE))
                        .add(trainingPolicy)
                        .build())
                .build();
    }

    private void assertTheSinglePoleBalancingProblem(final boolean shouldUseParallelism) {
        double timeSpentGoal = 60D;
        NeatSetup neatSetup = createSinglePoleBalancingTest(timeSpentGoal, shouldUseParallelism);
        NeatEvaluatorTrainer neat = Neat.createEvaluatorTrainer(neatSetup.settings);
        boolean success = neat.train(neatSetup.trainingPolicy);

        System.out.printf("=========================================%n");
        System.out.printf("Single Pole Balancing (%s)%n", shouldUseParallelism ? "parallel" : "single");
        System.out.printf("=========================================%n");
        System.out.printf("generation: %d%n", neat.getGeneration());
        System.out.printf("species: %d%n", neat.getSpeciesCount());
        System.out.printf("fitness: %f%n", neat.getMaximumFitness());
        Assert.assertTrue(success);
        Assert.assertEquals(neatSetup.populationSize, neatSetup.genomeIds.size());
    }

    @Test
    public void GIVEN_a_single_threaded_neat_evaluator_WHEN_finding_the_solution_to_the_second_problem_which_is_the_single_pole_balancing_problem_in_a_discrete_environment_THEN_find_the_solution() {
        assertTheSinglePoleBalancingProblem(false);
    }

    @Test
    public void GIVEN_a_multi_threaded_neat_evaluator_WHEN_finding_the_solution_to_the_second_problem_which_is_the_single_pole_balancing_problem_in_a_discrete_environment_THEN_find_the_solution() {
        assertTheSinglePoleBalancingProblem(true);
    }

    @AllArgsConstructor(access = AccessLevel.PACKAGE)
    private static final class NeatEnvironmentContainer implements NeatEnvironment {
        private final Set<String> genomeIds;
        private final NeatEnvironment environment;

        @Builder(access = AccessLevel.PRIVATE)
        public static NeatEnvironmentContainer create(final boolean shouldUseParallelism, final NeatEnvironment environment) {
            Set<String> genomeIds = !shouldUseParallelism
                    ? new HashSet<>()
                    : Collections.newSetFromMap(new ConcurrentHashMap<>());

            return new NeatEnvironmentContainer(genomeIds, environment);
        }

        @Override
        public float test(final Genome genome) {
            genomeIds.add(genome.getId());

            return environment.test(genome);
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder(access = AccessLevel.PRIVATE)
    private static final class NeatSetup {
        private final int populationSize;
        private final Set<String> genomeIds;
        private final SettingsEvaluator settings;
        private final NeatEvaluatorTrainingPolicy trainingPolicy;
    }
}