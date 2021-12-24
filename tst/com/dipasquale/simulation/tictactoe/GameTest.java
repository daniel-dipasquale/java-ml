package com.dipasquale.simulation.tictactoe;

import com.dipasquale.search.mcts.DefaultStrategyCalculator;
import com.dipasquale.search.mcts.DeterministicSimulationPolicy;
import com.dipasquale.search.mcts.MonteCarloTreeSearch;
import com.dipasquale.search.mcts.SimulationRolloutType;
import com.dipasquale.search.mcts.UctSelectionPolicy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class GameTest {
    @Test
    public void TEST_1() {
        MonteCarloTreeSearch<GameState> mcts = MonteCarloTreeSearch.<GameState>builder()
                .simulationPolicy(DeterministicSimulationPolicy.builder()
                        .maximumSimulation(1_600)
                        .maximumDepth(8)
                        .abortionRate(0.5f)
                        .build())
                .selectionPolicy(new UctSelectionPolicy<>())
                .simulationRolloutType(SimulationRolloutType.STOCHASTIC_CHOICE_DETERMINISTIC_OUTCOME)
                .strategyCalculator(DefaultStrategyCalculator.<GameState>builder()
                        .winningFactor(2f)
                        .notLosingFactor(0.5f)
                        .build())
                .build();

        MctsPlayer player1 = new MctsPlayer(mcts);
        MctsPlayer player2 = new MctsPlayer(mcts);
        Game test = new Game(player1, player2);
        int result = test.play();

        System.out.printf("game outcome was: %d%n", result);
        Assertions.assertTrue(result >= -1 && result <= 1);
    }
}
