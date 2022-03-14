package com.dipasquale.simulation.tictactoe;

import com.dipasquale.search.mcts.MonteCarloTreeSearch;
import com.dipasquale.search.mcts.NodeCacheSettings;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.SelectionConfidenceCalculator;
import com.dipasquale.search.mcts.SimulationResultObserver;
import com.dipasquale.search.mcts.classic.ClassicEdge;
import com.dipasquale.search.mcts.classic.ClassicMaximumSearchPolicy;
import com.dipasquale.search.mcts.classic.ClassicMonteCarloTreeSearch;
import com.dipasquale.search.mcts.classic.ClassicSelectionConfidenceCalculator;
import com.dipasquale.search.mcts.classic.PrevalentActionEfficiencyCalculator;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public final class GameTest {
    private static final boolean DEBUG_MODE = ManagementFactory.getRuntimeMXBean().getInputArguments().toString().indexOf("-agentlib:jdwp") > 0;
    private static final boolean MANUAL_TESTING_ENABLED = false;
    private static final boolean HUMAN_PLAYER_SHOULD_BE_FIRST = true;

    private static MonteCarloTreeSearch<GameAction, GameState> createMcts(final int maximumSimulations, final SelectionConfidenceCalculator<ClassicEdge> selectionConfidenceCalculator, final SimulationResultObserver<GameAction, ClassicEdge, GameState> simulationResultObserver) {
        return ClassicMonteCarloTreeSearch.<GameAction, GameState>builder()
                .searchPolicy(ClassicMaximumSearchPolicy.builder()
                        .maximumSelections(maximumSimulations)
                        .maximumSimulationRolloutDepth(9)
                        .build())
                .nodeCache(NodeCacheSettings.builder()
                        .participants(2)
                        .build())
                .selectionConfidenceCalculator(selectionConfidenceCalculator)
                .simulationResultObserver(simulationResultObserver)
                .actionEfficiencyCalculator(PrevalentActionEfficiencyCalculator.<GameAction>builder()
                        .winningFactor(2f)
                        .notLosingFactor(0.5f)
                        .build())
                .build();
    }

    @Test
    public void TEST_1() {
        StatisticsObserver statisticsObserver = new StatisticsObserver();
        MonteCarloTreeSearch<GameAction, GameState> mcts = createMcts(255_168, null, statisticsObserver);

        Assertions.assertNotNull(mcts.proposeNextAction(new GameState()));
        Assertions.assertEquals("[[0.24361748f, 0.13706407f, 0.24361748f, 0.13706407f, 0.3877551f, 0.13706407f, 0.24361748f, 0.13706407f, 0.24361748f], [-0.16081564f, -0.3153153f, -0.16081564f, -0.3153153f, 0.012366035f, -0.3153153f, -0.16081564f, -0.3153153f, -0.16081564f], [0.24361748f, 0.13706407f, 0.24361748f, 0.13706407f, 0.3877551f, 0.13706407f, 0.24361748f, 0.13706407f, 0.24361748f], [-0.16081564f, -0.3153153f, -0.16081564f, -0.3153153f, 0.012366035f, -0.3153153f, -0.16081564f, -0.3153153f, -0.16081564f], [0.24361748f, 0.13706407f, 0.24361748f, 0.13706407f, 0.3877551f, 0.13706407f, 0.24361748f, 0.13706407f, 0.24361748f], [-0.15638208f, -0.31088084f, -0.15638208f, -0.31088084f, 0.01655629f, -0.31088084f, -0.15638208f, -0.31088084f, -0.15638208f], [0.2736842f, 0.13846155f, 0.2736842f, 0.13846155f, 0.43783784f, 0.13846155f, 0.2736842f, 0.13846155f, 0.2736842f], [0.024539877f, -0.22463769f, 0.024539877f, -0.22463769f, 0.23404256f, -0.22463769f, 0.024539877f, -0.22463769f, 0.024539877f], [0.6785714f, 0.5f, 0.6785714f, 0.5f, 0.7894737f, 0.5f, 0.6785714f, 0.5f, 0.6785714f]]", Arrays.deepToString(statisticsObserver.dataSets));
    }

    @Test
    public void TEST_2() {
        MctsPlayer player1 = new MctsPlayer(createMcts(50, new ClassicSelectionConfidenceCalculator(), null));
        MctsPlayer player2 = new MctsPlayer(createMcts(1_600, new ClassicSelectionConfidenceCalculator(), null));

        for (int i = 0; i < 20; i++) {
            GameResult result = Game.play(player1, player2);
            int outcomeId = result.getOutcomeId();

            System.out.printf("game outcome was: %s%n", result);
            Assertions.assertTrue(outcomeId >= -1 && outcomeId <= 1);
        }
    }

    private static ActionIdModel createModel(final List<Integer> actionIds) {
        Iterator<Integer> actionIdsIterator = actionIds.iterator();

        return state -> actionIdsIterator.next();
    }

    @Test
    public void TEST_3() {
        List<GameSetup> gameSetups = List.of(
                GameSetup.builder()
                        .player1ActionIds(List.of(0, 1, 2))
                        .player2ActionIds(List.of(3, 4))
                        .outcomeId(0)
                        .build(),
                GameSetup.builder()
                        .player1ActionIds(List.of(0, 6, 8))
                        .player2ActionIds(List.of(1, 4, 7))
                        .outcomeId(1)
                        .build(),
                GameSetup.builder()
                        .player1ActionIds(List.of(0, 4, 8))
                        .player2ActionIds(List.of(1, 2))
                        .outcomeId(0)
                        .build(),
                GameSetup.builder()
                        .player1ActionIds(List.of(0, 1, 3))
                        .player2ActionIds(List.of(2, 4, 6))
                        .outcomeId(1)
                        .build(),
                GameSetup.builder()
                        .player1ActionIds(List.of(0, 2, 5, 6, 7))
                        .player2ActionIds(List.of(1, 3, 4, 8))
                        .outcomeId(-1)
                        .build(),
                GameSetup.builder()
                        .player1ActionIds(List.of(0, 7, 6, 1, 5))
                        .player2ActionIds(List.of(4, 2, 8, 3))
                        .outcomeId(-1)
                        .build()
        );

        for (GameSetup gameSetup : gameSetups) {
            ActionIdModelPlayer player1 = new ActionIdModelPlayer(createModel(gameSetup.player1ActionIds));
            ActionIdModelPlayer player2 = new ActionIdModelPlayer(createModel(gameSetup.player2ActionIds));
            GameResult result = Game.play(player1, player2);

            Assertions.assertEquals(gameSetup.outcomeId, result.getOutcomeId(), String.format("actionIds: %s", Arrays.toString(result.getActionIds())));
        }
    }

    @Test
    public void TEST_4() {
        if (!MANUAL_TESTING_ENABLED || !DEBUG_MODE) {
            return;
        }

        ActionIdModelPlayer player1 = new ActionIdModelPlayer(createModel(List.of(0, 1, 2, 3, 4, 5, 7, 8)));
        MctsPlayer player2 = new MctsPlayer(createMcts(1_600, new ClassicSelectionConfidenceCalculator(), null));

        GameResult result = HUMAN_PLAYER_SHOULD_BE_FIRST
                ? Game.play(player1, player2)
                : Game.play(player2, player1);

        System.out.printf("game outcome was: %s%n", result);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class DataSet {
        private int won = 0;
        private int drawn = 0;
        private int unfinished = 0;
        private int visited = 0;

        @Override
        public String toString() {
            float wonFixed = won;
            float wonWeight = 1f;
            float drawnFixed = drawn;
            float drawnWeight = 0f;
            float lostFixed = visited - won - drawn - unfinished;
            float lostWeight = -1f;
            float visitedFixed = visited - unfinished;
            float result = (wonFixed * wonWeight + drawnFixed * drawnWeight + lostFixed * lostWeight) / visitedFixed;

            return String.format("%sf", result);
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class StatisticsObserver implements SimulationResultObserver<GameAction, ClassicEdge, GameState> {
        private final DataSet[][] dataSets = createStatistics();

        private static DataSet[][] createStatistics() {
            int length = GameState.BOARD_LENGTH;
            DataSet[][] statistics = new DataSet[length][length];

            for (int i1 = 0; i1 < length; i1++) {
                for (int i2 = 0; i2 < length; i2++) {
                    statistics[i1][i2] = new DataSet();
                }
            }

            return statistics;
        }

        @Override
        public void notify(final SearchNode<GameAction, ClassicEdge, GameState> leafNode) {
            GameState leafState = leafNode.getState();
            int statusId = leafState.getStatusId();
            int sequence = leafState.getDepth() - 1;

            for (SearchNode<GameAction, ClassicEdge, GameState> node = leafNode; sequence >= 0; node = node.getParent()) {
                GameAction action = node.getAction();
                DataSet dataSet = dataSets[sequence][action.getId()];

                if (statusId == action.getParticipantId()) {
                    dataSet.won++;
                } else if (statusId == MonteCarloTreeSearch.DRAWN_STATUS_ID) {
                    dataSet.drawn++;
                } else if (statusId == MonteCarloTreeSearch.IN_PROGRESS_STATUS_ID) {
                    dataSet.unfinished++;
                }

                dataSet.visited++;
                sequence--;
            }
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder(access = AccessLevel.PRIVATE)
    private static final class GameSetup {
        private final List<Integer> player1ActionIds;
        private final List<Integer> player2ActionIds;
        private final int outcomeId;
    }
}
