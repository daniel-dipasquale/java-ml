package com.dipasquale.simulation.game2048;

import com.dipasquale.common.random.float1.DeterministicRandomSupport;
import com.dipasquale.common.random.float1.UniformRandomSupport;
import com.dipasquale.search.mcts.CacheType;
import com.dipasquale.search.mcts.alphazero.BackPropagationType;
import com.dipasquale.search.mcts.common.CPuctCalculator;
import com.dipasquale.search.mcts.common.ExtendedMaximumSearchPolicy;
import com.dipasquale.search.mcts.common.RosinCPuctCalculator;
import com.dipasquale.search.mcts.common.ValueHeuristic;
import com.dipasquale.search.mcts.common.ValueHeuristicController;
import com.dipasquale.search.mcts.common.ValueHeuristicPermissionType;
import com.dipasquale.search.mcts.heuristic.HeuristicMonteCarloTreeSearch;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.List;

public final class GameTest {
    private static final int VICTORY_VALUE = 11;
    private static final WeightedBoardType WEIGHTED_BOARD_TYPE = WeightedBoardType.SNAKE_SHAPE;
    private static final float C_PUCT_CONSTANT = 1f;
    private static final boolean DEBUG = true;

    @Test
    public void TEST_1() {
        List<Integer> actionIds = List.of(
                0, 3, 2, 1, 0, 3, 1, 2, 3,
                0, 1, 3, 0, 0, 3, 2, 0, 1,
                0, 0, 1, 2, 2, 1, 3, 0, 3,
                2, 3, 1, 0, 3, 1, 2, 1, 1,
                2, 2, 1
        );

        DeterministicRandomSupport locationRandomSupport = new DeterministicRandomSupport(16);
        DeterministicRandomSupport valueRandomSupport = new DeterministicRandomSupport(10);
        ValuedTileSupport valuedTileSupport = new ValuedTileSupport(locationRandomSupport, valueRandomSupport);
        int victoryValue = 6;
        Game game = new Game(valuedTileSupport, victoryValue, new RandomValuedTileAdderPlayer());
        ActionIdModelPlayer player = new ActionIdModelPlayer(new ListActionIdModel(actionIds));
        GameResult result = game.play(player);

        Assertions.assertTrue(result.isSuccessful());
        Assertions.assertEquals(332, result.getScore());
        Assertions.assertEquals(actionIds.size(), result.getMoveCount());
    }

    @Test
    public void TEST_2() {
        UniformRandomSupport locationRandomSupport = new UniformRandomSupport();
        UniformRandomSupport valueRandomSupport = new UniformRandomSupport();
        ValuedTileSupport valuedTileSupport = new ValuedTileSupport(locationRandomSupport, valueRandomSupport);
        Game game = new Game(valuedTileSupport, VICTORY_VALUE, new RandomValuedTileAdderPlayer());

        MctsPlayer player = MctsPlayer.builder()
                .mcts(HeuristicMonteCarloTreeSearch.<GameAction, GameState>builder()
                        .searchPolicy(ExtendedMaximumSearchPolicy.builder()
                                .maximumSelections(200)
                                .maximumSimulationRolloutDepth(16)
                                .perspectiveParticipantId(2)
                                .build())
                        .cacheType(CacheType.AUTO_CLEAR)
                        .valueHeuristic(ValueHeuristicController.<GameAction, GameState>builder()
                                .permissionTypes(HeuristicPermissionType.INTENTIONAL_ONLY.reference)
                                .addValueHeuristic(HeuristicType.WEIGHTED_BOARD.reference, 1f)
                                .addValueHeuristic(HeuristicType.FREE_TILE.reference, 1f)
                                .addValueHeuristic(HeuristicType.MONOTONICITY.reference, 2f)
                                .addValueHeuristic(HeuristicType.TWIN_VALUED_TILE.reference, 0f)
                                .addValueHeuristic(HeuristicType.UNIFORMITY.reference, 0f)
                                .addValueHeuristic(HeuristicType.AVERAGE_VALUED_TILE.reference, 1f)
                                .build())
                        .explorationProbabilityCalculator(GameExplorationProbabilityCalculator.getInstance())
                        .cpuctCalculator(CPuctCalculatorType.ROSIN.reference)
                        .backPropagationType(BackPropagationType.REVERSED_ON_OPPONENT)
                        .build())
                .debug(DEBUG)
                .build();

        GameResult result = game.play(player);

        System.out.printf("score: %d, move count: %d%n", result.getScore(), result.getMoveCount());
        Assertions.assertTrue(result.isSuccessful());
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private enum HeuristicPermissionType {
        INTENTIONAL_ONLY(EnumSet.of(ValueHeuristicPermissionType.ALLOWED_ON_INTENTIONAL_STATES)),
        UNINTENTIONAL_ONLY(EnumSet.of(ValueHeuristicPermissionType.ALLOWED_ON_UNINTENTIONAL_STATES)),
        BOTH(EnumSet.of(ValueHeuristicPermissionType.ALLOWED_ON_INTENTIONAL_STATES, ValueHeuristicPermissionType.ALLOWED_ON_UNINTENTIONAL_STATES));

        private final EnumSet<ValueHeuristicPermissionType> reference;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private enum HeuristicType {
        WEIGHTED_BOARD(new WeightedBoardValueHeuristic(WEIGHTED_BOARD_TYPE)),
        FREE_TILE(FreeTileValueHeuristic.getInstance()),
        MONOTONICITY(MonotonicityValueHeuristic.getInstance()),
        TWIN_VALUED_TILE(TwinValuedTileValueHeuristic.getInstance()),
        UNIFORMITY(UniformityValueHeuristic.getInstance()),
        AVERAGE_VALUED_TILE(AverageValuedTileValueHeuristic.getInstance());

        private final ValueHeuristic<GameAction, GameState> reference;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private enum CPuctCalculatorType {
        CONSTANT((simulations, visited) -> C_PUCT_CONSTANT),
        ROSIN(new RosinCPuctCalculator());

        private final CPuctCalculator reference;
    }
}
