package com.dipasquale.simulation.game2048;

import com.dipasquale.common.random.float1.DeterministicRandomSupport;
import com.dipasquale.common.random.float1.UniformRandomSupport;
import com.dipasquale.search.mcts.CacheType;
import com.dipasquale.search.mcts.alphazero.BackPropagationType;
import com.dipasquale.search.mcts.common.CPuctCalculator;
import com.dipasquale.search.mcts.common.ExtendedMaximumSearchPolicy;
import com.dipasquale.search.mcts.common.RewardHeuristic;
import com.dipasquale.search.mcts.common.RewardHeuristicController;
import com.dipasquale.search.mcts.common.RewardHeuristicPermissionType;
import com.dipasquale.search.mcts.common.RosinCPuctCalculator;
import com.dipasquale.search.mcts.heuristic.HeuristicMonteCarloTreeSearch;
import com.dipasquale.simulation.game2048.heuristic.AverageValuedTileRewardHeuristic;
import com.dipasquale.simulation.game2048.heuristic.FreeTileRewardHeuristic;
import com.dipasquale.simulation.game2048.heuristic.GameExplorationHeuristic;
import com.dipasquale.simulation.game2048.heuristic.MonotonicityRewardHeuristic;
import com.dipasquale.simulation.game2048.heuristic.TwinValuedTileRewardHeuristic;
import com.dipasquale.simulation.game2048.heuristic.UniformityRewardHeuristic;
import com.dipasquale.simulation.game2048.heuristic.WeightedBoardRewardHeuristic;
import com.dipasquale.simulation.game2048.heuristic.WeightedBoardType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.List;

public final class GameTest {
    private static final int VICTORY_VALUE = 11;
    private static final WeightedBoardType WEIGHTED_BOARD_TYPE = WeightedBoardType.SNAKE_SHAPE;
    private static final double C_PUCT_ROSIN_BASE = 196D;
    private static final double C_PUCT_ROSIN_INIT = 2.5D;
    private static final float C_PUCT_CONSTANT = 1f;
    private static final boolean PRINT_FINAL_STATE = true;
    private static final TestOption TEST_OPTION = TestOption.DISPLAYED_ON_BROWSER_ONLY;

    @Test
    public void TEST_1() {
        List<Integer> actionIds = List.of(
                0, 3, 2, 1, 0, 3, 1, 2, 3,
                0, 1, 3, 0, 0, 3, 2, 0, 1,
                0, 0, 1, 2, 2, 1, 3, 0, 3,
                2, 3, 1, 0, 3, 1, 2, 1, 1,
                2, 2, 1
        );

        int victoryValue = 6;
        DeterministicRandomSupport locationRandomSupport = new DeterministicRandomSupport(16);
        DeterministicRandomSupport valueRandomSupport = new DeterministicRandomSupport(10);
        ValuedTileSupport valuedTileSupport = new ValuedTileSupport(locationRandomSupport, valueRandomSupport);
        Game game = new Game(victoryValue, new ValuedTileAdderPlayer(valuedTileSupport));
        ActionIdModelPlayer player = new ActionIdModelPlayer(new ListActionIdModel(actionIds));
        GameResult result = game.play(player);

        Assertions.assertTrue(result.isSuccessful());
        Assertions.assertEquals(332, result.getScore());
        Assertions.assertEquals(actionIds.size(), result.getMoveCount());
    }

    private static GameResult playGame(final Player valuedTileAdderPlayer) {
        Game game = new Game(VICTORY_VALUE, valuedTileAdderPlayer);

        MctsPlayer player = MctsPlayer.builder()
                .mcts(HeuristicMonteCarloTreeSearch.<GameAction, GameState>builder()
                        .searchPolicy(ExtendedMaximumSearchPolicy.builder()
                                .maximumSelections(200)
                                .maximumSimulationRolloutDepth(16)
                                .build())
                        .cacheType(CacheType.AUTO_CLEAR)
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
                        .cpuctCalculator(CPuctCalculatorType.ROSIN.reference)
                        .backPropagationType(BackPropagationType.REVERSED_ON_OPPONENT)
                        .build())
                .debug(PRINT_FINAL_STATE)
                .build();

        return game.play(player);
    }

    @Test
    public void TEST_2() {
        if (!TEST_OPTION.reference.contains(TestType.INVISIBLE)) {
            return;
        }

        UniformRandomSupport locationRandomSupport = new UniformRandomSupport();
        UniformRandomSupport valueRandomSupport = new UniformRandomSupport();
        ValuedTileSupport valuedTileSupport = new ValuedTileSupport(locationRandomSupport, valueRandomSupport);
        Player valuedTileAdderPlayer = new ValuedTileAdderPlayer(valuedTileSupport);
        GameResult result = playGame(valuedTileAdderPlayer);

        System.out.printf("score: %d, move count: %d%n", result.getScore(), result.getMoveCount());
        Assertions.assertTrue(result.getHighestValue() >= VICTORY_VALUE - 1);
    }

    @Test
    public void TEST_3() {
        if (!TEST_OPTION.reference.contains(TestType.DISPLAYED_ON_BROWSER)) {
            return;
        }

        StandardIOValuedTileAdderPlayer valuedTileAdderPlayer = new StandardIOValuedTileAdderPlayer();
        GameResult result = playGame(valuedTileAdderPlayer);

        System.out.printf("score: %d, move count: %d%n", result.getScore(), result.getMoveCount());
        Assertions.assertTrue(result.getHighestValue() >= VICTORY_VALUE - 1);
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
    private enum CPuctCalculatorType {
        CONSTANT((simulations, visited) -> C_PUCT_CONSTANT),
        ROSIN(new RosinCPuctCalculator(C_PUCT_ROSIN_BASE, C_PUCT_ROSIN_INIT));

        private final CPuctCalculator reference;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private enum TestType {
        INVISIBLE,
        DISPLAYED_ON_BROWSER
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private enum TestOption {
        INVISIBLE_ONLY(EnumSet.of(TestType.INVISIBLE)),
        DISPLAYED_ON_BROWSER_ONLY(EnumSet.of(TestType.DISPLAYED_ON_BROWSER)),
        ALL(EnumSet.of(TestType.INVISIBLE, TestType.DISPLAYED_ON_BROWSER));
        private final EnumSet<TestType> reference;
    }
}
