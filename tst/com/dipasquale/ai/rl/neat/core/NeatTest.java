package com.dipasquale.ai.rl.neat.core;

import com.dipasquale.ai.common.fitness.FitnessDeterminerFactory;
import com.dipasquale.ai.common.function.activation.ActivationFunctionType;
import com.dipasquale.ai.common.function.activation.OutputActivationFunctionType;
import com.dipasquale.ai.rl.neat.common.RandomType;
import com.dipasquale.ai.rl.neat.genotype.Genome;
import com.dipasquale.ai.rl.neat.settings.ConnectionGeneSupport;
import com.dipasquale.ai.rl.neat.settings.CrossOverSupport;
import com.dipasquale.ai.rl.neat.settings.EnumValue;
import com.dipasquale.ai.rl.neat.settings.EvaluatorSettings;
import com.dipasquale.ai.rl.neat.settings.EvaluatorStateSettings;
import com.dipasquale.ai.rl.neat.settings.FloatNumber;
import com.dipasquale.ai.rl.neat.settings.GeneralEvaluatorSupport;
import com.dipasquale.ai.rl.neat.settings.GenesisGenomeTemplate;
import com.dipasquale.ai.rl.neat.settings.InitialConnectionType;
import com.dipasquale.ai.rl.neat.settings.InitialWeightType;
import com.dipasquale.ai.rl.neat.settings.IntegerNumber;
import com.dipasquale.ai.rl.neat.settings.MutationSupport;
import com.dipasquale.ai.rl.neat.settings.NeuralNetworkSupport;
import com.dipasquale.ai.rl.neat.settings.NeuralNetworkType;
import com.dipasquale.ai.rl.neat.settings.NodeGeneSupport;
import com.dipasquale.ai.rl.neat.settings.ParallelismSupport;
import com.dipasquale.ai.rl.neat.settings.RandomSupport;
import com.dipasquale.ai.rl.neat.settings.SpeciationSupport;
import com.dipasquale.common.test.JvmWarmup;
import com.dipasquale.common.time.MillisecondsDateTimeSupport;
import com.dipasquale.simulation.cart.pole.CartPoleEnvironment;
import com.dipasquale.threading.event.loop.IterableEventLoop;
import com.dipasquale.threading.event.loop.IterableEventLoopSettings;
import com.google.common.collect.ImmutableList;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serial;
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

    private static final IterableEventLoopSettings EVENT_LOOP_SETTINGS = IterableEventLoopSettings.builder()
            .executorService(EXECUTOR_SERVICE)
            .numberOfThreads(NUMBER_OF_THREADS)
            .errorLogger(EXCEPTIONS::add)
            .dateTimeSupport(new MillisecondsDateTimeSupport())
            .build();

    private static final IterableEventLoop EVENT_LOOP = new IterableEventLoop(EVENT_LOOP_SETTINGS);

    @BeforeAll
    public static void beforeAll() {
        JvmWarmup.start(250_000);
    }

    @AfterAll
    public static void afterAll() {
        EVENT_LOOP.shutdown();
        EXECUTOR_SERVICE.shutdown();
    }

    private static byte[] getBytes(final NeatEvaluatorTrainer neat)
            throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            neat.save(outputStream);

            return outputStream.toByteArray();
        }
    }

    private static NeatEvaluatorTrainer createEvaluatorTrainer(final byte[] bytes, final EvaluatorStateSettings stateSettings)
            throws IOException {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes)) {
            return Neat.createEvaluatorTrainer(inputStream, stateSettings);
        }
    }

    private static void assertPersistence(final NeatEvaluatorTrainer neat, final NeatEvaluatorSetup neatSetup, final boolean shouldUseParallelism) {
        try {
            byte[] bytes = getBytes(neat);

            Assertions.assertTrue(bytes.length > 30_000);
            Assertions.assertTrue(bytes.length < 1_000_000);

            EvaluatorStateSettings stateSettings = EvaluatorStateSettings.builder()
                    .meantToOverrideTopology(true)
                    .meantToOverrideSettings(true)
                    .environment(null)
                    .eventLoop(shouldUseParallelism ? EVENT_LOOP : null)
                    .build();

            NeatEvaluatorTrainer neatCopy = createEvaluatorTrainer(bytes, stateSettings);

            Assertions.assertEquals(neat.getGeneration(), neatCopy.getGeneration());
            Assertions.assertEquals(neat.getSpeciesCount(), neatCopy.getSpeciesCount());
            Assertions.assertEquals(neat.getMaximumFitness(), neatCopy.getMaximumFitness(), 0f);

            NeatEvaluatorTrainingResult result = neatSetup.trainingPolicy.test(new NeatActivatorEvaluatorTrainer(neatCopy));

            Assertions.assertEquals(NeatEvaluatorTrainingResult.WORKING_SOLUTION_FOUND, result);
        } catch (IOException e) {
            Assertions.fail(e.getMessage());
        }
    }

    private static void assertTheSolutionForTheProblem(final NeatEvaluatorSetup neatSetup, final boolean shouldTestSerialization) {
        NeatEvaluatorTrainer neat = Neat.createEvaluatorTrainer(neatSetup.settings);
        boolean success = neat.train(neatSetup.trainingPolicy);

        System.out.printf("=========================================%n");
        System.out.printf("%s (%s):%n", neatSetup.name, neatSetup.shouldUseParallelism ? "parallel" : "single");
        System.out.printf("=========================================%n");
        System.out.printf("generation: %d%n", neat.getGeneration());
        System.out.printf("species: %d%n", neat.getSpeciesCount());
        System.out.printf("fitness: %f%n", neat.getMaximumFitness());
        Assertions.assertTrue(success);
        Assertions.assertEquals(neatSetup.populationSize, neatSetup.genomeIds.size());

        if (shouldTestSerialization) {
            assertPersistence(neat, neatSetup, !neatSetup.shouldUseParallelism);
        }
    }

    private static NeatEvaluatorSetup createXorEvaluatorTest(final boolean shouldUseParallelism) {
        int populationSize = 150;

        float[][] inputs = new float[][]{
                new float[]{1f, 1f}, // 0f
                new float[]{1f, 0f}, // 1f
                new float[]{0f, 1f}, // 1f
                new float[]{0f, 0f}  // 0f
        };

        float[] expectedOutputs = new float[]{0f, 1f, 1f, 0f};

        NeatEnvironmentMock environment = NeatEnvironmentMock.builder()
                .shouldUseParallelism(shouldUseParallelism)
                .environment(genome -> {
                    float error = 0f;

                    for (int i = 0; i < inputs.length; i++) {
                        float[] output = genome.activate(inputs[i]);

                        error += (float) Math.pow(expectedOutputs[i] - output[0], 2D);
                    }

                    return inputs.length - error;
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

        return NeatEvaluatorSetup.builder()
                .name("XOR")
                .shouldUseParallelism(shouldUseParallelism)
                .populationSize(populationSize)
                .genomeIds(environment.genomeIds)
                .settings(EvaluatorSettings.builder()
                        .general(GeneralEvaluatorSupport.builder()
                                .populationSize(populationSize)
                                .genesisGenomeFactory(GenesisGenomeTemplate.builder()
                                        .inputs(IntegerNumber.literal(2))
                                        .inputBias(FloatNumber.literal(0f))
                                        .inputActivationFunction(EnumValue.literal(ActivationFunctionType.IDENTITY))
                                        .outputs(IntegerNumber.literal(1))
                                        .outputBias(FloatNumber.random(RandomType.UNIFORM, -1f, 1f))
                                        .outputActivationFunction(EnumValue.literal(OutputActivationFunctionType.SIGMOID))
                                        .biases(ImmutableList.of())
                                        .initialConnectionType(InitialConnectionType.ALL_INPUTS_AND_BIASES_TO_ALL_OUTPUTS)
                                        .initialWeightType(InitialWeightType.RANDOM)
                                        .build())
                                .fitnessDeterminerFactory(FitnessDeterminerFactory.createLastValue())
                                .fitnessFunction(environment)
                                .build())
                        .nodes(NodeGeneSupport.builder()
                                .hiddenBias(FloatNumber.random(RandomType.UNIFORM, -1f, 1f))
                                .hiddenActivationFunction(EnumValue.literal(ActivationFunctionType.TAN_H))
                                .build())
                        .connections(ConnectionGeneSupport.builder()
                                .weightFactory(FloatNumber.random(RandomType.UNIFORM, -1f, 1f))
                                .weightPerturber(FloatNumber.literal(2.5f))
                                .build())
                        .neuralNetwork(NeuralNetworkSupport.builder()
                                .type(NeuralNetworkType.MULTI_CYCLE_RECURRENT)
                                .build())
                        .parallelism(ParallelismSupport.builder()
                                .eventLoop(shouldUseParallelism ? EVENT_LOOP : null)
                                .build())
                        .random(RandomSupport.builder()
                                .nextIndex(RandomType.UNIFORM)
                                .isLessThan(RandomType.UNIFORM)
                                .build())
                        .mutation(MutationSupport.builder()
                                .addNodeMutationRate(FloatNumber.literal(0.1f))
                                .addConnectionMutationRate(FloatNumber.literal(0.2f))
                                .perturbConnectionsWeightRate(FloatNumber.literal(0.75f))
                                .replaceConnectionsWeightRate(FloatNumber.literal(0.5f))
                                .disableConnectionExpressedRate(FloatNumber.literal(0.05f))
                                .build())
                        .crossOver(CrossOverSupport.builder()
                                .mateOnlyRate(FloatNumber.literal(0.2f))
                                .mutateOnlyRate(FloatNumber.literal(0.25f))
                                .overrideConnectionExpressedRate(FloatNumber.literal(0.5f))
                                .useRandomParentConnectionWeightRate(FloatNumber.literal(0.6f))
                                .build())
                        .speciation(SpeciationSupport.builder()
                                .maximumSpecies(IntegerNumber.literal(20))
                                .maximumGenomes(IntegerNumber.literal(20))
                                .weightDifferenceCoefficient(FloatNumber.literal(0.5f))
                                .disjointCoefficient(FloatNumber.literal(1f))
                                .excessCoefficient(FloatNumber.literal(1f))
                                .compatibilityThreshold(FloatNumber.literal(3f))
                                .compatibilityThresholdModifier(FloatNumber.literal(1.2f))
                                .eugenicsThreshold(FloatNumber.literal(0.2f))
                                .elitistThreshold(FloatNumber.literal(0.01f))
                                .elitistThresholdMinimum(IntegerNumber.literal(2))
                                .stagnationDropOffAge(IntegerNumber.literal(15))
                                .interSpeciesMatingRate(FloatNumber.literal(0.001f))
                                .build())
                        .build())
                .trainingPolicy(NeatEvaluatorTrainingPolicies.builder()
                        .add(NeatEvaluatorTrainingPolicy.maximumGenerations(2_000, NeatEvaluatorTrainingResult.EVALUATE_FITNESS_AND_EVOLVE))
                        .add(trainingPolicy)
                        .build())
                .build();
    }

    @Test
    @Order(1)
    public void GIVEN_a_single_threaded_neat_evaluator_WHEN_finding_the_solution_to_the_first_problem_which_is_xor_THEN_find_the_solution() {
        assertTheSolutionForTheProblem(createXorEvaluatorTest(false), false);
    }

    @Test
    @Order(2)
    public void GIVEN_a_multi_threaded_neat_evaluator_WHEN_finding_the_solution_to_the_first_problem_which_is_xor_THEN_find_the_solution() {
        assertTheSolutionForTheProblem(createXorEvaluatorTest(true), false);
    }

    @Test
    @Order(3)
    public void GIVEN_a_single_threaded_neat_evaluator_WHEN_finding_the_solution_to_the_first_problem_which_is_xor_THEN_find_the_solution_also_save_it_and_transfer_it() {
        assertTheSolutionForTheProblem(createXorEvaluatorTest(false), true);
    }

    @Test
    @Order(4)
    public void GIVEN_a_multi_threaded_neat_evaluator_WHEN_finding_the_solution_to_the_first_problem_which_is_xor_THEN_find_the_solution_also_save_it_and_transfer_it() {
        assertTheSolutionForTheProblem(createXorEvaluatorTest(true), true);
    }

    private static float[] convertToFloat(final double[] input) {
        float[] output = new float[input.length];

        for (int i = 0; i < input.length; i++) {
            output[i] = (float) input[i];
        }

        return output;
    }

    private static NeatEvaluatorSetup createSinglePoleBalancingTest(final double timeSpentGoal, final boolean shouldUseParallelism) {
        int populationSize = 150;

        NeatEnvironmentMock environment = NeatEnvironmentMock.builder()
                .shouldUseParallelism(shouldUseParallelism)
                .environment(genome -> {
                    float minimumTimeSpent = Float.MAX_VALUE;

                    for (int i = 0, attempts = 5; i < attempts; i++) {
                        CartPoleEnvironment cartPole = CartPoleEnvironment.builder()
                                .build();

                        while (!cartPole.isLimitHit() && Double.compare(cartPole.getTimeSpent(), timeSpentGoal) < 0) {
                            float[] input = convertToFloat(cartPole.getState());
                            float[] output = genome.activate(input);

                            cartPole.stepInDiscrete(output[0]);
                        }

                        minimumTimeSpent = Math.min(minimumTimeSpent, (float) cartPole.getTimeSpent());
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
                CartPoleEnvironment cartPole = CartPoleEnvironment.builder()
                        .build();

                while (!cartPole.isLimitHit() && Double.compare(cartPole.getTimeSpent(), timeSpentGoal) < 0) {
                    float[] input = convertToFloat(cartPole.getState());
                    float[] output = na.activate(input);

                    cartPole.stepInDiscrete(output[0]);
                }

                success = Double.compare(cartPole.getTimeSpent(), timeSpentGoal) >= 0;
            }

            if (success) {
                return NeatEvaluatorTrainingResult.WORKING_SOLUTION_FOUND;
            }

            return NeatEvaluatorTrainingResult.EVALUATE_FITNESS_AND_EVOLVE;
        };

        return NeatEvaluatorSetup.builder()
                .name("Single Pole Balancing")
                .shouldUseParallelism(shouldUseParallelism)
                .populationSize(populationSize)
                .genomeIds(environment.genomeIds)
                .settings(EvaluatorSettings.builder()
                        .general(GeneralEvaluatorSupport.builder()
                                .populationSize(populationSize)
                                .genesisGenomeFactory(GenesisGenomeTemplate.builder()
                                        .inputs(IntegerNumber.literal(4))
                                        .inputBias(FloatNumber.literal(0f))
                                        .inputActivationFunction(EnumValue.literal(ActivationFunctionType.IDENTITY))
                                        .outputs(IntegerNumber.literal(1))
                                        .outputBias(FloatNumber.random(RandomType.UNIFORM, -1f, 1f))
                                        .outputActivationFunction(EnumValue.literal(OutputActivationFunctionType.TAN_H))
                                        .biases(ImmutableList.of())
                                        .initialConnectionType(InitialConnectionType.ALL_INPUTS_AND_BIASES_TO_ALL_OUTPUTS)
                                        .initialWeightType(InitialWeightType.RANDOM)
                                        .build())
                                .fitnessDeterminerFactory(FitnessDeterminerFactory.createLastValue())
                                .fitnessFunction(environment)
                                .build())
                        .nodes(NodeGeneSupport.builder()
                                .hiddenBias(FloatNumber.random(RandomType.UNIFORM, -1f, 1f))
                                .hiddenActivationFunction(EnumValue.literal(ActivationFunctionType.SIGMOID))
                                .build())
                        .connections(ConnectionGeneSupport.builder()
                                .weightFactory(FloatNumber.random(RandomType.UNIFORM, -1f, 1f))
                                .weightPerturber(FloatNumber.literal(1.8f))
                                .build())
                        .neuralNetwork(NeuralNetworkSupport.builder()
                                .type(NeuralNetworkType.MULTI_CYCLE_RECURRENT)
                                .build())
                        .parallelism(ParallelismSupport.builder()
                                .eventLoop(shouldUseParallelism ? EVENT_LOOP : null)
                                .build())
                        .random(RandomSupport.builder()
                                .nextIndex(RandomType.UNIFORM)
                                .isLessThan(RandomType.UNIFORM)
                                .build())
                        .mutation(MutationSupport.builder()
                                .addNodeMutationRate(FloatNumber.literal(0.1f))
                                .addConnectionMutationRate(FloatNumber.literal(0.2f))
                                .perturbConnectionsWeightRate(FloatNumber.literal(0.75f))
                                .replaceConnectionsWeightRate(FloatNumber.literal(0.5f))
                                .disableConnectionExpressedRate(FloatNumber.literal(0.05f))
                                .build())
                        .crossOver(CrossOverSupport.builder()
                                .mateOnlyRate(FloatNumber.literal(0.2f))
                                .mutateOnlyRate(FloatNumber.literal(0.25f))
                                .overrideConnectionExpressedRate(FloatNumber.literal(0.5f))
                                .useRandomParentConnectionWeightRate(FloatNumber.literal(0.6f))
                                .build())
                        .speciation(SpeciationSupport.builder()
                                .maximumSpecies(IntegerNumber.literal(20))
                                .maximumGenomes(IntegerNumber.literal(20))
                                .weightDifferenceCoefficient(FloatNumber.literal(0.5f))
                                .disjointCoefficient(FloatNumber.literal(1f))
                                .excessCoefficient(FloatNumber.literal(1f))
                                .compatibilityThreshold(FloatNumber.literal(4f))
                                .compatibilityThresholdModifier(FloatNumber.literal(1.2f))
                                .eugenicsThreshold(FloatNumber.literal(0.4f))
                                .elitistThreshold(FloatNumber.literal(0.01f))
                                .elitistThresholdMinimum(IntegerNumber.literal(2))
                                .stagnationDropOffAge(IntegerNumber.literal(15))
                                .interSpeciesMatingRate(FloatNumber.literal(0.001f))
                                .build())
                        .build())
                .trainingPolicy(NeatEvaluatorTrainingPolicies.builder()
                        .add(NeatEvaluatorTrainingPolicy.maximumGenerations(2_000, NeatEvaluatorTrainingResult.EVALUATE_FITNESS_AND_EVOLVE))
                        .add(trainingPolicy)
                        .build())
                .build();
    }

    @Test
    @Order(5)
    public void GIVEN_a_single_threaded_neat_evaluator_WHEN_finding_the_solution_to_the_second_problem_which_is_the_single_pole_balancing_problem_in_a_discrete_environment_THEN_find_the_solution() {
        assertTheSolutionForTheProblem(createSinglePoleBalancingTest(60D, false), false);
    }

    @Test
    @Order(6)
    public void GIVEN_a_multi_threaded_neat_evaluator_WHEN_finding_the_solution_to_the_second_problem_which_is_the_single_pole_balancing_problem_in_a_discrete_environment_THEN_find_the_solution() {
        assertTheSolutionForTheProblem(createSinglePoleBalancingTest(60D, true), false);
    }

    @Test
    @Order(7)
    public void GIVEN_a_single_threaded_neat_evaluator_WHEN_finding_the_solution_to_the_second_problem_which_is_the_single_pole_balancing_problem_in_a_discrete_environment_THEN_find_the_solution_also_save_it_and_transfer_it() {
        assertTheSolutionForTheProblem(createSinglePoleBalancingTest(60D, false), true);
    }

    @Test
    @Order(8)
    public void GIVEN_a_multi_threaded_neat_evaluator_WHEN_finding_the_solution_to_the_second_problem_which_is_the_single_pole_balancing_problem_in_a_discrete_environment_THEN_find_the_solution_also_save_it_and_transfer_it() {
        assertTheSolutionForTheProblem(createSinglePoleBalancingTest(60D, true), true);
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class NeatEnvironmentMock implements NeatEnvironment {
        @Serial
        private static final long serialVersionUID = 395615026591016419L;
        private final Set<String> genomeIds;
        private final NeatEnvironment environment;

        @Builder(access = AccessLevel.PRIVATE)
        public static NeatEnvironmentMock create(final boolean shouldUseParallelism, final NeatEnvironment environment) {
            Set<String> genomeIds = !shouldUseParallelism
                    ? new HashSet<>()
                    : Collections.newSetFromMap(new ConcurrentHashMap<>());

            return new NeatEnvironmentMock(genomeIds, environment);
        }

        @Override
        public float test(final Genome genome) {
            genomeIds.add(genome.getId());

            return environment.test(genome);
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder(access = AccessLevel.PRIVATE)
    private static final class NeatEvaluatorSetup {
        private final String name;
        private final boolean shouldUseParallelism;
        private final int populationSize;
        private final Set<String> genomeIds;
        private final EvaluatorSettings settings;
        private final NeatEvaluatorTrainingPolicy trainingPolicy;
    }
}
