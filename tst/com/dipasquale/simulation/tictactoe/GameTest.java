package com.dipasquale.simulation.tictactoe;

import com.dipasquale.search.mcts.classic.ClassicConfidenceCalculator;
import com.dipasquale.search.mcts.classic.ClassicEdge;
import com.dipasquale.search.mcts.classic.ClassicPrevalentStrategyCalculator;
import com.dipasquale.search.mcts.core.BackPropagationObserver;
import com.dipasquale.search.mcts.core.ConfidenceCalculator;
import com.dipasquale.search.mcts.core.DeterministicSearchPolicy;
import com.dipasquale.search.mcts.core.MonteCarloTreeSearch;
import com.dipasquale.search.mcts.core.SearchNode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public final class GameTest {
    private static MonteCarloTreeSearch<GameState, ClassicEdge, GameEnvironment> createMcts(final int maximumSimulations, final ConfidenceCalculator<ClassicEdge> confidenceCalculator, final BackPropagationObserver<GameState, ClassicEdge, GameEnvironment> backPropagationObserver) {
        return MonteCarloTreeSearch.<GameState, GameEnvironment>classicBuilder()
                .searchPolicy(DeterministicSearchPolicy.builder()
                        .maximumSimulations(maximumSimulations)
                        .maximumDepth(9)
                        .build())
                .confidenceCalculator(confidenceCalculator)
                .backPropagationObserver(backPropagationObserver)
                .strategyCalculator(ClassicPrevalentStrategyCalculator.builder()
                        .winningFactor(2f)
                        .notLosingFactor(0.5f)
                        .build())
                .build();
    }

    @Test
    public void TEST_1() {
        MctsPlayer player1 = new MctsPlayer(createMcts(200, new ClassicConfidenceCalculator(), null));
        MctsPlayer player2 = new MctsPlayer(createMcts(1_600, null, null));

        for (int i = 0; i < 20; i++) {
            GameResult result = Game.play(player1, player2);
            int outcomeId = result.getOutcomeId();

            System.out.printf("game outcome was: %s%n", result);
            Assertions.assertTrue(outcomeId >= -1 && outcomeId <= 1);
        }
    }

    @Test
    public void TEST_2() {
        StatisticsCollector statisticsCollector = new StatisticsCollector();
        MonteCarloTreeSearch<GameState, ClassicEdge, GameEnvironment> mcts = createMcts(255_168, null, statisticsCollector);

        Assertions.assertNotNull(mcts.proposeNextState(new GameEnvironment()));
        System.out.println(Arrays.deepToString(statisticsCollector.statistics));
    }

    private static Entry[][] createStatistics() {
        int length = 9;
        Entry[][] statistics = new Entry[length][length];

        for (int i1 = 0; i1 < length; i1++) {
            for (int i2 = 0; i2 < length; i2++) {
                statistics[i1][i2] = new Entry();
            }
        }

        return statistics;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class Entry {
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
    private static final class StatisticsCollector implements BackPropagationObserver<GameState, ClassicEdge, GameEnvironment> {
        private final Entry[][] statistics = createStatistics();

        @Override
        public void notify(final SearchNode<GameState, ClassicEdge, GameEnvironment> leafNode, final int simulationStatusId) {
            int move = leafNode.getEnvironment().getMoves().size() - 1;

            for (SearchNode<GameState, ClassicEdge, GameEnvironment> currentNode = leafNode; move >= 0; currentNode = currentNode.getParent()) {
                GameState currentState = currentNode.getState();
                int location = currentState.getLocation();
                Entry entry = statistics[move][location];

                if (currentState.getParticipantId() == simulationStatusId) {
                    entry.won++;
                } else if (simulationStatusId == MonteCarloTreeSearch.DRAWN) {
                    entry.drawn++;
                } else if (simulationStatusId == MonteCarloTreeSearch.IN_PROGRESS) {
                    entry.unfinished++;
                }

                entry.visited++;
                move--;
            }
        }
    }
}
