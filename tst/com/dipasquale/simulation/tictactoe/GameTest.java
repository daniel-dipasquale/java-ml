package com.dipasquale.simulation.tictactoe;

import com.dipasquale.search.mcts.MonteCarloTreeSearch;
import com.dipasquale.search.mcts.SearchResult;
import com.dipasquale.search.mcts.buffer.BufferType;
import com.dipasquale.search.mcts.classic.ClassicMonteCarloTreeSearch;
import com.dipasquale.search.mcts.propagation.BackPropagationObserver;
import com.dipasquale.search.mcts.seek.MaximumComprehensiveSeekPolicy;
import com.dipasquale.simulation.tictactoe.player.LocationIdModelPlayer;
import com.dipasquale.simulation.tictactoe.player.MctsPlayer;
import com.dipasquale.simulation.tictactoe.player.PredeterminedLocationIdModel;
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

    private static MonteCarloTreeSearch<GameAction, GameState> createMcts(final int maximumSimulationCount, final BackPropagationObserver<GameAction, GameState> backPropagationObserver) {
        return ClassicMonteCarloTreeSearch.<GameAction, GameState>builder()
                .comprehensiveSeekPolicy(MaximumComprehensiveSeekPolicy.builder()
                        .maximumSelectionCount(maximumSimulationCount)
                        .maximumSimulationDepth(8)
                        .build())
                .bufferType(BufferType.DISABLED)
                .backPropagationObserver(backPropagationObserver)
                .build();
    }

    @Test
    public void TEST_1() {
        GameOutcomeStatisticsObserver gameOutcomeStatisticsObserver = new GameOutcomeStatisticsObserver();
        MonteCarloTreeSearch<GameAction, GameState> mcts = createMcts(1_355_168, gameOutcomeStatisticsObserver);

        Assertions.assertNotNull(mcts.proposeFirst(new GameState()));
        Assertions.assertEquals("[[0.9328316f, 0.722366f, 0.9328316f, 0.722366f, 1.1601533f, 0.722366f, 0.9328316f, 0.722366f, 0.9328316f], [-0.47020784f, -0.83029723f, -0.47020784f, -0.83029723f, -0.10338681f, -0.83029723f, -0.47020784f, -0.83029723f, -0.47020784f], [0.9328316f, 0.722366f, 0.9328316f, 0.722366f, 1.1601533f, 0.722366f, 0.9328316f, 0.722366f, 0.9328316f], [-0.47020784f, -0.83029723f, -0.47020784f, -0.83029723f, -0.10338681f, -0.83029723f, -0.47020784f, -0.83029723f, -0.47020784f], [0.9328316f, 0.722366f, 0.9328316f, 0.722366f, 1.1601533f, 0.722366f, 0.9328316f, 0.722366f, 0.9328316f], [-0.46666667f, -0.8269231f, -0.46666667f, -0.8269231f, -0.1f, -0.8269231f, -0.46666667f, -0.8269231f, -0.46666667f], [0.95771426f, 0.7505618f, 0.95771426f, 0.7505618f, 1.1813953f, 0.7505618f, 0.95771426f, 0.7505618f, 0.95771426f], [-0.30379745f, -0.6621622f, -0.30379745f, -0.6621622f, 0.04761905f, -0.6621622f, -0.30379745f, -0.6621622f, -0.30379745f], [1.8f, 1.7285714f, 1.8f, 1.7285714f, 1.8857143f, 1.7285714f, 1.8f, 1.7285714f, 1.8f]]", Arrays.deepToString(gameOutcomeStatisticsObserver.dataSets));
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
                        .player1LocationIds(List.of(0, 1, 2))
                        .player2LocationIds(List.of(3, 4))
                        .outcomeId(0)
                        .build(),
                GameSetup.builder()
                        .player1LocationIds(List.of(0, 6, 8))
                        .player2LocationIds(List.of(1, 4, 7))
                        .outcomeId(1)
                        .build(),
                GameSetup.builder()
                        .player1LocationIds(List.of(0, 4, 8))
                        .player2LocationIds(List.of(1, 2))
                        .outcomeId(0)
                        .build(),
                GameSetup.builder()
                        .player1LocationIds(List.of(0, 1, 3))
                        .player2LocationIds(List.of(2, 4, 6))
                        .outcomeId(1)
                        .build(),
                GameSetup.builder()
                        .player1LocationIds(List.of(0, 2, 5, 6, 7))
                        .player2LocationIds(List.of(1, 3, 4, 8))
                        .outcomeId(-1)
                        .build(),
                GameSetup.builder()
                        .player1LocationIds(List.of(0, 7, 6, 1, 5))
                        .player2LocationIds(List.of(4, 2, 8, 3))
                        .outcomeId(-1)
                        .build()
        );

        for (GameSetup gameSetup : gameSetups) {
            LocationIdModelPlayer player1 = new LocationIdModelPlayer(new PredeterminedLocationIdModel(gameSetup.player1LocationIds));
            LocationIdModelPlayer player2 = new LocationIdModelPlayer(new PredeterminedLocationIdModel(gameSetup.player2LocationIds));
            GameResult result = Game.play(player1, player2);

            Assertions.assertEquals(gameSetup.outcomeId, result.getOutcomeId(), String.format("locationIds: %s", result.getLocationIds()));
        }
    }

    @Test
    public void TEST_4() {
        if (!MANUAL_TESTING_ENABLED || !DEBUG_MODE) {
            return;
        }

        LocationIdModelPlayer player1 = new LocationIdModelPlayer(new PredeterminedLocationIdModel(List.of(0, 1, 2, 3, 4, 5, 7, 8)));
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
            if (visited - unfinished <= 0) {
                return "empty";
            }

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
            int length = GameState.BOARD_VECTOR_SIZE;
            DataSet[][] statistics = new DataSet[length][length];

            for (int i1 = 0; i1 < length; i1++) {
                for (int i2 = 0; i2 < length; i2++) {
                    statistics[i1][i2] = new DataSet();
                }
            }

            return statistics;
        }

        @Override
        public void notify(final int statusId, final Iterable<SearchResult<GameAction, GameState>> results) {
            for (SearchResult<GameAction, GameState> result : results) {
                int sequence = result.getStateId().getDepth() - 1;

                if (sequence >= 0) {
                    int actionId = result.getAction().getLocationId();
                    DataSet dataSet = dataSets[sequence][actionId];

                    if (statusId == result.getState().getParticipantId()) {
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
        private final List<Integer> player1LocationIds;
        private final List<Integer> player2LocationIds;
        private final int outcomeId;
    }
}
