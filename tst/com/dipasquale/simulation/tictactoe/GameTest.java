package com.dipasquale.simulation.tictactoe;

import com.dipasquale.search.mcts.classic.ClassicConfidenceCalculator;
import com.dipasquale.search.mcts.classic.ClassicEdge;
import com.dipasquale.search.mcts.classic.PrevalentProposalStrategy;
import com.dipasquale.search.mcts.core.BackPropagationObserver;
import com.dipasquale.search.mcts.core.ConfidenceCalculator;
import com.dipasquale.search.mcts.core.MaximumSearchPolicy;
import com.dipasquale.search.mcts.core.MonteCarloTreeSearch;
import com.dipasquale.search.mcts.core.SearchNode;
import com.dipasquale.search.mcts.core.SearchNodeCacheSettings;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public final class GameTest {
    private static MonteCarloTreeSearch<GameAction, ClassicEdge, GameState> createMcts(final int maximumSimulations, final ConfidenceCalculator<ClassicEdge> confidenceCalculator, final BackPropagationObserver<GameAction, ClassicEdge, GameState> backPropagationObserver) {
        return MonteCarloTreeSearch.<GameAction, GameState>classicBuilder()
                .searchPolicy(MaximumSearchPolicy.builder()
                        .maximumSimulations(maximumSimulations)
                        .maximumDepth(9)
                        .build())
                .searchNodeCacheSettings(SearchNodeCacheSettings.builder()
                        .participants(2)
                        .build())
                .confidenceCalculator(confidenceCalculator)
                .backPropagationObserver(backPropagationObserver)
                .proposalStrategy(PrevalentProposalStrategy.builder()
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
        MonteCarloTreeSearch<GameAction, ClassicEdge, GameState> mcts = createMcts(255_168, null, statisticsCollector);

        Assertions.assertNotNull(mcts.proposeNextAction(new GameState()));
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
    private static final class StatisticsCollector implements BackPropagationObserver<GameAction, ClassicEdge, GameState> {
        private final Entry[][] statistics = createStatistics();

        @Override
        public void notify(final SearchNode<GameAction, ClassicEdge, GameState> leafNode, final int simulationStatusId) {
            int move = leafNode.getState().getActionIds().size() - 1;

            for (SearchNode<GameAction, ClassicEdge, GameState> currentNode = leafNode; move >= 0; currentNode = currentNode.getParent()) {
                GameAction currentAction = currentNode.getAction();
                int actionId = currentAction.getId();
                Entry entry = statistics[move][actionId];

                if (currentAction.getParticipantId() == simulationStatusId) {
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
