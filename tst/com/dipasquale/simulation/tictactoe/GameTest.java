package com.dipasquale.simulation.tictactoe;

import com.dipasquale.search.mcts.classic.ClassicPrevalentStrategyCalculator;
import com.dipasquale.search.mcts.classic.ClassicSearchEdge;
import com.dipasquale.search.mcts.classic.ClassicSimulationRolloutType;
import com.dipasquale.search.mcts.core.BackPropagationObserver;
import com.dipasquale.search.mcts.core.DeterministicSimulationPolicy;
import com.dipasquale.search.mcts.core.MonteCarloTreeSearch;
import com.dipasquale.search.mcts.core.SearchNode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class GameTest {
    private static MonteCarloTreeSearch<GameState, ClassicSearchEdge, GameEnvironment> createMcts(final int maximumSimulations, final BackPropagationObserver<GameState, ClassicSearchEdge, GameEnvironment> backPropagationObserver) {
        return MonteCarloTreeSearch.<GameState, GameEnvironment>classicBuilder()
                .simulationPolicy(DeterministicSimulationPolicy.builder()
                        .maximumSimulations(maximumSimulations)
                        .maximumDepth(8)
                        .build())
                .simulationRolloutType(ClassicSimulationRolloutType.STOCHASTIC_CHOICE_DETERMINISTIC_OUTCOME)
                .backPropagationObserver(backPropagationObserver)
                .strategyCalculator(ClassicPrevalentStrategyCalculator.builder()
                        .winningFactor(2f)
                        .notLosingFactor(0.5f)
                        .build())
                .build();
    }

    @Test
    public void TEST_1() {
        MctsPlayer player1 = new MctsPlayer(createMcts(400, null));
        MctsPlayer player2 = new MctsPlayer(createMcts(1_600, null));

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
        MonteCarloTreeSearch<GameState, ClassicSearchEdge, GameEnvironment> mcts = createMcts(400_000, statisticsCollector);

        Assertions.assertNotNull(mcts.proposeNextState(new GameEnvironment()));
        System.out.println(Arrays.deepToString(statisticsCollector.statistics));
    }

    private static Result[][] createStatistics() {
        int length = 9;
        Result[][] statistics = new Result[length][length];

        for (int i1 = 0; i1 < length; i1++) {
            for (int i2 = 0; i2 < length; i2++) {
                statistics[i1][i2] = new Result();
            }
        }

        return statistics;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class Result {
        private int won = 0;
        private int drawn = 0;
        private int visited = 0;

        @Override
        public String toString() {
            float wonFixed = won;
            float drawnFixed = drawn;
            float visitedFixed = visited;
            float result = (wonFixed * 0.85f + drawnFixed * 0.15f) / visitedFixed;

            return String.format("%sf", result);
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class StatisticsCollector implements BackPropagationObserver<GameState, ClassicSearchEdge, GameEnvironment> {
        private final Result[][] statistics = createStatistics();

        @Override
        public void notify(final SearchNode<GameState, ClassicSearchEdge, GameEnvironment> leafNode, final int simulationStatusId) {
            int move = leafNode.getEnvironment().getMoves().size() - 1;

            for (SearchNode<GameState, ClassicSearchEdge, GameEnvironment> currentNode = leafNode; move >= 0; currentNode = currentNode.getParent()) {
                GameState currentState = currentNode.getState();
                int location = currentState.getLocation();
                Result result = statistics[move][location];

                if (currentState.getParticipantId() == simulationStatusId) {
                    result.won++;
                } else if (simulationStatusId == MonteCarloTreeSearch.DRAWN) {
                    result.drawn++;
                }

                result.visited++;
                move--;
            }
        }
    }
}
