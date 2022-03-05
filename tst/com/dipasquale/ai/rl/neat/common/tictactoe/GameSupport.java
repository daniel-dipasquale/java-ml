package com.dipasquale.ai.rl.neat.common.tictactoe;

import com.dipasquale.ai.common.NeuralNetworkDecoder;
import com.dipasquale.ai.common.NeuralNetworkEncoder;
import com.dipasquale.ai.rl.neat.common.TwoPlayerGameSupport;
import com.dipasquale.ai.rl.neat.phenotype.NeatNeuralNetwork;
import com.dipasquale.search.mcts.MaximumSearchPolicy;
import com.dipasquale.search.mcts.MonteCarloTreeSearch;
import com.dipasquale.search.mcts.SearchNodeCacheSettings;
import com.dipasquale.search.mcts.alphazero.AlphaZeroPolicyDistributor;
import com.dipasquale.search.mcts.alphazero.AlphaZeroPrediction;
import com.dipasquale.search.mcts.alphazero.AlphaZeroValueCalculator;
import com.dipasquale.search.mcts.alphazero.MostVisitedActionEfficiencyCalculator;
import com.dipasquale.search.mcts.alphazero.NeuralNetworkAlphaZeroHeuristic;
import com.dipasquale.search.mcts.alphazero.NeuralNetworkAlphaZeroHeuristicContext;
import com.dipasquale.search.mcts.alphazero.SearchNodeProviderSettings;
import com.dipasquale.search.mcts.alphazero.TemperatureController;
import com.dipasquale.search.mcts.classic.ClassicSelectionConfidenceCalculator;
import com.dipasquale.search.mcts.classic.PrevalentActionEfficiencyCalculator;
import com.dipasquale.simulation.tictactoe.Game;
import com.dipasquale.simulation.tictactoe.GameAction;
import com.dipasquale.simulation.tictactoe.GameState;
import com.dipasquale.simulation.tictactoe.MctsPlayer;
import com.dipasquale.simulation.tictactoe.Player;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PACKAGE)
final class GameSupport implements TwoPlayerGameSupport<Player> {
    private final int trainingMatchMaximumSimulations;
    private final int trainingMatchMaximumDepth;
    private final boolean trainingAllowRootExplorationNoise;
    private final NeuralNetworkEncoder<GameState> encoder;
    private final NeuralNetworkDecoder<AlphaZeroPrediction<GameAction, GameState>, NeuralNetworkAlphaZeroHeuristicContext<GameAction, GameState>> decoder;
    private final AlphaZeroValueCalculator<GameAction, GameState> valueCalculator;
    private final AlphaZeroPolicyDistributor<GameAction, GameState> policyDistributor;
    private final float trainingInitialTemperature;
    private final float trainingFinalTemperature;
    private final int trainingTemperatureDepthThreshold;
    private final int validationMatchMaximumSimulations;
    private final int validationMatchMaximumDepth;

    @Override
    public Player createPlayer(final NeatNeuralNetwork neuralNetwork) {
        return MctsPlayer.builder()
                .mcts(MonteCarloTreeSearch.<GameAction, GameState>alphaZeroBuilder()
                        .searchPolicy(MaximumSearchPolicy.builder()
                                .maximumSimulations(trainingMatchMaximumSimulations)
                                .maximumDepth(trainingMatchMaximumDepth)
                                .build())
                        .nodeProviderSettings(SearchNodeProviderSettings.builder()
                                .allowRootExplorationNoise(trainingAllowRootExplorationNoise)
                                .build())
                        .traversalHeuristic(new NeuralNetworkAlphaZeroHeuristic<>(encoder, decoder, neuralNetwork, valueCalculator, policyDistributor))
                        .actionEfficiencyCalculator(MostVisitedActionEfficiencyCalculator.<GameAction>builder()
                                .temperatureController(TemperatureController.builder()
                                        .initialValue(trainingInitialTemperature)
                                        .finalValue(trainingFinalTemperature)
                                        .depthThreshold(trainingTemperatureDepthThreshold)
                                        .build())
                                .build())
                        .build())
                .build();
    }

    @Override
    public Player createBasicPlayer() {
        return MctsPlayer.builder()
                .mcts(MonteCarloTreeSearch.<GameAction, GameState>classicBuilder()
                        .searchPolicy(MaximumSearchPolicy.builder()
                                .maximumSimulations(validationMatchMaximumSimulations)
                                .maximumDepth(validationMatchMaximumDepth)
                                .build())
                        .nodeCacheSettings(SearchNodeCacheSettings.builder()
                                .participants(2)
                                .build())
                        .selectionConfidenceCalculator(new ClassicSelectionConfidenceCalculator())
                        .actionEfficiencyCalculator(PrevalentActionEfficiencyCalculator.<GameAction>builder()
                                .winningFactor(2f)
                                .notLosingFactor(0.5f)
                                .build())
                        .build())
                .build();
    }

    @Override
    public int play(final Player player1, final Player player2) {
        return Game.play(player1, player2).getOutcomeId();
    }
}
