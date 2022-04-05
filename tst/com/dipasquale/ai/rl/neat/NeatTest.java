package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.rl.neat.common.NeatTestSetup;
import com.dipasquale.ai.rl.neat.common.TaskSetup;
import com.dipasquale.ai.rl.neat.common.cartpole.CartSinglePoleBalanceTaskSetup;
import com.dipasquale.ai.rl.neat.common.game2048.Game2048TaskSetup;
import com.dipasquale.ai.rl.neat.common.openai.GymClientProvider;
import com.dipasquale.ai.rl.neat.common.openai.NeatTestSetupOpenAIGym;
import com.dipasquale.ai.rl.neat.common.openai.cartpole.OpenAIGymCartPoleTaskSetup;
import com.dipasquale.ai.rl.neat.common.tictactoe.TicTacToeTaskSetup;
import com.dipasquale.ai.rl.neat.common.xor.XorTaskSetup;
import com.dipasquale.common.JvmWarmup;
import com.dipasquale.common.time.MillisecondsDateTimeSupport;
import com.dipasquale.synchronization.event.loop.BatchingEventLoop;
import com.dipasquale.synchronization.event.loop.BatchingEventLoopSettings;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public final class NeatTest {
    private static final boolean XOR_TASK_ENABLED = true;
    private static final boolean CART_SINGLE_POLE_BALANCE_TASKS_ENABLED = true;
    private static final boolean OPEN_AI_TASKS_ENABLED = false;
    private static final boolean OPEN_AI_CART_POLE_TASKS_ENABLED = false;
    private static final boolean TIC_TAC_TOE_TASKS_ENABLED = false;
    private static final boolean GAME_2048_TASKS_ENABLED = false;
    private static final int NUMBER_OF_THREADS = Math.max(1, Runtime.getRuntime().availableProcessors() - 1);
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    private static final List<Throwable> UNHANDLED_EXCEPTIONS = Collections.synchronizedList(new ArrayList<>());

    private static final BatchingEventLoopSettings EVENT_LOOP_SETTINGS = BatchingEventLoopSettings.builder()
            .executorService(EXECUTOR_SERVICE)
            .numberOfThreads(NUMBER_OF_THREADS)
            .errorHandler(UNHANDLED_EXCEPTIONS::add)
            .dateTimeSupport(new MillisecondsDateTimeSupport())
            .build();

    private static final BatchingEventLoop EVENT_LOOP = new BatchingEventLoop(EVENT_LOOP_SETTINGS);

    @BeforeAll
    public static void beforeAll() {
        JvmWarmup.start(100_000);
    }

    @AfterAll
    public static void afterAll() {
        if (OPEN_AI_TASKS_ENABLED) {
            GymClientProvider.getGymClient().shutdown();
        }

        EVENT_LOOP.shutdown();
        EXECUTOR_SERVICE.shutdown();
    }

    @BeforeEach
    public void beforeEach() {
        UNHANDLED_EXCEPTIONS.clear();
        EVENT_LOOP.clear();
    }

    private static boolean isTaskEnabled(final TaskSetup taskSetup) {
        return XOR_TASK_ENABLED && taskSetup instanceof XorTaskSetup
                || CART_SINGLE_POLE_BALANCE_TASKS_ENABLED && taskSetup instanceof CartSinglePoleBalanceTaskSetup
                || OPEN_AI_TASKS_ENABLED && OPEN_AI_CART_POLE_TASKS_ENABLED && taskSetup instanceof OpenAIGymCartPoleTaskSetup
                || TIC_TAC_TOE_TASKS_ENABLED && taskSetup instanceof TicTacToeTaskSetup
                || GAME_2048_TASKS_ENABLED && taskSetup instanceof Game2048TaskSetup;
    }

    private static void assertTaskSolution(final NeatTestSetup testSetup) {
        if (!isTaskEnabled(testSetup.getTask())) {
            return;
        }

        testSetup.assertTaskSolution();
    }

    @Test
    @Timeout(value = 85_500, unit = TimeUnit.MILLISECONDS)
    public void GIVEN_a_single_instance_single_threaded_neat_trainer_WHEN_finding_the_solution_to_the_xor_problem_THEN_evaluate_fitness_and_evolve_until_finding_the_solution() {
        assertTaskSolution(NeatTestSetup.builder()
                .task(XorTaskSetup.builder()
                        .metricsEmissionEnabled(false)
                        .build())
                .eventLoop(null)
                .shouldTestPersistence(false)
                .build());
    }

    @Test
    @Timeout(value = 85_501, unit = TimeUnit.MILLISECONDS)
    public void GIVEN_a_single_instance_multi_threaded_neat_trainer_WHEN_finding_the_solution_to_the_xor_problem_THEN_evaluate_fitness_and_evolve_until_finding_the_solution() {
        assertTaskSolution(NeatTestSetup.builder()
                .task(XorTaskSetup.builder()
                        .metricsEmissionEnabled(false)
                        .build())
                .eventLoop(EVENT_LOOP)
                .shouldTestPersistence(false)
                .build());
    }

    @Test
    @Timeout(value = 85_500, unit = TimeUnit.MILLISECONDS)
    public void GIVEN_a_single_instance_single_threaded_neat_trainer_WHEN_finding_the_solution_to_the_xor_problem_THEN_evaluate_fitness_and_evolve_until_finding_the_solution_to_then_save_it_and_transfer_it() {
        assertTaskSolution(NeatTestSetup.builder()
                .task(XorTaskSetup.builder()
                        .metricsEmissionEnabled(false)
                        .build())
                .eventLoop(null)
                .shouldTestPersistence(true)
                .build());
    }

    @Test
    @Timeout(value = 85_500, unit = TimeUnit.MILLISECONDS)
    public void GIVEN_a_single_instance_multi_threaded_neat_trainer_WHEN_finding_the_solution_to_the_xor_problem_THEN_evaluate_fitness_and_evolve_until_finding_the_solution_to_then_save_it_and_transfer_it() {
        assertTaskSolution(NeatTestSetup.builder()
                .task(XorTaskSetup.builder()
                        .metricsEmissionEnabled(false)
                        .build())
                .eventLoop(EVENT_LOOP)
                .shouldTestPersistence(true)
                .build());
    }

    @Test
    @Timeout(value = 85_500, unit = TimeUnit.MILLISECONDS)
    public void GIVEN_a_multi_instance_neat_trainer_WHEN_finding_the_solution_to_the_xor_problem_THEN_evaluate_fitness_and_evolve_until_finding_the_solution() {
        assertTaskSolution(NeatTestSetup.builder()
                .task(XorTaskSetup.builder()
                        .metricsEmissionEnabled(false)
                        .build())
                .eventLoop(EVENT_LOOP)
                .trainerFactory(Neat::createParallelTrainer)
                .shouldTestPersistence(false)
                .build());
    }

    @Test
    @Timeout(value = 85_500, unit = TimeUnit.MILLISECONDS)
    public void GIVEN_a_single_instance_multi_threaded_neat_trainer_WHEN_finding_the_solution_to_the_cart_single_pole_balance_problem_in_a_discrete_environment_THEN_evaluate_fitness_and_evolve_until_finding_the_solution() {
        assertTaskSolution(NeatTestSetup.builder()
                .task(CartSinglePoleBalanceTaskSetup.builder()
                        .metricsEmissionEnabled(false)
                        .build())
                .eventLoop(EVENT_LOOP)
                .shouldTestPersistence(false)
                .build());
    }

    @Test
    @Timeout(value = 200_500, unit = TimeUnit.MILLISECONDS)
    public void GIVEN_a_single_instance_single_threaded_neat_trainer_WHEN_finding_the_solution_to_the_open_ai_gym_cart_pole_problem_THEN_evaluate_fitness_and_evolve_until_finding_the_solution() {
        assertTaskSolution(NeatTestSetupOpenAIGym.openAIGymBuilder()
                .task(OpenAIGymCartPoleTaskSetup.builder()
                        .gymClient(OPEN_AI_TASKS_ENABLED
                                ? GymClientProvider.getGymClient()
                                : null)
                        .metricsEmissionEnabled(false)
                        .build())
                .eventLoop(null)
                .shouldTestPersistence(false)
                .shouldVisualizeSolution(false)
                .build());
    }

    @Test
    @Timeout(value = 255_500, unit = TimeUnit.MILLISECONDS)
    public void GIVEN_a_single_instance_multi_threaded_neat_trainer_WHEN_finding_the_solution_to_the_tic_tac_toe_problem_THEN_evaluate_fitness_and_evolve_until_finding_the_solution() {
        assertTaskSolution(NeatTestSetup.builder()
                .task(TicTacToeTaskSetup.builder()
                        .metricsEmissionEnabled(false)
                        .build())
                .eventLoop(EVENT_LOOP)
                .shouldTestPersistence(false)
                .build());
    }

    @Test
    @Timeout(value = 300_500, unit = TimeUnit.MILLISECONDS)
    public void GIVEN_a_single_instance_multi_threaded_neat_trainer_WHEN_finding_the_solution_to_the_game_2048_problem_THEN_evaluate_fitness_and_evolve_until_finding_the_solution() {
        assertTaskSolution(NeatTestSetup.builder()
                .task(Game2048TaskSetup.builder()
                        .metricsEmissionEnabled(false)
                        .build())
                .eventLoop(EVENT_LOOP)
                .shouldTestPersistence(false)
                .build());
    }
}
