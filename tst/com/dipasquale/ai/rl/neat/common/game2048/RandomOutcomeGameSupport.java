package com.dipasquale.ai.rl.neat.common.game2048;

import com.dipasquale.ai.common.NeuralNetworkEncoder;
import com.dipasquale.ai.rl.neat.common.OnePlayerGameSupport;
import com.dipasquale.ai.rl.neat.phenotype.NeatNeuralNetwork;
import com.dipasquale.common.factory.ObjectFactory;
import com.dipasquale.search.mcts.StandardSearchNode;
import com.dipasquale.search.mcts.alphazero.AlphaZeroEdge;
import com.dipasquale.search.mcts.alphazero.AlphaZeroMonteCarloTreeSearch;
import com.dipasquale.search.mcts.alphazero.expansion.RootExplorationProbabilityNoiseSettings;
import com.dipasquale.search.mcts.alphazero.proposal.TemperatureController;
import com.dipasquale.search.mcts.alphazero.seek.AlphaZeroMaximumSeekPolicy;
import com.dipasquale.search.mcts.alphazero.selection.AlphaZeroNeuralNetworkDecoder;
import com.dipasquale.search.mcts.alphazero.selection.StandardNeuralNetworkAlphaZeroModel;
import com.dipasquale.search.mcts.buffer.BufferType;
import com.dipasquale.search.mcts.heuristic.intention.ExplorationHeuristic;
import com.dipasquale.search.mcts.heuristic.selection.CPuctAlgorithm;
import com.dipasquale.search.mcts.heuristic.selection.RewardHeuristic;
import com.dipasquale.search.mcts.propagation.BackPropagationType;
import com.dipasquale.simulation.game2048.Game;
import com.dipasquale.simulation.game2048.GameAction;
import com.dipasquale.simulation.game2048.GameState;
import com.dipasquale.simulation.game2048.Player;
import com.dipasquale.simulation.game2048.player.MctsPlayer;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PACKAGE)
final class RandomOutcomeGameSupport implements OnePlayerGameSupport<Player> {
    private final int maximumExpansions;
    private final RootExplorationProbabilityNoiseSettings rootExplorationProbabilityNoise;
    private final BufferType bufferType;
    private final NeuralNetworkEncoder<GameState> encoder;
    private final AlphaZeroNeuralNetworkDecoder<GameAction, GameState, StandardSearchNode<GameAction, AlphaZeroEdge, GameState>> decoder;
    private final RewardHeuristic<GameAction, GameState> rewardHeuristic;
    private final ExplorationHeuristic<GameAction> explorationHeuristic;
    private final CPuctAlgorithm cpuctAlgorithm;
    private final BackPropagationType backPropagationType;
    private final int temperatureDepthThreshold;
    private final ObjectFactory<Game> gameFactory;

    @Override
    public Player createPlayer(final NeatNeuralNetwork neuralNetwork) {
        return MctsPlayer.builder()
                .mcts(AlphaZeroMonteCarloTreeSearch.<GameAction, GameState>builder()
                        .seekPolicy(AlphaZeroMaximumSeekPolicy.builder()
                                .maximumExpansions(maximumExpansions)
                                .build())
                        .rootExplorationProbabilityNoise(rootExplorationProbabilityNoise)
                        .bufferType(bufferType)
                        .traversalModel(new StandardNeuralNetworkAlphaZeroModel<>(encoder, decoder, neuralNetwork, rewardHeuristic, explorationHeuristic))
                        .cpuctAlgorithm(cpuctAlgorithm)
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
