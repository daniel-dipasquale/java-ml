package com.dipasquale.ai.rl.neat.core;

import com.dipasquale.ai.rl.neat.settings.EvaluatorOverrideSettings;
import com.dipasquale.ai.rl.neat.settings.EvaluatorSettings;
import com.dipasquale.common.JvmWarmup;
import com.dipasquale.common.time.MillisecondsDateTimeSupport;
import com.dipasquale.synchronization.event.loop.IterableEventLoop;
import com.dipasquale.synchronization.event.loop.IterableEventLoopSettings;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public final class NeatTest {
    private static final boolean XOR_TASK_ENABLED = true;
    private static final boolean SINGLE_POLE_BALANCING_TASK_ENABLED = true;
    private static final Set<String> GENOME_IDS = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private static final int NUMBER_OF_THREADS = Math.max(1, Runtime.getRuntime().availableProcessors() - 1);
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    private static final List<Throwable> UNHANDLED_EXCEPTIONS = Collections.synchronizedList(new ArrayList<>());

    private static final IterableEventLoopSettings EVENT_LOOP_SETTINGS = IterableEventLoopSettings.builder()
            .executorService(EXECUTOR_SERVICE)
            .numberOfThreads(NUMBER_OF_THREADS)
            .errorHandler(UNHANDLED_EXCEPTIONS::add)
            .dateTimeSupport(new MillisecondsDateTimeSupport())
            .build();

    private static final IterableEventLoop EVENT_LOOP = new IterableEventLoop(EVENT_LOOP_SETTINGS);

    @BeforeAll
    public static void beforeAll() {
        JvmWarmup.start(100_000);
    }

    @AfterAll
    public static void afterAll() {
        EVENT_LOOP.shutdown();
        EXECUTOR_SERVICE.shutdown();
    }

    @BeforeEach
    public void beforeEach() {
        GENOME_IDS.clear();
        UNHANDLED_EXCEPTIONS.clear();
    }

    private static boolean isTaskEnabled(final TaskSetup taskSetup) {
        return XOR_TASK_ENABLED && taskSetup instanceof XorTaskSetup
                || SINGLE_POLE_BALANCING_TASK_ENABLED && taskSetup instanceof SinglePoleBalancingTaskSetup;
    }

    private static byte[] getBytes(final NeatTrainer trainer)
            throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            trainer.save(outputStream);

            return outputStream.toByteArray();
        }
    }

    private static NeatTrainer createTrainer(final byte[] bytes, final EvaluatorOverrideSettings overrideSettings)
            throws IOException {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes)) {
            return Neat.createTrainer(inputStream, overrideSettings);
        }
    }

    private static NeatTrainerSetup createTrainerSetup(final NeatTestSetup testSetup) {
        IterableEventLoop eventLoop = testSetup.shouldTestParallelism ? EVENT_LOOP : null;

        return NeatTrainerSetup.builder()
                .name(testSetup.task.getName())
                .shouldTestParallelism(testSetup.shouldTestParallelism)
                .settings(testSetup.task.createSettings(GENOME_IDS, eventLoop))
                .trainingPolicy(testSetup.task.createTrainingPolicy())
                .build();
    }

    private static void assertTrainingResults(final NeatTrainer trainer, final NeatTrainerSetup trainerSetup, final int populationSize) {
        boolean success = trainer.train(trainerSetup.trainingPolicy);

        System.out.printf("=========================================%n");
        System.out.printf("%s (%s):%n", trainerSetup.name, trainerSetup.shouldTestParallelism ? "parallel" : "single");
        System.out.printf("=========================================%n");
        System.out.printf("generation: %d%n", trainer.getGeneration());
        System.out.printf("species: %d%n", trainer.getSpeciesCount());
        System.out.printf("complexity: %d%n", trainer.getCurrentComplexity());
        System.out.printf("fitness: %f%n", trainer.getMaximumFitness());
        Assertions.assertTrue(success);
        Assertions.assertEquals(populationSize, GENOME_IDS.size());
    }

    private static NeatTrainingResult test(final NeatTrainingPolicy trainingPolicy, final NeatTrainer trainer) {
        return trainingPolicy.testOnce(new NeatActivatorTrainer(trainer));
    }

    private static void assertPersistence(final NeatTrainer trainer, final NeatTrainerSetup trainerSetup, final boolean shouldTestParallelism) {
        try {
            byte[] bytes = getBytes(trainer);

            Assertions.assertTrue(bytes.length > 30_000);
            Assertions.assertTrue(bytes.length < 1_000_000);

            EvaluatorOverrideSettings overrideSettings = EvaluatorOverrideSettings.builder()
                    .fitnessFunction(null)
                    .eventLoop(shouldTestParallelism ? EVENT_LOOP : null)
                    .build();

            NeatTrainer trainerCopy = createTrainer(bytes, overrideSettings);

            Assertions.assertEquals(trainer.getGeneration(), trainerCopy.getGeneration());
            Assertions.assertEquals(trainer.getSpeciesCount(), trainerCopy.getSpeciesCount());
            Assertions.assertEquals(trainer.getMaximumFitness(), trainerCopy.getMaximumFitness(), 0f);
            Assertions.assertEquals(NeatTrainingResult.WORKING_SOLUTION_FOUND, test(trainerSetup.trainingPolicy, trainer));
            Assertions.assertEquals(NeatTrainingResult.WORKING_SOLUTION_FOUND, test(trainerSetup.trainingPolicy, trainerCopy));
        } catch (IOException e) {
            Assertions.fail(e.getMessage());
        }
    }

    private static void assertTaskSolution(final NeatTestSetup testSetup) {
        if (!isTaskEnabled(testSetup.task)) {
            return;
        }

        NeatTrainerSetup trainerSetup = createTrainerSetup(testSetup);
        NeatTrainer trainer = Neat.createTrainer(trainerSetup.settings);
        int populationSize = testSetup.task.getPopulationSize();

        assertTrainingResults(trainer, trainerSetup, populationSize);

        if (testSetup.shouldTestPersistence) {
            assertPersistence(trainer, trainerSetup, !trainerSetup.shouldTestParallelism);
        }
    }

    @Test
    @Timeout(value = 2, unit = TimeUnit.MINUTES)
    @Order(1)
    public void GIVEN_a_single_threaded_neat_trainer_WHEN_finding_the_solution_to_the_is_xor_problem_THEN_evaluate_fitness_and_evolve_until_finding_the_solution() {
        assertTaskSolution(NeatTestSetup.builder()
                .task(new XorTaskSetup())
                .shouldTestParallelism(false)
                .shouldTestPersistence(false)
                .build());
    }

    @Test
    @Timeout(value = 2, unit = TimeUnit.MINUTES)
    @Order(2)
    public void GIVEN_a_multi_threaded_neat_trainer_WHEN_finding_the_solution_to_the_is_xor_problem_THEN_evaluate_fitness_and_evolve_until_finding_the_solution() {
        assertTaskSolution(NeatTestSetup.builder()
                .task(new XorTaskSetup())
                .shouldTestParallelism(true)
                .shouldTestPersistence(false)
                .build());
    }

    @Test
    @Timeout(value = 2, unit = TimeUnit.MINUTES)
    @Order(3)
    public void GIVEN_a_single_threaded_neat_trainer_WHEN_finding_the_solution_to_the_is_xor_problem_THEN_evaluate_fitness_and_evolve_until_finding_the_solution_to_then_save_it_and_transfer_it() {
        assertTaskSolution(NeatTestSetup.builder()
                .task(new XorTaskSetup())
                .shouldTestParallelism(false)
                .shouldTestPersistence(true)
                .build());
    }

    @Test
    @Timeout(value = 2, unit = TimeUnit.MINUTES)
    @Order(4)
    public void GIVEN_a_multi_threaded_neat_trainer_WHEN_finding_the_solution_to_the_is_xor_problem_THEN_evaluate_fitness_and_evolve_until_finding_the_solution_to_then_save_it_and_transfer_it() {
        assertTaskSolution(NeatTestSetup.builder()
                .task(new XorTaskSetup())
                .shouldTestParallelism(true)
                .shouldTestPersistence(true)
                .build());
    }

    @Test
    @Timeout(value = 2, unit = TimeUnit.MINUTES)
    @Order(6)
    public void GIVEN_a_multi_threaded_neat_trainer_WHEN_finding_the_solution_to_the_single_pole_balancing_problem_in_a_discrete_environment_THEN_evaluate_fitness_and_evolve_until_finding_the_solution() {
        assertTaskSolution(NeatTestSetup.builder()
                .task(new SinglePoleBalancingTaskSetup())
                .shouldTestParallelism(true)
                .shouldTestPersistence(false)
                .build());
    }

    @Test
    @Timeout(value = 2, unit = TimeUnit.MINUTES)
    @Order(8)
    public void GIVEN_a_multi_threaded_neat_trainer_WHEN_finding_the_solution_to_the_single_pole_balancing_problem_in_a_discrete_environment_THEN_evaluate_fitness_and_evolve_until_finding_the_solution_to_then_save_it_and_transfer_it() {
        assertTaskSolution(NeatTestSetup.builder()
                .task(new SinglePoleBalancingTaskSetup())
                .shouldTestParallelism(true)
                .shouldTestPersistence(true)
                .build());
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder(access = AccessLevel.PRIVATE)
    private static final class NeatTrainerSetup {
        private final String name;
        private final boolean shouldTestParallelism;
        private final EvaluatorSettings settings;
        private final NeatTrainingPolicy trainingPolicy;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder(access = AccessLevel.PRIVATE)
    private static final class NeatTestSetup {
        private final TaskSetup task;
        private final boolean shouldTestParallelism;
        private final boolean shouldTestPersistence;
    }
}
