package com.dipasquale.ai.rl.neat.common.tictactoe;

import com.dipasquale.ai.common.NeuralNetworkDecoder;
import com.dipasquale.ai.common.NeuralNetworkEncoder;
import com.dipasquale.ai.rl.neat.common.TwoPlayerGameSupport;
import com.dipasquale.ai.rl.neat.phenotype.NeatNeuralNetwork;
import com.dipasquale.search.mcts.CacheType;
import com.dipasquale.search.mcts.alphazero.AlphaZeroMaximumSearchPolicy;
import com.dipasquale.search.mcts.alphazero.AlphaZeroMonteCarloTreeSearch;
import com.dipasquale.search.mcts.alphazero.AlphaZeroPrediction;
import com.dipasquale.search.mcts.alphazero.BackPropagationType;
import com.dipasquale.search.mcts.alphazero.NeuralNetworkAlphaZeroContext;
import com.dipasquale.search.mcts.alphazero.NeuralNetworkAlphaZeroModel;
import com.dipasquale.search.mcts.alphazero.RootExplorationProbabilityNoiseSettings;
import com.dipasquale.search.mcts.alphazero.TemperatureController;
import com.dipasquale.search.mcts.classic.ClassicMonteCarloTreeSearch;
import com.dipasquale.search.mcts.common.CPuctCalculator;
import com.dipasquale.search.mcts.common.ExplorationProbabilityCalculator;
import com.dipasquale.search.mcts.common.ExtendedMaximumSearchPolicy;
import com.dipasquale.search.mcts.common.ValueHeuristic;
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
    private final CacheType cacheType;
    private final NeuralNetworkEncoder<GameState> encoder;
    private final NeuralNetworkDecoder<AlphaZeroPrediction<GameAction, GameState>, NeuralNetworkAlphaZeroContext<GameAction, GameState>> decoder;
    private final ValueHeuristic<GameAction, GameState> valueHeuristic;
    private final ExplorationProbabilityCalculator<GameAction> policyCalculator;
    private final CPuctCalculator cpuctCalculator;
    private final BackPropagationType backPropagationType;
    private final int temperatureDepthThreshold;
    private final int classicMaximumSelections;
    private final int classicMaximumSimulationRolloutDepth;
    private final CacheType classicCacheType;

    @Override
    public Player createPlayer(final NeatNeuralNetwork neuralNetwork) {
        return MctsPlayer.builder()
                .mcts(AlphaZeroMonteCarloTreeSearch.<GameAction, GameState>builder()
                        .searchPolicy(AlphaZeroMaximumSearchPolicy.builder()
                                .maximumExpansions(maximumExpansions)
                                .build())
                        .rootExplorationProbabilityNoise(rootExplorationProbabilityNoise)
                        .cacheType(cacheType)
                        .traversalModel(new NeuralNetworkAlphaZeroModel<>(encoder, decoder, neuralNetwork, valueHeuristic, policyCalculator))
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
                        .searchPolicy(ExtendedMaximumSearchPolicy.builder()
                                .maximumSelections(classicMaximumSelections)
                                .maximumSimulationRolloutDepth(classicMaximumSimulationRolloutDepth)
                                .build())
                        .cacheType(classicCacheType)
                        .build())
                .build();
    }

    @Override
    public int play(final Player player1, final Player player2) {
        GameResult result = Game.play(player1, player2);

        return result.getOutcomeId();
    }
}
