package com.dipasquale.ai.rl.neat.core;

import com.dipasquale.common.JvmWarmup;
import com.dipasquale.common.time.MillisecondsDateTimeSupport;
import com.dipasquale.simulation.openai.gym.client.GymClient;
import com.dipasquale.synchronization.event.loop.IterableEventLoop;
import com.dipasquale.synchronization.event.loop.IterableEventLoopSettings;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public final class NeatTest {
    private static final boolean METRICS_EMISSION_ENABLED = false;
    private static final boolean XOR_TASK_ENABLED = true;
    private static final boolean SINGLE_POLE_CART_BALANCE_TASK_ENABLED = true;
    private static final boolean OPEN_AI_TASKS_ENABLED = false;
    private static final boolean OPEN_AI_CART_POLE_TASK_ENABLED = true;
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
    private static GymClient GYM_CLIENT = null;

    private static void startOpenAIGymServer()
            throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder("python", "gym_http_server.py");
        String currentDirectory = System.getProperty("user.dir");
        String gymHttpApiDirectory = Path.of(new File(currentDirectory).getParent(), "gym-http-api").toString();

        processBuilder.directory(new File(gymHttpApiDirectory));
        processBuilder.start();
    }

    @BeforeAll
    public static void beforeAll() {
        if (OPEN_AI_TASKS_ENABLED) {
            try {
                System.setProperty("jdk.http.auth.proxying.disabledSchemes", "");
                System.setProperty("jdk.http.auth.tunneling.disabledSchemes", "");
                startOpenAIGymServer();
                GYM_CLIENT = new GymClient();
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }

        JvmWarmup.start(100_000);
    }

    @AfterAll
    public static void afterAll() {
        if (GYM_CLIENT != null) {
            GYM_CLIENT.shutdown();
        }

        EVENT_LOOP.shutdown();
        EXECUTOR_SERVICE.shutdown();
    }

    @BeforeEach
    public void beforeEach() {
        UNHANDLED_EXCEPTIONS.clear();
    }

    private static boolean isTaskEnabled(final TaskSetup taskSetup) {
        return XOR_TASK_ENABLED && taskSetup instanceof XorTaskSetup
                || SINGLE_POLE_CART_BALANCE_TASK_ENABLED && taskSetup instanceof SinglePoleCartBalanceTaskSetup
                || OPEN_AI_TASKS_ENABLED && OPEN_AI_CART_POLE_TASK_ENABLED && taskSetup instanceof OpenAIGymCartPoleTaskSetup;
    }

    private static void assertTaskSolution(final NeatTestSetup testSetup) {
        if (!isTaskEnabled(testSetup.getTask())) {
            return;
        }

        testSetup.assertTaskSolution();
    }

    @Test
    @Timeout(value = 85_500, unit = TimeUnit.MILLISECONDS)
    public void GIVEN_a_single_instance_single_threaded_neat_trainer_WHEN_finding_the_solution_to_the_is_xor_problem_THEN_evaluate_fitness_and_evolve_until_finding_the_solution() {
        assertTaskSolution(NeatTestSetup.builder()
                .task(new XorTaskSetup(METRICS_EMISSION_ENABLED))
                .eventLoop(null)
                .shouldTestPersistence(false)
                .build());
    }

    @Test
    @Timeout(value = 85_500, unit = TimeUnit.MILLISECONDS)
    public void GIVEN_a_single_instance_multi_threaded_neat_trainer_WHEN_finding_the_solution_to_the_is_xor_problem_THEN_evaluate_fitness_and_evolve_until_finding_the_solution() {
        assertTaskSolution(NeatTestSetup.builder()
                .task(new XorTaskSetup(METRICS_EMISSION_ENABLED))
                .eventLoop(EVENT_LOOP)
                .shouldTestPersistence(false)
                .build());
    }

    @Test
    @Timeout(value = 85_500, unit = TimeUnit.MILLISECONDS)
    public void GIVEN_a_single_instance_single_threaded_neat_trainer_WHEN_finding_the_solution_to_the_is_xor_problem_THEN_evaluate_fitness_and_evolve_until_finding_the_solution_to_then_save_it_and_transfer_it() {
        assertTaskSolution(NeatTestSetup.builder()
                .task(new XorTaskSetup(METRICS_EMISSION_ENABLED))
                .eventLoop(null)
                .shouldTestPersistence(true)
                .build());
    }

    @Test
    @Timeout(value = 85_500, unit = TimeUnit.MILLISECONDS)
    public void GIVEN_a_single_instance_multi_threaded_neat_trainer_WHEN_finding_the_solution_to_the_is_xor_problem_THEN_evaluate_fitness_and_evolve_until_finding_the_solution_to_then_save_it_and_transfer_it() {
        assertTaskSolution(NeatTestSetup.builder()
                .task(new XorTaskSetup(METRICS_EMISSION_ENABLED))
                .eventLoop(EVENT_LOOP)
                .shouldTestPersistence(true)
                .build());
    }

    @Test
    @Timeout(value = 105_500, unit = TimeUnit.MILLISECONDS)
    public void GIVEN_a_single_instance_multi_threaded_neat_trainer_WHEN_finding_the_solution_to_the_single_pole_cart_balance_problem_in_a_discrete_environment_THEN_evaluate_fitness_and_evolve_until_finding_the_solution() {
        assertTaskSolution(NeatTestSetup.builder()
                .task(new SinglePoleCartBalanceTaskSetup(METRICS_EMISSION_ENABLED))
                .eventLoop(EVENT_LOOP)
                .shouldTestPersistence(false)
                .build());
    }

    @Test
    @Timeout(value = 85_500, unit = TimeUnit.MILLISECONDS)
    public void GIVEN_a_multi_instance_neat_trainer_WHEN_finding_the_solution_to_the_is_xor_problem_THEN_evaluate_fitness_and_evolve_until_finding_the_solution() {
        assertTaskSolution(NeatTestSetup.builder()
                .task(new XorTaskSetup(METRICS_EMISSION_ENABLED))
                .eventLoop(EVENT_LOOP)
                .neatTrainerFactory(Neat::createMultiTrainer)
                .shouldTestPersistence(false)
                .build());
    }

    @Test
    @Timeout(value = 85_500, unit = TimeUnit.MILLISECONDS)
    public void GIVEN_a_single_instance_multi_threaded_neat_trainer_WHEN_finding_the_solution_to_the_single_pole_cart_balance_problem_in_a_discrete_environment_THEN_evaluate_fitness_and_evolve_until_finding_the_solution_to_then_save_it_and_transfer_it() {
        assertTaskSolution(NeatTestSetup.builder()
                .task(new SinglePoleCartBalanceTaskSetup(METRICS_EMISSION_ENABLED))
                .eventLoop(EVENT_LOOP)
                .shouldTestPersistence(true)
                .build());
    }

    @Test
    @Timeout(value = 300_500, unit = TimeUnit.MILLISECONDS)
    public void GIVEN_a_single_instance_single_threaded_neat_trainer_WHEN_finding_the_solution_to_the_open_ai_gym_cart_pole_problem_THEN_evaluate_fitness_and_evolve_until_finding_the_solution() {
        assertTaskSolution(NeatTestSetupOpenAIGym.openAIGymBuilder()
                .task(new OpenAIGymCartPoleTaskSetup(GYM_CLIENT, METRICS_EMISSION_ENABLED))
                .eventLoop(null)
                .shouldTestPersistence(false)
                .shouldVisualizeSolution(false)
                .build());
    }
}
