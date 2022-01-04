package com.dipasquale.simulation.tictactoe;

import com.dipasquale.search.mcts.classic.ClassicPrevalentStrategyCalculator;
import com.dipasquale.search.mcts.classic.ClassicSearchEdge;
import com.dipasquale.search.mcts.classic.ClassicSimulationRolloutType;
import com.dipasquale.search.mcts.core.DeterministicSimulationPolicy;
import com.dipasquale.search.mcts.core.MonteCarloTreeSearch;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class GameTest {
    private static MonteCarloTreeSearch<GameState, ClassicSearchEdge, GameEnvironment> createMcts() {
        return MonteCarloTreeSearch.<GameState, GameEnvironment>classicBuilder()
                .simulationPolicy(DeterministicSimulationPolicy.builder()
                        .maximumSimulation(1_600)
                        .maximumDepth(8)
                        .build())
                .simulationRolloutType(ClassicSimulationRolloutType.STOCHASTIC_CHOICE_DETERMINISTIC_OUTCOME)
                .strategyCalculator(ClassicPrevalentStrategyCalculator.builder()
                        .winningFactor(2f)
                        .notLosingFactor(0.5f)
                        .build())
                .build();
    }

    @Test
    public void TEST_1() {
        MctsPlayer player1 = new MctsPlayer(createMcts());
        MctsPlayer player2 = new MctsPlayer(createMcts());

        for (int i = 0; i < 20; i++) {
            int result = Game.play(player1, player2);

            System.out.printf("game outcome was: %d%n", result);
            Assertions.assertTrue(result >= -1 && result <= 1);
        }
    }
}
