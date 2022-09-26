package com.dipasquale.ai.rl.neat.common.game2048;

import com.dipasquale.ai.common.NeuralNetworkEncoder;
import com.dipasquale.ai.common.fitness.AverageFitnessControllerFactory;
import com.dipasquale.ai.rl.neat.ActivationSettings;
import com.dipasquale.ai.rl.neat.ConnectionGeneSettings;
import com.dipasquale.ai.rl.neat.ContinuousTrainingPolicy;
import com.dipasquale.ai.rl.neat.DelegatedTrainingPolicy;
import com.dipasquale.ai.rl.neat.EnumValue;
import com.dipasquale.ai.rl.neat.FloatNumber;
import com.dipasquale.ai.rl.neat.GenesisGenomeTemplate;
import com.dipasquale.ai.rl.neat.InitialConnectionType;
import com.dipasquale.ai.rl.neat.InitialWeightType;
import com.dipasquale.ai.rl.neat.IntegerNumber;
import com.dipasquale.ai.rl.neat.MetricCollectionType;
import com.dipasquale.ai.rl.neat.MetricCollectorTrainingPolicy;
import com.dipasquale.ai.rl.neat.MetricsSettings;
import com.dipasquale.ai.rl.neat.MutationSettings;
import com.dipasquale.ai.rl.neat.NeatSettings;
import com.dipasquale.ai.rl.neat.NeatTrainingPolicy;
import com.dipasquale.ai.rl.neat.NeatTrainingPolicyController;
import com.dipasquale.ai.rl.neat.NodeGeneSettings;
import com.dipasquale.ai.rl.neat.ParallelismSettings;
import com.dipasquale.ai.rl.neat.RandomType;
import com.dipasquale.ai.rl.neat.RecurrentStateType;
import com.dipasquale.ai.rl.neat.SecludedNeatEnvironment;
import com.dipasquale.ai.rl.neat.Sequence;
import com.dipasquale.ai.rl.neat.SpeciationSettings;
import com.dipasquale.ai.rl.neat.SupervisorTrainingPolicy;
import com.dipasquale.ai.rl.neat.common.TaskSetup;
import com.dipasquale.ai.rl.neat.common.TwoPlayerWinRateTrainingAssessor;
import com.dipasquale.ai.rl.neat.function.activation.ActivationFunctionType;
import com.dipasquale.ai.rl.neat.function.activation.OutputActivationFunctionType;
import com.dipasquale.ai.rl.neat.phenotype.DoubleSolutionNeuronLayerTopologyDefinition;
import com.dipasquale.ai.rl.neat.phenotype.IdentityNeuronLayerTopologyDefinition;
import com.dipasquale.ai.rl.neat.phenotype.NeuronLayerTopologyDefinition;
import com.dipasquale.common.time.MillisecondsDateTimeSupport;
import com.dipasquale.search.mcts.StandardSearchNode;
import com.dipasquale.search.mcts.alphazero.AlphaZeroEdge;
import com.dipasquale.search.mcts.alphazero.expansion.RootExplorationProbabilityNoiseSettings;
import com.dipasquale.search.mcts.alphazero.selection.AlphaZeroNeuralNetworkDecoder;
import com.dipasquale.search.mcts.alphazero.selection.PredictionBehaviorType;
import com.dipasquale.search.mcts.buffer.BufferType;
import com.dipasquale.search.mcts.heuristic.selection.CPuctAlgorithm;
import com.dipasquale.search.mcts.heuristic.selection.RewardHeuristic;
import com.dipasquale.search.mcts.heuristic.selection.RosinCPuctAlgorithm;
import com.dipasquale.search.mcts.propagation.BackPropagationType;
import com.dipasquale.simulation.game2048.GameAction;
import com.dipasquale.simulation.game2048.GameState;
import com.dipasquale.simulation.game2048.Player;
import com.dipasquale.simulation.game2048.encoding.InputPerRowNeuralNetworkEncoder;
import com.dipasquale.simulation.game2048.encoding.InputPerTileInputNeuralNetworkEncoder;
import com.dipasquale.simulation.game2048.encoding.InputPerTileLineInputNeuralNetworkEncoder;
import com.dipasquale.simulation.game2048.encoding.VectorEncodingType;
import com.dipasquale.simulation.game2048.heuristic.AverageValuedTileRewardHeuristic;
import com.dipasquale.simulation.game2048.heuristic.FreeTileRewardHeuristic;
import com.dipasquale.simulation.game2048.heuristic.GameExplorationHeuristic;
import com.dipasquale.simulation.game2048.heuristic.MonotonicityRewardHeuristic;
import com.dipasquale.simulation.game2048.heuristic.TwinValuedTileRewardHeuristic;
import com.dipasquale.simulation.game2048.heuristic.UniformityRewardHeuristic;
import com.dipasquale.simulation.game2048.heuristic.WeightedBoardRewardHeuristic;
import com.dipasquale.simulation.game2048.heuristic.WeightedBoardType;
import com.dipasquale.synchronization.event.loop.ParallelEventLoop;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public final class Game2048TaskSetup implements TaskSetup {
    private static final int MAXIMUM_EXPANSIONS = 75;
    private static final float ROOT_EXPLORATION_PROBABILITY_NOISE_SHAPE = 0.03f;
    private static final float ROOT_EXPLORATION_PROBABILITY_NOISE_EPSILON = 0.25f;
    private static final RootExplorationProbabilityNoiseType ROOT_EXPLORATION_PROBABILITY_NOISE_TYPE = RootExplorationProbabilityNoiseType.NONE;
    private static final BufferType CACHE_TYPE = BufferType.AUTO_CLEAR;
    private static final PopulationSettingsType POPULATION_SETTINGS_TYPE = PopulationSettingsType.VANILLA;
    private static final VectorEncodingType VECTOR_ENCODING_TYPE = VectorEncodingType.INTEGER;
    private static final InputTopologySettingsType INPUT_TOPOLOGY_SETTINGS_TYPE = InputTopologySettingsType.VALUE_PER_ROW;
    private static final EnumSet<PredictionBehaviorType> PREDICTION_BEHAVIOR_TYPES = EnumSet.noneOf(PredictionBehaviorType.class);
    private static final OutputTopologySettingsType OUTPUT_TOPOLOGY_SETTINGS_TYPE = OutputTopologySettingsType.DOUBLE;
    private static final ValueHeuristicSettingsType VALUE_HEURISTIC_SETTINGS_TYPE = ValueHeuristicSettingsType.AVERAGE_VALUED_TILE;
    private static final float C_PUCT_CONSTANT = 1f;
    private static final CPuctAlgorithmType C_PUCT_ALGORITHM_TYPE = CPuctAlgorithmType.CONSTANT;
    private static final BackPropagationType BACK_PROPAGATION_TYPE = BackPropagationType.IDENTITY;
    private static final int TEMPERATURE_DEPTH_THRESHOLD = 90;
    private static final int VICTORY_VALUE = 11;
    private static final WeightedBoardType WEIGHTED_BOARD_TYPE = WeightedBoardType.SNAKE_SHAPE;
    private static final RandomGameFactory GAME_FACTORY = new RandomGameFactory(VICTORY_VALUE);

    private static final RandomOutcomeGameSupport GAME_SUPPORT = RandomOutcomeGameSupport.builder()
            .maximumExpansions(MAXIMUM_EXPANSIONS)
            .rootExplorationProbabilityNoise(ROOT_EXPLORATION_PROBABILITY_NOISE_TYPE.reference)
            .bufferType(CACHE_TYPE)
            .encoder(INPUT_TOPOLOGY_SETTINGS_TYPE.encoder)
            .decoder(OUTPUT_TOPOLOGY_SETTINGS_TYPE.decoder)
            .rewardHeuristic(VALUE_HEURISTIC_SETTINGS_TYPE.reference)
            .explorationHeuristic(GameExplorationHeuristic.getInstance())
            .cpuctAlgorithm(C_PUCT_ALGORITHM_TYPE.reference)
            .backPropagationType(BACK_PROPAGATION_TYPE)
            .temperatureDepthThreshold(TEMPERATURE_DEPTH_THRESHOLD)
            .gameFactory(GAME_FACTORY)
            .build();

    private static final int VALIDATION_MATCHES = 10;
    private static final double VALIDATION_WIN_RATE = 0.1D;
    private static final TwoPlayerWinRateTrainingAssessor<Player> WIN_RATE_TRAINING_ASSESSOR = new TwoPlayerWinRateTrainingAssessor<>(GAME_SUPPORT.createAsTwo(), VALIDATION_MATCHES, VALIDATION_WIN_RATE);
    private static final EnvironmentSettingsType ENVIRONMENT_SETTINGS_TYPE = EnvironmentSettingsType.AVERAGE_VALUED_TILE;
    private static final MutationSettingsType MUTATION_SETTINGS_TYPE = MutationSettingsType.RECOMMENDED_MARKOV;
    private static final int FITNESS_EVALUATION_COUNT = 3;
    private final String name = "Game 2048";
    private final int populationSize = POPULATION_SETTINGS_TYPE.populationSize;
    private final boolean metricsEmissionEnabled;

    @Override
    public NeatSettings createSettings(final Set<Integer> genomeIds, final ParallelEventLoop eventLoop) {
        return NeatSettings.builder()
                .parallelism(ParallelismSettings.builder()
                        .eventLoop(eventLoop)
                        .build())
                .activation(ActivationSettings.builder()
                        .genesisGenomeTemplate(GenesisGenomeTemplate.builder()
                                .inputs(INPUT_TOPOLOGY_SETTINGS_TYPE.nodeCount)
                                .outputs(OUTPUT_TOPOLOGY_SETTINGS_TYPE.nodeCount)
                                .biases(List.of())
                                .initialConnectionType(InitialConnectionType.FULLY_CONNECTED)
                                .initialWeightType(InitialWeightType.ALL_RANDOM)
                                .build())
                        .fitnessFunction((SecludedNeatEnvironment) genomeActivator -> {
                            genomeIds.add(genomeActivator.getGenome().getId());

                            return ENVIRONMENT_SETTINGS_TYPE.reference.test(genomeActivator);
                        })
                        .fitnessControllerFactory(AverageFitnessControllerFactory.getInstance())
                        .outputTopologyDefinition(OUTPUT_TOPOLOGY_SETTINGS_TYPE.topologyDefinition)
                        .build())
                .nodeGenes(NodeGeneSettings.builder()
                        .inputBias(FloatNumber.constant(0f))
                        .inputActivationFunction(EnumValue.constant(ActivationFunctionType.IDENTITY))
                        .outputBias(FloatNumber.random(RandomType.UNIFORM, 2f))
                        .outputActivationFunction(OUTPUT_TOPOLOGY_SETTINGS_TYPE.activationFunction)
                        .hiddenBias(FloatNumber.random(RandomType.UNIFORM, 4f))
                        .hiddenActivationFunction(EnumValue.constant(ActivationFunctionType.TAN_H))
                        .build())
                .connectionGenes(ConnectionGeneSettings.builder()
                        .weightFactory(FloatNumber.random(RandomType.BELL_CURVE, 2f))
                        .weightPerturber(FloatNumber.constant(2.5f))
                        .recurrentStateType(RecurrentStateType.DEFAULT)
                        .recurrentAllowanceRate(0f)
                        .unrestrictedDirectionAllowanceRate(0f)
                        .multiCycleAllowanceRate(0f)
                        .build())
                .mutation(MutationSettings.builder()
                        .addNodeRate(MUTATION_SETTINGS_TYPE.addNodeRate)
                        .addConnectionRate(MUTATION_SETTINGS_TYPE.addConnectionRate)
                        .perturbWeightRate(FloatNumber.constant(0.75f))
                        .replaceWeightRate(FloatNumber.constant(0.5f))
                        .disableExpressedConnectionRate(MUTATION_SETTINGS_TYPE.disableExpressedConnectionRate)
                        .build())
                .speciation(SpeciationSettings.builder()
                        .populationSize(IntegerNumber.constant(populationSize))
                        .build())
                .metrics(MetricsSettings.builder()
                        .types(metricsEmissionEnabled
                                ? EnumSet.of(MetricCollectionType.ENABLED)
                                : EnumSet.noneOf(MetricCollectionType.class))
                        .build())
                .build();
    }

    @Override
    public NeatTrainingPolicy createTrainingPolicy() {
        return NeatTrainingPolicyController.builder()
                .add(SupervisorTrainingPolicy.builder()
                        .maximumGeneration(10_000_000)
                        .maximumRestartCount(5)
                        .build())
                .add(new MetricCollectorTrainingPolicy(new MillisecondsDateTimeSupport()))
                .add(new DelegatedTrainingPolicy(WIN_RATE_TRAINING_ASSESSOR))
                .add(ContinuousTrainingPolicy.builder()
                        .fitnessEvaluationCount(FITNESS_EVALUATION_COUNT)
                        .build())
                .build();
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private enum RootExplorationProbabilityNoiseType {
        NONE(null),
        ENABLED(RootExplorationProbabilityNoiseSettings.builder()
                .shape(ROOT_EXPLORATION_PROBABILITY_NOISE_SHAPE)
                .epsilon(ROOT_EXPLORATION_PROBABILITY_NOISE_EPSILON)
                .build());

        private final RootExplorationProbabilityNoiseSettings reference;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private enum PopulationSettingsType {
        VANILLA(150),
        DOUBLE(300);

        private final int populationSize;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private enum InputTopologySettingsType {
        VALUE_PER_TILE(16,
                new InputPerTileInputNeuralNetworkEncoder()),
        VALUE_PER_ROW(4,
                InputPerRowNeuralNetworkEncoder.builder()
                        .vectorEncodingType(VECTOR_ENCODING_TYPE)
                        .build()),
        VALUE_PER_TILE_LINE(8,
                InputPerTileLineInputNeuralNetworkEncoder.builder()
                        .vectorEncodingType(VECTOR_ENCODING_TYPE)
                        .build());

        private final int nodeCount;
        private final NeuralNetworkEncoder<GameState> encoder;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private enum OutputTopologySettingsType {
        VANILLA_NO_VALUE(4,
                EnumValue.constant(OutputActivationFunctionType.SIGMOID),
                IdentityNeuronLayerTopologyDefinition.getInstance(),
                AlphaZeroNeuralNetworkDecoder.<GameAction, GameState, StandardSearchNode<GameAction, AlphaZeroEdge, GameState>>builder()
                        .perspectiveParticipantId(1)
                        .behaviorTypes(PREDICTION_BEHAVIOR_TYPES)
                        .valueIndex(-1)
                        .build()),
        VANILLA(5,
                EnumValue.sequence(Sequence.<OutputActivationFunctionType>builder()
                        .add(1, OutputActivationFunctionType.TAN_H)
                        .add(4, OutputActivationFunctionType.SIGMOID)
                        .build()),
                IdentityNeuronLayerTopologyDefinition.getInstance(),
                AlphaZeroNeuralNetworkDecoder.<GameAction, GameState, StandardSearchNode<GameAction, AlphaZeroEdge, GameState>>builder()
                        .perspectiveParticipantId(1)
                        .behaviorTypes(PREDICTION_BEHAVIOR_TYPES)
                        .valueIndex(0)
                        .build()),
        DOUBLE_NO_VALUE(8,
                EnumValue.constant(OutputActivationFunctionType.SIGMOID),
                DoubleSolutionNeuronLayerTopologyDefinition.getInstance(),
                AlphaZeroNeuralNetworkDecoder.<GameAction, GameState, StandardSearchNode<GameAction, AlphaZeroEdge, GameState>>builder()
                        .perspectiveParticipantId(1)
                        .behaviorTypes(PREDICTION_BEHAVIOR_TYPES)
                        .valueIndex(-1)
                        .build()),
        DOUBLE(10,
                EnumValue.sequence(Sequence.<OutputActivationFunctionType>builder()
                        .add(2, OutputActivationFunctionType.TAN_H)
                        .add(8, OutputActivationFunctionType.SIGMOID)
                        .build()),
                DoubleSolutionNeuronLayerTopologyDefinition.getInstance(),
                AlphaZeroNeuralNetworkDecoder.<GameAction, GameState, StandardSearchNode<GameAction, AlphaZeroEdge, GameState>>builder()
                        .perspectiveParticipantId(1)
                        .behaviorTypes(PREDICTION_BEHAVIOR_TYPES)
                        .valueIndex(0)
                        .build());

        private final int nodeCount;
        private final EnumValue<OutputActivationFunctionType> activationFunction;
        private final NeuronLayerTopologyDefinition topologyDefinition;
        private final AlphaZeroNeuralNetworkDecoder<GameAction, GameState, StandardSearchNode<GameAction, AlphaZeroEdge, GameState>> decoder;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private enum ValueHeuristicSettingsType {
        NONE(null),
        WEIGHTED_BOARD(new WeightedBoardRewardHeuristic(WEIGHTED_BOARD_TYPE)),
        FREE_TILE(FreeTileRewardHeuristic.getInstance()),
        MONOTONICITY(MonotonicityRewardHeuristic.getInstance()),
        TWIN_VALUED_TILE(TwinValuedTileRewardHeuristic.getInstance()),
        UNIFORMITY(UniformityRewardHeuristic.getInstance()),
        AVERAGE_VALUED_TILE(AverageValuedTileRewardHeuristic.getInstance());

        private final RewardHeuristic<GameAction, GameState> reference;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private enum CPuctAlgorithmType {
        CONSTANT((simulations, visited) -> C_PUCT_CONSTANT),
        ROSIN(new RosinCPuctAlgorithm());

        private final CPuctAlgorithm reference;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private enum EnvironmentSettingsType {
        SCORE_BY_VALUED_TILE_COUNT(new ScoreByValuedTileCountEnvironment(GAME_SUPPORT)),
        AVERAGE_VALUED_TILE(new AverageValuedTileEnvironment(GAME_SUPPORT));

        private final SecludedNeatEnvironment reference;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private enum MutationSettingsType {
        RECOMMENDED_MARKOV(FloatNumber.constant(0.03f), FloatNumber.constant(0.06f), FloatNumber.constant(0.015f)),
        DOUBLE_MARKOV(FloatNumber.constant(0.06f), FloatNumber.constant(0.12f), FloatNumber.constant(0.03f));

        private final FloatNumber addNodeRate;
        private final FloatNumber addConnectionRate;
        private final FloatNumber disableExpressedConnectionRate;
    }
}
