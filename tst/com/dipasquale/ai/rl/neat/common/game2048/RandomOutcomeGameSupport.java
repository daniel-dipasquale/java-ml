package com.dipasquale.ai.rl.neat.common.game2048;

import com.dipasquale.ai.common.NeuralNetworkDecoder;
import com.dipasquale.ai.common.NeuralNetworkEncoder;
import com.dipasquale.ai.rl.neat.common.OnePlayerGameSupport;
import com.dipasquale.ai.rl.neat.phenotype.NeatNeuralNetwork;
import com.dipasquale.common.factory.ObjectFactory;
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
import com.dipasquale.simulation.game2048.Game;
import com.dipasquale.simulation.game2048.GameAction;
import com.dipasquale.simulation.game2048.GameState;
import com.dipasquale.simulation.game2048.Player;
import com.dipasquale.simulation.game2048.RandomOutcomeMctsPlayer;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PACKAGE)
final class RandomOutcomeGameSupport implements OnePlayerGameSupport<Player> {
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
    private final ObjectFactory<Game> gameFactory;

    @Override
    public Player createPlayer(final NeatNeuralNetwork neuralNetwork) {
        return RandomOutcomeMctsPlayer.builder()
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

    public Game createGame() {
        return gameFactory.create();
    }

    @Override
    public boolean play(final Player player) {
        Game game = createGame();

        return game.play(player).isSuccess();
    }
}
