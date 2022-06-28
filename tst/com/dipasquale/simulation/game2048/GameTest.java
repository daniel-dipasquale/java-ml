package com.dipasquale.simulation.game2048;

import com.dipasquale.common.JvmWarmup;
import com.dipasquale.common.random.DeterministicRandomSupport;
import com.dipasquale.common.random.UniformRandomSupport;
import com.dipasquale.common.time.MillisecondsDateTimeSupport;
import com.dipasquale.io.StandardIOClient;
import com.dipasquale.search.mcts.SearchResult;
import com.dipasquale.search.mcts.buffer.BufferType;
import com.dipasquale.search.mcts.concurrent.ConcurrencySettings;
import com.dipasquale.search.mcts.concurrent.EdgeTraversalLockType;
import com.dipasquale.search.mcts.heuristic.HeuristicMonteCarloTreeSearch;
import com.dipasquale.search.mcts.heuristic.selection.CPuctAlgorithm;
import com.dipasquale.search.mcts.heuristic.selection.RewardHeuristic;
import com.dipasquale.search.mcts.heuristic.selection.RewardHeuristicController;
import com.dipasquale.search.mcts.heuristic.selection.RewardHeuristicPermissionType;
import com.dipasquale.search.mcts.heuristic.selection.RosinCPuctAlgorithm;
import com.dipasquale.search.mcts.propagation.BackPropagationType;
import com.dipasquale.search.mcts.seek.MaximumComprehensiveSeekPolicy;
import com.dipasquale.simulation.game2048.heuristic.AverageValuedTileRewardHeuristic;
import com.dipasquale.simulation.game2048.heuristic.FreeTileRewardHeuristic;
import com.dipasquale.simulation.game2048.heuristic.GameExplorationHeuristic;
import com.dipasquale.simulation.game2048.heuristic.MonotonicityRewardHeuristic;
import com.dipasquale.simulation.game2048.heuristic.TwinValuedTileRewardHeuristic;
import com.dipasquale.simulation.game2048.heuristic.UniformityRewardHeuristic;
import com.dipasquale.simulation.game2048.heuristic.WeightedBoardRewardHeuristic;
import com.dipasquale.simulation.game2048.heuristic.WeightedBoardType;
import com.dipasquale.simulation.game2048.player.ActionIdModelPlayer;
import com.dipasquale.simulation.game2048.player.GameStandardIOClient;
import com.dipasquale.simulation.game2048.player.ListActionIdModel;
import com.dipasquale.simulation.game2048.player.MctsPlayer;
import com.dipasquale.simulation.game2048.player.StandardIOValuedTileAllocationPlayer;
import com.dipasquale.simulation.game2048.player.ValuedTileAllocationPlayer;
import com.dipasquale.synchronization.event.loop.ParallelEventLoop;
import com.dipasquale.synchronization.event.loop.ParallelEventLoopSettings;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public final class GameTest {
    private static final boolean VISUALIZE_TEST_1 = false;
    private static final int VICTORIOUS_EXPONENTIAL_VALUE = 11;
    private static final WeightedBoardType WEIGHTED_BOARD_TYPE = WeightedBoardType.SNAKE_SHAPE;
    private static final double C_PUCT_ROSIN_BASE = 196D;
    private static final double C_PUCT_ROSIN_INIT = 2.5D;
    private static final float C_PUCT_CONSTANT = 1f;
    private static final boolean SHOULD_ECHO_FINAL_STATE = true;
    private static final BufferType BUFFER_TYPE = BufferType.DISABLED;
    private static final TestOption TEST_OPTION = TestOption.BACKGROUND_ONLY;
    private static final int CONCURRENCY_LEVEL = 2;
    private static final List<Throwable> UNHANDLED_EXCEPTIONS = Collections.synchronizedList(new ArrayList<>());

    private static final ParallelEventLoop EVENT_LOOP = ParallelEventLoop.builder()
            .settings(ParallelEventLoopSettings.builder()
                    .concurrencyLevel(CONCURRENCY_LEVEL)
                    .errorHandler(UNHANDLED_EXCEPTIONS::add)
                    .dateTimeSupport(new MillisecondsDateTimeSupport())
                    .build())
            .build();

    private static final int MAXIMUM_SIMULATION_DEPTH = 16;

    @BeforeAll
    public static void beforeAll() {
        JvmWarmup.start(100_000);
    }

    @AfterAll
    public static void afterAll() {
        EVENT_LOOP.shutdown();
    }

    @BeforeEach
    public void beforeEach() {
        UNHANDLED_EXCEPTIONS.clear();
        EVENT_LOOP.clear();
    }

    @Test
    public void TEST_1() {
        List<Integer> actionIds = List.of(
                0, 3, 2, 1, 0, 3, 1, 2, 3,
                0, 1, 3, 0, 0, 3, 2, 0, 1,
                0, 2, 3, 0, 3, 0, 0, 1, 0,
                1, 0, 0, 1, 2, 3, 1, 0, 2,
                3, 0, 1
        );

        int victoryValue = 6;
        DeterministicRandomSupport tileIdRandomSupport = DeterministicRandomSupport.create(16L);
        DeterministicRandomSupport exponentialValueRandomSupport = DeterministicRandomSupport.create(10L);
        ValuedTileSupport valuedTileSupport = new ValuedTileSupport(tileIdRandomSupport, exponentialValueRandomSupport);

        Consumer<SearchResult<GameAction, GameState>> inspector = VISUALIZE_TEST_1
                ? searchResult -> searchResult.getState().print(System.out)
                : null;

        Game game = Game.create(victoryValue, new ValuedTileAllocationPlayer(valuedTileSupport), inspector);
        ActionIdModelPlayer player = new ActionIdModelPlayer(new ListActionIdModel(actionIds));
        GameResult result = game.play(player);

        Assertions.assertTrue(result.isSuccessful());
        Assertions.assertEquals(340, result.getScore());
        Assertions.assertEquals(actionIds.size(), result.getMoveCount());
    }

    private static GameResult playGame(final Player valuedTileAdderPlayer, final int maximumSelectionCount, final ConcurrencySettings concurrencySettings) {
        Game game = Game.create(VICTORIOUS_EXPONENTIAL_VALUE, valuedTileAdderPlayer);

        MctsPlayer player = MctsPlayer.builder()
                .mcts(HeuristicMonteCarloTreeSearch.<GameAction, GameState>builder()
                        .comprehensiveSeekPolicy(MaximumComprehensiveSeekPolicy.builder()
                                .maximumSelectionCount(maximumSelectionCount)
                                .maximumSimulationDepth(MAXIMUM_SIMULATION_DEPTH)
                                .build())
                        .bufferType(BUFFER_TYPE)
                        .rewardHeuristic(RewardHeuristicController.<GameAction, GameState>builder()
                                .permissionTypes(HeuristicPermissionType.INTENTIONAL_ONLY.reference)
                                .addHeuristic(HeuristicType.WEIGHTED_BOARD.reference, 1f)
                                .addHeuristic(HeuristicType.FREE_TILE.reference, 1f)
                                .addHeuristic(HeuristicType.MONOTONICITY.reference, 1.999f)
                                .addHeuristic(HeuristicType.TWIN_VALUED_TILE.reference, 0.0001f)
                                .addHeuristic(HeuristicType.UNIFORMITY.reference, 0.0008f)
                                .addHeuristic(HeuristicType.AVERAGE_VALUED_TILE.reference, 0.0001f)
                                .build())
                        .explorationHeuristic(GameExplorationHeuristic.getInstance())
                        .cpuctAlgorithm(CPuctAlgorithmType.ROSIN.reference)
                        .backPropagationType(BackPropagationType.REVERSED_ON_OPPONENT)
                        .concurrencySettings(concurrencySettings)
                        .build())
                .debug(SHOULD_ECHO_FINAL_STATE)
                .build();

        return game.play(player);
    }

    private static GameResult playGame(final int maximumSelectionCount, final ConcurrencySettings concurrencySettings) {
        UniformRandomSupport tileIdRandomSupport = new UniformRandomSupport();
        UniformRandomSupport exponentialValueRandomSupport = new UniformRandomSupport();
        ValuedTileSupport valuedTileSupport = new ValuedTileSupport(tileIdRandomSupport, exponentialValueRandomSupport);
        Player valuedTileAdderPlayer = new ValuedTileAllocationPlayer(valuedTileSupport);

        return playGame(valuedTileAdderPlayer, maximumSelectionCount, concurrencySettings);
    }

    private static void assertGameResult(final GameResult result, final int expectedHighestValue) {
        System.out.printf("score: %d, move count: %d%n", result.getScore(), result.getMoveCount());
        Assertions.assertTrue(result.getHighestValue() >= expectedHighestValue);
    }

    private static void assertGamePlay(final int maximumSelectionCount, final ConcurrencySettings concurrencySettings, final int expectedHighestExponentialValue) {
        GameResult result = playGame(maximumSelectionCount, concurrencySettings);

        assertGameResult(result, expectedHighestExponentialValue);
    }

    @Test
    public void TEST_2() {
        if (!TEST_OPTION.testTypes.contains(TestType.BACKGROUND)) {
            return;
        }

        assertGamePlay(200, null, VICTORIOUS_EXPONENTIAL_VALUE - 2);
    }

    private static String getDirectoryName() {
        String currentDirectoryName = System.getProperty("user.dir");

        return Path.of(currentDirectoryName, "tst/com/dipasquale/simulation/game2048/python").toString();
    }

    private static GameStandardIOClient createClient() {
        StandardIOClient client = StandardIOClient.builder()
                .directoryName(getDirectoryName())
                .processFileName("python")
                .arguments(List.of("main.py"))
                .build();

        return new GameStandardIOClient(client);
    }

    @Test
    public void TEST_3() {
        if (!TEST_OPTION.testTypes.contains(TestType.DISPLAYED_ON_BROWSER)) {
            return;
        }

        StandardIOValuedTileAllocationPlayer valuedTileAdderPlayer = new StandardIOValuedTileAllocationPlayer(GameTest::createClient);
        GameResult result = playGame(valuedTileAdderPlayer, 400, null);

        assertGameResult(result, VICTORIOUS_EXPONENTIAL_VALUE);
    }

    /*
     * NOTE:
     *
     * (a) sample runs for 400 selections per move, 8+ simulated depth from selected node:
     *       1-thread: 0m24s
     *       15-thread(shared-lock): 0m43s
     *       15-thread(rcu-lock): 0m58s
     *
     * (b) sample runs for 800 selections per move, 8+ simulated depth from selected node
     *       1-thread: 1m1s
     *       15-thread(shared-lock): 1m30s
     *       15-thread(rcu-lock): 2m3s
     *
     * (c) sample runs for 1,600 selections per move, 8+ simulated depth from selected node
     *       1-thread: 1m41s
     *       15-thread(shared-lock): 3m10s
     *       15-thread(rcu-lock): 5m22s
     */
    @Test
    @Timeout(value = 270_000, unit = TimeUnit.MILLISECONDS)
    public void TEST_4() {
        if (!TEST_OPTION.testTypes.contains(TestType.BACKGROUND)) {
            return;
        }

        ConcurrencySettings concurrencySettings = ConcurrencySettings.builder()
                .eventLoop(EVENT_LOOP)
                .edgeTraversalLockType(EdgeTraversalLockType.SHARED)
                .build();

        assertGamePlay(250, concurrencySettings, VICTORIOUS_EXPONENTIAL_VALUE - 2);
    }

    @Test
    @Timeout(value = 470_000, unit = TimeUnit.MILLISECONDS)
    public void TEST_5() {
        if (!TEST_OPTION.testTypes.contains(TestType.BACKGROUND)) {
            return;
        }

        ConcurrencySettings concurrencySettings = ConcurrencySettings.builder()
                .eventLoop(EVENT_LOOP)
                .edgeTraversalLockType(EdgeTraversalLockType.RCU)
                .build();

        assertGamePlay(250, concurrencySettings, VICTORIOUS_EXPONENTIAL_VALUE - 2);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private enum HeuristicPermissionType {
        INTENTIONAL_ONLY(EnumSet.of(RewardHeuristicPermissionType.ALLOWED_ON_INTENTIONAL_STATES)),
        UNINTENTIONAL_ONLY(EnumSet.of(RewardHeuristicPermissionType.ALLOWED_ON_UNINTENTIONAL_STATES)),
        BOTH(EnumSet.of(RewardHeuristicPermissionType.ALLOWED_ON_INTENTIONAL_STATES, RewardHeuristicPermissionType.ALLOWED_ON_UNINTENTIONAL_STATES));

        private final EnumSet<RewardHeuristicPermissionType> reference;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private enum HeuristicType {
        WEIGHTED_BOARD(new WeightedBoardRewardHeuristic(WEIGHTED_BOARD_TYPE)),
        FREE_TILE(FreeTileRewardHeuristic.getInstance()),
        MONOTONICITY(MonotonicityRewardHeuristic.getInstance()),
        TWIN_VALUED_TILE(TwinValuedTileRewardHeuristic.getInstance()),
        UNIFORMITY(UniformityRewardHeuristic.getInstance()),
        AVERAGE_VALUED_TILE(AverageValuedTileRewardHeuristic.getInstance());

        private final RewardHeuristic<GameAction, GameState> reference;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private enum CPuctAlgorithmType {
        CONSTANT((simulations, visited) -> C_PUCT_CONSTANT),
        ROSIN(new RosinCPuctAlgorithm(C_PUCT_ROSIN_BASE, C_PUCT_ROSIN_INIT));

        private final CPuctAlgorithm reference;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private enum TestType {
        BACKGROUND,
        DISPLAYED_ON_BROWSER
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private enum TestOption {
        BACKGROUND_ONLY(EnumSet.of(TestType.BACKGROUND)),
        DISPLAYED_ON_BROWSER_ONLY(EnumSet.of(TestType.DISPLAYED_ON_BROWSER)),
        ALL(EnumSet.of(TestType.BACKGROUND, TestType.DISPLAYED_ON_BROWSER));
        private final EnumSet<TestType> testTypes;
    }
}
