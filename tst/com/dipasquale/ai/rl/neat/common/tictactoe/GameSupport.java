package com.dipasquale.ai.rl.neat.common.tictactoe;

import com.dipasquale.ai.common.NeuralNetworkDecoder;
import com.dipasquale.ai.common.NeuralNetworkEncoder;
import com.dipasquale.ai.rl.neat.common.TwoPlayerGameSupport;
import com.dipasquale.ai.rl.neat.phenotype.NeatNeuralNetwork;
import com.dipasquale.search.mcts.NodeCacheSettings;
import com.dipasquale.search.mcts.alphazero.AlphaZeroMaximumSearchPolicy;
import com.dipasquale.search.mcts.alphazero.AlphaZeroMonteCarloTreeSearch;
import com.dipasquale.search.mcts.alphazero.AlphaZeroPolicyDistributor;
import com.dipasquale.search.mcts.alphazero.AlphaZeroPrediction;
import com.dipasquale.search.mcts.alphazero.AlphaZeroSelectionConfidenceCalculator;
import com.dipasquale.search.mcts.alphazero.AlphaZeroValueCalculator;
import com.dipasquale.search.mcts.alphazero.BackPropagationType;
import com.dipasquale.search.mcts.alphazero.CPuctCalculator;
import com.dipasquale.search.mcts.alphazero.NeuralNetworkAlphaZeroModel;
import com.dipasquale.search.mcts.alphazero.NeuralNetworkAlphaZeroModelContext;
import com.dipasquale.search.mcts.alphazero.RootExplorationProbabilityNoiseSettings;
import com.dipasquale.search.mcts.alphazero.TemperatureController;
import com.dipasquale.search.mcts.classic.ClassicMaximumSearchPolicy;
import com.dipasquale.search.mcts.classic.ClassicMonteCarloTreeSearch;
import com.dipasquale.search.mcts.classic.ClassicSelectionConfidenceCalculator;
import com.dipasquale.search.mcts.classic.PrevalentActionEfficiencyCalculator;
import com.dipasquale.simulation.tictactoe.Game;
import com.dipasquale.simulation.tictactoe.GameAction;
import com.dipasquale.simulation.tictactoe.GameResult;
import com.dipasquale.simulation.tictactoe.GameState;
import com.dipasquale.simulation.tictactoe.MctsPlayer;
import com.dipasquale.simulation.tictactoe.Player;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PACKAGE)
final class GameSupport implements TwoPlayerGameSupport<Player> {
    private final int maximumExpansions;
    private final RootExplorationProbabilityNoiseSettings rootExplorationProbabilityNoise;
    private final NodeCacheSettings nodeCache;
    private final NeuralNetworkEncoder<GameState> encoder;
    private final NeuralNetworkDecoder<AlphaZeroPrediction<GameAction, GameState>, NeuralNetworkAlphaZeroModelContext<GameAction, GameState>> decoder;
    private final AlphaZeroValueCalculator<GameAction, GameState> valueCalculator;
    private final AlphaZeroPolicyDistributor<GameAction, GameState> policyDistributor;
    private final CPuctCalculator cpuctCalculator;
    private final BackPropagationType backPropagationType;
    private final int temperatureDepthThreshold;
    private final int classicMaximumSelections;
    private final int classicMaximumSimulationRolloutDepth;
    private final NodeCacheSettings classicNodeCache;

    @Override
    public Player createPlayer(final NeatNeuralNetwork neuralNetwork) {
        return MctsPlayer.builder()
                .mcts(AlphaZeroMonteCarloTreeSearch.<GameAction, GameState>builder()
                        .searchPolicy(AlphaZeroMaximumSearchPolicy.builder()
                                .maximumExpansions(maximumExpansions)
                                .build())
                        .rootExplorationProbabilityNoise(rootExplorationProbabilityNoise)
                        .nodeCache(nodeCache)
                        .traversalModel(new NeuralNetworkAlphaZeroModel<>(encoder, decoder, neuralNetwork, valueCalculator, policyDistributor))
                        .selectionConfidenceCalculator(new AlphaZeroSelectionConfidenceCalculator(cpuctCalculator))
                        .backPropagationType(backPropagationType)
                        .temperatureController(TemperatureController.builder()
                                .depthThreshold(temperatureDepthThreshold)
                                .build())
                        .build())
                .build();
    }

    @Override
    public Player createClassicPlayer() {
        return MctsPlayer.builder()
                .mcts(ClassicMonteCarloTreeSearch.<GameAction, GameState>builder()
                        .searchPolicy(ClassicMaximumSearchPolicy.builder()
                                .maximumSelections(classicMaximumSelections)
                                .maximumSimulationRolloutDepth(classicMaximumSimulationRolloutDepth)
                                .build())
                        .nodeCache(classicNodeCache)
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
        GameResult result = Game.play(player1, player2);

        return result.getOutcomeId();
    }
}
