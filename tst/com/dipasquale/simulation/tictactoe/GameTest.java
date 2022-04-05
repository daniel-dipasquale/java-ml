package com.dipasquale.simulation.tictactoe;

import com.dipasquale.search.mcts.BackPropagationObserver;
import com.dipasquale.search.mcts.CacheType;
import com.dipasquale.search.mcts.MonteCarloTreeSearch;
import com.dipasquale.search.mcts.classic.ClassicMonteCarloTreeSearch;
import com.dipasquale.search.mcts.common.ExtendedMaximumSearchPolicy;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.List;

public final class GameTest {
    private static final boolean DEBUG_MODE = ManagementFactory.getRuntimeMXBean().getInputArguments().toString().indexOf("-agentlib:jdwp") > 0;
    private static final boolean MANUAL_TESTING_ENABLED = false;
    private static final boolean HUMAN_PLAYER_SHOULD_BE_FIRST = true;

    private static MonteCarloTreeSearch<GameAction, GameState> createMcts(final int maximumSimulations, final BackPropagationObserver<GameAction, GameState> backPropagationObserver) {
        return ClassicMonteCarloTreeSearch.<GameAction, GameState>builder()
                .searchPolicy(ExtendedMaximumSearchPolicy.builder()
                        .maximumSelections(maximumSimulations)
                        .maximumSimulationRolloutDepth(8)
                        .build())
                .cacheType(CacheType.AUTO_CLEAR)
                .backPropagationObserver(backPropagationObserver)
                .build();
    }

    @Test
    public void TEST_1() {
        GameOutcomeStatisticsObserver gameOutcomeStatisticsObserver = new GameOutcomeStatisticsObserver();
        MonteCarloTreeSearch<GameAction, GameState> mcts = createMcts(255_168, gameOutcomeStatisticsObserver);

        Assertions.assertNotNull(mcts.proposeNextAction(new GameState()));
        Assertions.assertEquals("[[0.67416704f, 0.44931063f, 0.67416704f, 0.44931063f, 0.9536178f, 0.44931063f, 0.67416704f, 0.44931063f, 0.67416704f], [-0.14504941f, -0.45045045f, -0.14504941f, -0.45045045f, 0.22258863f, -0.45045045f, -0.14504941f, -0.45045045f, -0.14504941f], [0.67416704f, 0.44931063f, 0.67416704f, 0.44931063f, 0.9536178f, 0.44931063f, 0.67416704f, 0.44931063f, 0.67416704f], [-0.14504941f, -0.45045045f, -0.14504941f, -0.45045045f, 0.22258863f, -0.45045045f, -0.14504941f, -0.45045045f, -0.14504941f], [0.67416704f, 0.44931063f, 0.67416704f, 0.44931063f, 0.9536178f, 0.44931063f, 0.67416704f, 0.44931063f, 0.67416704f], [-0.13524936f, -0.44041452f, -0.13524936f, -0.44041452f, 0.23178808f, -0.44041452f, -0.13524936f, -0.44041452f, -0.13524936f], [0.7368421f, 0.46153846f, 0.7368421f, 0.46153846f, 1.0486486f, 0.46153846f, 0.7368421f, 0.46153846f, 0.7368421f], [0.2638037f, -0.19565217f, 0.2638037f, -0.19565217f, 0.68085104f, -0.19565217f, 0.2638037f, -0.19565217f, 0.2638037f], [1.6785715f, 1.5f, 1.6785715f, 1.5f, 1.7894737f, 1.5f, 1.6785715f, 1.5f, 1.6785715f]]", Arrays.deepToString(gameOutcomeStatisticsObserver.dataSets));
    }

    @Test
    public void TEST_2() {
        MctsPlayer player1 = new MctsPlayer(createMcts(200, null));
        MctsPlayer player2 = new MctsPlayer(createMcts(1_600, null));

        for (int i = 0; i < 100; i++) {
            GameResult result = Game.play(player1, player2);
            int outcomeId = result.getOutcomeId();

            System.out.printf("game outcome was: %s%n", result);
            Assertions.assertTrue(outcomeId >= -1 && outcomeId <= 1);
        }
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
            ActionIdModelPlayer player1 = new ActionIdModelPlayer(new ListActionIdModel(gameSetup.player1ActionIds));
            ActionIdModelPlayer player2 = new ActionIdModelPlayer(new ListActionIdModel(gameSetup.player2ActionIds));
            GameResult result = Game.play(player1, player2);

            Assertions.assertEquals(gameSetup.outcomeId, result.getOutcomeId(), String.format("actionIds: %s", Arrays.toString(result.getActionIds())));
        }
    }

    @Test
    public void TEST_4() {
        if (!MANUAL_TESTING_ENABLED || !DEBUG_MODE) {
            return;
        }

        ActionIdModelPlayer player1 = new ActionIdModelPlayer(new ListActionIdModel(List.of(0, 1, 2, 3, 4, 5, 7, 8)));
        MctsPlayer player2 = new MctsPlayer(createMcts(1_600, null));

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
            float fixedWon = won;
            float wonWeight = 2f;
            float fixedDrawn = drawn;
            float drawnWeight = 1f;
            float fixedLost = visited - unfinished - won - drawn;
            float lostWeight = -2f;
            float fixedVisited = visited - unfinished;
            float result = (fixedWon * wonWeight + fixedDrawn * drawnWeight + fixedLost * lostWeight) / fixedVisited;

            return String.format("%sf", result);
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class GameOutcomeStatisticsObserver implements BackPropagationObserver<GameAction, GameState> {
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
        public void notify(final int statusId, final Iterable<GameState> states) {
            for (GameState state : states) {
                int sequence = state.getDepth() - 1;

                if (sequence >= 0) {
                    int actionId = state.getLastAction().getId();
                    DataSet dataSet = dataSets[sequence][actionId];

                    if (statusId == state.getParticipantId()) {
                        dataSet.won++;
                    } else if (statusId == MonteCarloTreeSearch.DRAWN_STATUS_ID) {
                        dataSet.drawn++;
                    } else if (statusId == MonteCarloTreeSearch.IN_PROGRESS_STATUS_ID) {
                        dataSet.unfinished++;
                    }

                    dataSet.visited++;
                }
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
