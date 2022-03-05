package com.dipasquale.ai.rl.neat.common.game2048;

import com.dipasquale.ai.common.NeuralNetworkDecoder;
import com.dipasquale.ai.common.NeuralNetworkEncoder;
import com.dipasquale.ai.rl.neat.common.OnePlayerGameSupport;
import com.dipasquale.ai.rl.neat.phenotype.NeatNeuralNetwork;
import com.dipasquale.common.factory.ObjectFactory;
import com.dipasquale.search.mcts.MaximumSearchPolicy;
import com.dipasquale.search.mcts.MonteCarloTreeSearch;
import com.dipasquale.search.mcts.alphazero.AlphaZeroPolicyDistributor;
import com.dipasquale.search.mcts.alphazero.AlphaZeroPrediction;
import com.dipasquale.search.mcts.alphazero.AlphaZeroValueCalculator;
import com.dipasquale.search.mcts.alphazero.MostVisitedActionEfficiencyCalculator;
import com.dipasquale.search.mcts.alphazero.NeuralNetworkAlphaZeroHeuristic;
import com.dipasquale.search.mcts.alphazero.NeuralNetworkAlphaZeroHeuristicContext;
import com.dipasquale.search.mcts.alphazero.SearchNodeProviderSettings;
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
    private final ObjectFactory<Game> gameFactory;

    @Override
    public Player createPlayer(final NeatNeuralNetwork neuralNetwork) {
        return RandomOutcomeMctsPlayer.builder()
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

    public Game createGame() {
        return gameFactory.create();
    }

    @Override
    public boolean play(final Player player) {
        Game game = createGame();

        return game.play(player).isSuccess();
    }
}
