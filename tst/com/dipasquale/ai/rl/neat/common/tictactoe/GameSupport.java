package com.dipasquale.ai.rl.neat.common.tictactoe;

import com.dipasquale.ai.common.NeuralNetworkEncoder;
import com.dipasquale.ai.rl.neat.common.TwoPlayerGameSupport;
import com.dipasquale.ai.rl.neat.phenotype.NeatNeuralNetwork;
import com.dipasquale.search.mcts.StandardSearchNode;
import com.dipasquale.search.mcts.alphazero.AlphaZeroEdge;
import com.dipasquale.search.mcts.alphazero.AlphaZeroMonteCarloTreeSearch;
import com.dipasquale.search.mcts.alphazero.expansion.RootExplorationProbabilityNoiseSettings;
import com.dipasquale.search.mcts.alphazero.proposal.TemperatureController;
import com.dipasquale.search.mcts.alphazero.seek.AlphaZeroMaximumSeekPolicy;
import com.dipasquale.search.mcts.alphazero.selection.AlphaZeroNeuralNetworkDecoder;
import com.dipasquale.search.mcts.alphazero.selection.StandardNeuralNetworkAlphaZeroModel;
import com.dipasquale.search.mcts.buffer.BufferType;
import com.dipasquale.search.mcts.classic.ClassicMonteCarloTreeSearch;
import com.dipasquale.search.mcts.heuristic.intention.ExplorationHeuristic;
import com.dipasquale.search.mcts.heuristic.selection.CPuctAlgorithm;
import com.dipasquale.search.mcts.heuristic.selection.RewardHeuristic;
import com.dipasquale.search.mcts.propagation.BackPropagationType;
import com.dipasquale.search.mcts.seek.MaximumComprehensiveSeekPolicy;
import com.dipasquale.simulation.tictactoe.Game;
import com.dipasquale.simulation.tictactoe.GameAction;
import com.dipasquale.simulation.tictactoe.GameResult;
import com.dipasquale.simulation.tictactoe.GameState;
import com.dipasquale.simulation.tictactoe.Player;
import com.dipasquale.simulation.tictactoe.player.MctsPlayer;
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
    private final CPuctAlgorithm cpuctAlgorithm;
    private final BackPropagationType backPropagationType;
    private final int temperatureDepthThreshold;
    private final int classicMaximumSelectionCount;
    private final int classicMaximumSimulationDepth;
    private final BufferType classicBufferType;

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

    @Override
    public Player createClassicPlayer() {
        return MctsPlayer.builder()
                .mcts(ClassicMonteCarloTreeSearch.<GameAction, GameState>builder()
                        .comprehensiveSeekPolicy(MaximumComprehensiveSeekPolicy.builder()
                                .maximumSelectionCount(classicMaximumSelectionCount)
                                .maximumSimulationDepth(classicMaximumSimulationDepth)
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
