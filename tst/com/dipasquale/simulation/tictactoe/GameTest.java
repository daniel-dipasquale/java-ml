package com.dipasquale.simulation.tictactoe;

import com.dipasquale.search.mcts.classic.ClassicPrevalentStrategyCalculator;
import com.dipasquale.search.mcts.classic.ClassicSearchEdge;
import com.dipasquale.search.mcts.classic.ClassicSimulationRolloutType;
import com.dipasquale.search.mcts.core.DeterministicSimulationPolicy;
import com.dipasquale.search.mcts.core.MonteCarloTreeSearch;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class GameTest {
    @Test
    public void TEST_1() {
        MonteCarloTreeSearch<GameState, ClassicSearchEdge> mcts = MonteCarloTreeSearch.<GameState>classicBuilder()
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

        MctsPlayer player1 = new MctsPlayer(mcts);
        MctsPlayer player2 = new MctsPlayer(mcts);
        Game test = new Game(player1, player2);

        for (int i = 0; i < 20; i++) {
            int result = test.play();

            System.out.printf("game outcome was: %d%n", result);
            Assertions.assertTrue(result >= -1 && result <= 1);
        }
    }
}
