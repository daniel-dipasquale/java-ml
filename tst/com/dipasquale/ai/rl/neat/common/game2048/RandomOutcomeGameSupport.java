package com.dipasquale.ai.rl.neat.common.game2048;

import com.dipasquale.ai.common.NeuralNetworkDecoder;
import com.dipasquale.ai.common.NeuralNetworkEncoder;
import com.dipasquale.ai.rl.neat.common.OnePlayerGameSupport;
import com.dipasquale.ai.rl.neat.phenotype.NeatNeuralNetwork;
import com.dipasquale.common.factory.ObjectFactory;
import com.dipasquale.search.mcts.CacheType;
import com.dipasquale.search.mcts.alphazero.AlphaZeroMaximumSearchPolicy;
import com.dipasquale.search.mcts.alphazero.AlphaZeroMonteCarloTreeSearch;
import com.dipasquale.search.mcts.alphazero.AlphaZeroPrediction;
import com.dipasquale.search.mcts.alphazero.BackPropagationType;
import com.dipasquale.search.mcts.alphazero.NeuralNetworkAlphaZeroContext;
import com.dipasquale.search.mcts.alphazero.NeuralNetworkAlphaZeroModel;
import com.dipasquale.search.mcts.alphazero.RootExplorationProbabilityNoiseSettings;
import com.dipasquale.search.mcts.alphazero.TemperatureController;
import com.dipasquale.search.mcts.common.CPuctCalculator;
import com.dipasquale.search.mcts.common.ExplorationHeuristic;
import com.dipasquale.search.mcts.common.RewardHeuristic;
import com.dipasquale.simulation.game2048.Game;
import com.dipasquale.simulation.game2048.GameAction;
import com.dipasquale.simulation.game2048.GameState;
import com.dipasquale.simulation.game2048.MctsPlayer;
import com.dipasquale.simulation.game2048.Player;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PACKAGE)
final class RandomOutcomeGameSupport implements OnePlayerGameSupport<Player> {
    private final int maximumExpansions;
    private final RootExplorationProbabilityNoiseSettings rootExplorationProbabilityNoise;
    private final CacheType cacheType;
    private final NeuralNetworkEncoder<GameState> encoder;
    private final NeuralNetworkDecoder<AlphaZeroPrediction<GameAction, GameState>, NeuralNetworkAlphaZeroContext<GameAction, GameState>> decoder;
    private final RewardHeuristic<GameAction, GameState> rewardHeuristic;
    private final ExplorationHeuristic<GameAction> explorationHeuristic;
    private final CPuctCalculator cpuctCalculator;
    private final BackPropagationType backPropagationType;
    private final int temperatureDepthThreshold;
    private final ObjectFactory<Game> gameFactory;

    @Override
    public Player createPlayer(final NeatNeuralNetwork neuralNetwork) {
        return MctsPlayer.builder()
                .mcts(AlphaZeroMonteCarloTreeSearch.<GameAction, GameState>builder()
                        .searchPolicy(AlphaZeroMaximumSearchPolicy.builder()
                                .maximumExpansions(maximumExpansions)
                                .build())
                        .rootExplorationProbabilityNoise(rootExplorationProbabilityNoise)
                        .cacheType(cacheType)
                        .traversalModel(new NeuralNetworkAlphaZeroModel<>(encoder, decoder, neuralNetwork, rewardHeuristic, explorationHeuristic))
                        .cpuctCalculator(cpuctCalculator)
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

        return game.play(player).isSuccessful();
    }
}
