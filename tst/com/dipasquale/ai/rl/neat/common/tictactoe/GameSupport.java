package com.dipasquale.ai.rl.neat.common.tictactoe;

import com.dipasquale.ai.common.NeuralNetworkEncoder;
import com.dipasquale.ai.rl.neat.common.TwoPlayerGameSupport;
import com.dipasquale.ai.rl.neat.phenotype.NeatNeuralNetwork;
import com.dipasquale.search.mcts.BufferType;
import com.dipasquale.search.mcts.StandardSearchNode;
import com.dipasquale.search.mcts.alphazero.AlphaZeroEdge;
import com.dipasquale.search.mcts.alphazero.AlphaZeroMaximumSearchPolicy;
import com.dipasquale.search.mcts.alphazero.AlphaZeroMonteCarloTreeSearch;
import com.dipasquale.search.mcts.alphazero.AlphaZeroNeuralNetworkDecoder;
import com.dipasquale.search.mcts.alphazero.BackPropagationType;
import com.dipasquale.search.mcts.alphazero.RootExplorationProbabilityNoiseSettings;
import com.dipasquale.search.mcts.alphazero.StandardNeuralNetworkAlphaZeroModel;
import com.dipasquale.search.mcts.alphazero.TemperatureController;
import com.dipasquale.search.mcts.classic.ClassicMonteCarloTreeSearch;
import com.dipasquale.search.mcts.common.CPuctCalculator;
import com.dipasquale.search.mcts.common.ExplorationHeuristic;
import com.dipasquale.search.mcts.common.MaximumFullSearchPolicy;
import com.dipasquale.search.mcts.common.RewardHeuristic;
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
    private final BufferType bufferType;
    private final NeuralNetworkEncoder<GameState> encoder;
    private final AlphaZeroNeuralNetworkDecoder<GameAction, GameState, StandardSearchNode<GameAction, AlphaZeroEdge, GameState>> decoder;
    private final RewardHeuristic<GameAction, GameState> rewardHeuristic;
    private final ExplorationHeuristic<GameAction> explorationHeuristic;
    private final CPuctCalculator cpuctCalculator;
    private final BackPropagationType backPropagationType;
    private final int temperatureDepthThreshold;
    private final int classicMaximumSelectionCount;
    private final int classicMaximumSimulationRolloutDepth;
    private final BufferType classicBufferType;

    @Override
    public Player createPlayer(final NeatNeuralNetwork neuralNetwork) {
        return MctsPlayer.builder()
                .mcts(AlphaZeroMonteCarloTreeSearch.<GameAction, GameState>builder()
                        .searchPolicy(AlphaZeroMaximumSearchPolicy.builder()
                                .maximumExpansions(maximumExpansions)
                                .build())
                        .rootExplorationProbabilityNoise(rootExplorationProbabilityNoise)
                        .bufferType(bufferType)
                        .traversalModel(new StandardNeuralNetworkAlphaZeroModel<>(encoder, decoder, neuralNetwork, rewardHeuristic, explorationHeuristic))
                        .cpuctCalculator(cpuctCalculator)
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
                        .searchPolicy(MaximumFullSearchPolicy.builder()
                                .maximumSelectionCount(classicMaximumSelectionCount)
                                .maximumSimulationRolloutDepth(classicMaximumSimulationRolloutDepth)
                                .build())
                        .bufferType(classicBufferType)
                        .build())
                .build();
    }

    @Override
    public int play(final Player player1, final Player player2) {
        GameResult result = Game.play(player1, player2);

        return result.getOutcomeId();
    }
}
