package com.dipasquale.ai.rl.neat.common.game2048;

import com.dipasquale.ai.common.NeuralNetworkDecoder;
import com.dipasquale.ai.common.NeuralNetworkEncoder;
import com.dipasquale.ai.common.fitness.AverageFitnessDeterminerFactory;
import com.dipasquale.ai.rl.neat.ActivationSupport;
import com.dipasquale.ai.rl.neat.ConnectionGeneSupport;
import com.dipasquale.ai.rl.neat.ContinuousTrainingPolicy;
import com.dipasquale.ai.rl.neat.DelegatedTrainingPolicy;
import com.dipasquale.ai.rl.neat.EnumValue;
import com.dipasquale.ai.rl.neat.EvaluatorSettings;
import com.dipasquale.ai.rl.neat.FloatNumber;
import com.dipasquale.ai.rl.neat.GeneralSupport;
import com.dipasquale.ai.rl.neat.GenesisGenomeTemplate;
import com.dipasquale.ai.rl.neat.InitialConnectionType;
import com.dipasquale.ai.rl.neat.InitialWeightType;
import com.dipasquale.ai.rl.neat.IntegerNumber;
import com.dipasquale.ai.rl.neat.IsolatedNeatEnvironment;
import com.dipasquale.ai.rl.neat.MetricCollectionType;
import com.dipasquale.ai.rl.neat.MetricCollectorTrainingPolicy;
import com.dipasquale.ai.rl.neat.MetricsSupport;
import com.dipasquale.ai.rl.neat.MutationSupport;
import com.dipasquale.ai.rl.neat.NeatTrainingPolicies;
import com.dipasquale.ai.rl.neat.NeatTrainingPolicy;
import com.dipasquale.ai.rl.neat.NodeGeneSupport;
import com.dipasquale.ai.rl.neat.ParallelismSupport;
import com.dipasquale.ai.rl.neat.RandomType;
import com.dipasquale.ai.rl.neat.RecurrentStateType;
import com.dipasquale.ai.rl.neat.Sequence;
import com.dipasquale.ai.rl.neat.SupervisorTrainingPolicy;
import com.dipasquale.ai.rl.neat.common.TaskSetup;
import com.dipasquale.ai.rl.neat.common.TwoPlayerWinRateTrainingAssessor;
import com.dipasquale.ai.rl.neat.function.activation.ActivationFunctionType;
import com.dipasquale.ai.rl.neat.function.activation.OutputActivationFunctionType;
import com.dipasquale.ai.rl.neat.phenotype.DoubleSolutionNeuronLayerTopologyDefinition;
import com.dipasquale.ai.rl.neat.phenotype.IdentityNeuronLayerTopologyDefinition;
import com.dipasquale.ai.rl.neat.phenotype.NeuronLayerTopologyDefinition;
import com.dipasquale.common.time.MillisecondsDateTimeSupport;
import com.dipasquale.search.mcts.NodeCacheSettings;
import com.dipasquale.search.mcts.alphazero.AlphaZeroNeuralNetworkDecoder;
import com.dipasquale.search.mcts.alphazero.AlphaZeroPrediction;
import com.dipasquale.search.mcts.alphazero.AlphaZeroValueCalculator;
import com.dipasquale.search.mcts.alphazero.BackPropagationType;
import com.dipasquale.search.mcts.alphazero.CPuctCalculator;
import com.dipasquale.search.mcts.alphazero.NeuralNetworkAlphaZeroModelContext;
import com.dipasquale.search.mcts.alphazero.PredictionBehaviorType;
import com.dipasquale.search.mcts.alphazero.RootExplorationProbabilityNoiseSettings;
import com.dipasquale.search.mcts.alphazero.RosinCPuctCalculator;
import com.dipasquale.simulation.game2048.GameAction;
import com.dipasquale.simulation.game2048.GameState;
import com.dipasquale.simulation.game2048.Player;
import com.dipasquale.simulation.game2048.ValuePerRowNeuralNetworkEncoder;
import com.dipasquale.simulation.game2048.ValuePerTileInputNeuralNetworkEncoder;
import com.dipasquale.simulation.game2048.ValuePerTileLineInputNeuralNetworkEncoder;
import com.dipasquale.simulation.game2048.VectorEncodingType;
import com.dipasquale.synchronization.event.loop.BatchingEventLoop;
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
    private static final int MAXIMUM_EXPANSIONS = 150;
    private static final float EXPLORATION_PROBABILITY_NOISE_SHAPE = 0.03f;
    private static final float EXPLORATION_PROBABILITY_NOISE_EPSILON = 0.25f;
    private static final RootExplorationProbabilityNoiseType ROOT_EXPLORATION_PROBABILITY_NOISE_TYPE = RootExplorationProbabilityNoiseType.NONE;
    private static final NodeCacheType NODE_CACHE_TYPE = NodeCacheType.ENABLED;
    private static final PopulationSettingsType POPULATION_SETTINGS_TYPE = PopulationSettingsType.VANILLA;
    private static final VectorEncodingType VECTOR_ENCODING_TYPE = VectorEncodingType.INTEGER;
    private static final InputTopologySettingsType INPUT_TOPOLOGY_SETTINGS_TYPE = InputTopologySettingsType.VALUE_PER_ROW;
    private static final EnumSet<PredictionBehaviorType> PREDICTION_BEHAVIOR_TYPE = EnumSet.noneOf(PredictionBehaviorType.class);
    private static final OutputTopologySettingsType OUTPUT_TOPOLOGY_SETTINGS_TYPE = OutputTopologySettingsType.VANILLA_NO_VALUE;
    private static final ValueCalculatorSettingsType VALUE_CALCULATOR_SETTINGS_TYPE = ValueCalculatorSettingsType.HIGHEST_VALUED_TILE_ON_CORNER;
    private static final float C_PUCT_CONSTANT = 1f;
    private static final CPuctCalculatorType C_PUCT_CALCULATOR_TYPE = CPuctCalculatorType.ROSIN;
    private static final BackPropagationType BACK_PROPAGATION_TYPE = BackPropagationType.IDENTITY;
    private static final int TEMPERATURE_DEPTH_THRESHOLD = 3;
    private static final int MAXIMUM_VALUE = 512;
    private static final RandomGameFactory GAME_FACTORY = new RandomGameFactory(MAXIMUM_VALUE);

    private static final RandomOutcomeGameSupport GAME_SUPPORT = RandomOutcomeGameSupport.builder()
            .maximumExpansions(MAXIMUM_EXPANSIONS)
            .rootExplorationProbabilityNoise(ROOT_EXPLORATION_PROBABILITY_NOISE_TYPE.reference)
            .nodeCache(NODE_CACHE_TYPE.reference)
            .encoder(INPUT_TOPOLOGY_SETTINGS_TYPE.encoder)
            .decoder(OUTPUT_TOPOLOGY_SETTINGS_TYPE.decoder)
            .valueCalculator(VALUE_CALCULATOR_SETTINGS_TYPE.reference)
            .policyDistributor(new GameAlphaZeroPolicyDistributor())
            .cpuctCalculator(C_PUCT_CALCULATOR_TYPE.reference)
            .backPropagationType(BACK_PROPAGATION_TYPE)
            .temperatureDepthThreshold(TEMPERATURE_DEPTH_THRESHOLD)
            .gameFactory(GAME_FACTORY)
            .build();

    private static final int VALIDATION_MATCHES = 10;
    private static final double VALIDATION_WIN_RATE = 0.1D;
    private static final TwoPlayerWinRateTrainingAssessor<Player> WIN_RATE_TRAINING_ASSESSOR = new TwoPlayerWinRateTrainingAssessor<>(GAME_SUPPORT.createAsTwo(), VALIDATION_MATCHES, VALIDATION_WIN_RATE);
    private static final EnvironmentSettingsType ENVIRONMENT_SETTINGS_TYPE = EnvironmentSettingsType.AVERAGE_VALUED_TILE;
    private static final MutationSettingsType MUTATION_SETTINGS_TYPE = MutationSettingsType.RECOMMENDED_MARKOV;
    private static final int FITNESS_TEST_COUNT = 3;
    private final String name = "Game 2048";
    private final int populationSize = POPULATION_SETTINGS_TYPE.populationSize;
    private final boolean metricsEmissionEnabled;

    @Override
    public EvaluatorSettings createSettings(final Set<String> genomeIds, final BatchingEventLoop eventLoop) {
        return EvaluatorSettings.builder()
                .general(GeneralSupport.builder()
                        .populationSize(IntegerNumber.literal(populationSize))
                        .genesisGenomeTemplate(GenesisGenomeTemplate.builder()
                                .inputs(INPUT_TOPOLOGY_SETTINGS_TYPE.nodeCount)
                                .outputs(OUTPUT_TOPOLOGY_SETTINGS_TYPE.nodeCount)
                                .biases(List.of())
                                .initialConnectionType(InitialConnectionType.FULLY_CONNECTED)
                                .initialWeightType(InitialWeightType.ALL_RANDOM)
                                .build())
                        .fitnessFunction((IsolatedNeatEnvironment) genomeActivator -> {
                            genomeIds.add(genomeActivator.getGenome().getId());

                            return ENVIRONMENT_SETTINGS_TYPE.reference.test(genomeActivator);
                        })
                        .fitnessDeterminerFactory(AverageFitnessDeterminerFactory.getInstance())
                        .build())
                .parallelism(ParallelismSupport.builder()
                        .eventLoop(eventLoop)
                        .build())
                .nodes(NodeGeneSupport.builder()
                        .inputBias(FloatNumber.literal(0f))
                        .inputActivationFunction(EnumValue.literal(ActivationFunctionType.IDENTITY))
                        .outputBias(FloatNumber.random(RandomType.UNIFORM, 2f))
                        .outputActivationFunction(OUTPUT_TOPOLOGY_SETTINGS_TYPE.activationFunction)
                        .hiddenBias(FloatNumber.random(RandomType.UNIFORM, 4f))
                        .hiddenActivationFunction(EnumValue.literal(ActivationFunctionType.TAN_H))
                        .build())
                .connections(ConnectionGeneSupport.builder()
                        .weightFactory(FloatNumber.random(RandomType.BELL_CURVE, 2f))
                        .weightPerturber(FloatNumber.literal(2.5f))
                        .recurrentStateType(RecurrentStateType.DEFAULT)
                        .recurrentAllowanceRate(FloatNumber.literal(0f))
                        .unrestrictedDirectionAllowanceRate(FloatNumber.literal(0f))
                        .multiCycleAllowanceRate(FloatNumber.literal(0f))
                        .build())
                .activation(ActivationSupport.builder()
                        .outputTopologyDefinition(OUTPUT_TOPOLOGY_SETTINGS_TYPE.topologyDefinition)
                        .build())
                .mutation(MutationSupport.builder()
                        .addNodeRate(MUTATION_SETTINGS_TYPE.addNodeRate)
                        .addConnectionRate(MUTATION_SETTINGS_TYPE.addConnectionRate)
                        .perturbWeightRate(FloatNumber.literal(0.75f))
                        .replaceWeightRate(FloatNumber.literal(0.5f))
                        .disableExpressedConnectionRate(MUTATION_SETTINGS_TYPE.disableExpressedConnectionRate)
                        .build())
                .metrics(MetricsSupport.builder()
                        .type(metricsEmissionEnabled
                                ? EnumSet.of(MetricCollectionType.ENABLED)
                                : EnumSet.noneOf(MetricCollectionType.class))
                        .build())
                .build();
    }

    @Override
    public NeatTrainingPolicy createTrainingPolicy() {
        return NeatTrainingPolicies.builder()
                .add(SupervisorTrainingPolicy.builder()
                        .maximumGeneration(10_000_000)
                        .maximumRestartCount(5)
                        .build())
                .add(new MetricCollectorTrainingPolicy(new MillisecondsDateTimeSupport()))
                .add(new DelegatedTrainingPolicy(WIN_RATE_TRAINING_ASSESSOR))
                .add(ContinuousTrainingPolicy.builder()
                        .fitnessTestCount(FITNESS_TEST_COUNT)
                        .build())
                .build();
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private enum RootExplorationProbabilityNoiseType {
        NONE(null),
        ENABLED(RootExplorationProbabilityNoiseSettings.builder()
                .shape(EXPLORATION_PROBABILITY_NOISE_SHAPE)
                .epsilon(EXPLORATION_PROBABILITY_NOISE_EPSILON)
                .build());

        private final RootExplorationProbabilityNoiseSettings reference;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private enum NodeCacheType {
        NONE(null),
        ENABLED(NodeCacheSettings.builder()
                .participants(1)
                .build());

        private final NodeCacheSettings reference;
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
                new ValuePerTileInputNeuralNetworkEncoder()),
        VALUE_PER_ROW(4,
                ValuePerRowNeuralNetworkEncoder.builder()
                        .vectorEncodingType(VECTOR_ENCODING_TYPE)
                        .build()),
        VALUE_PER_TILE_LINE(8,
                ValuePerTileLineInputNeuralNetworkEncoder.builder()
                        .vectorEncodingType(VECTOR_ENCODING_TYPE)
                        .build());

        private final int nodeCount;
        private final NeuralNetworkEncoder<GameState> encoder;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private enum OutputTopologySettingsType {
        VANILLA_NO_VALUE(4,
                EnumValue.literal(OutputActivationFunctionType.SIGMOID),
                IdentityNeuronLayerTopologyDefinition.getInstance(),
                AlphaZeroNeuralNetworkDecoder.<GameAction, GameState>builder()
                        .perspectiveParticipantId(1)
                        .behaviorType(PREDICTION_BEHAVIOR_TYPE)
                        .valueIndex(-1)
                        .build()),
        DOUBLE_NO_VALUE(8,
                EnumValue.literal(OutputActivationFunctionType.SIGMOID),
                DoubleSolutionNeuronLayerTopologyDefinition.getInstance(),
                AlphaZeroNeuralNetworkDecoder.<GameAction, GameState>builder()
                        .perspectiveParticipantId(1)
                        .behaviorType(PREDICTION_BEHAVIOR_TYPE)
                        .valueIndex(-1)
                        .build()),
        VANILLA_WITH_VALUE(5,
                EnumValue.sequence(Sequence.<OutputActivationFunctionType>builder()
                        .add(1, OutputActivationFunctionType.TAN_H)
                        .add(4, OutputActivationFunctionType.SIGMOID)
                        .build()),
                IdentityNeuronLayerTopologyDefinition.getInstance(),
                AlphaZeroNeuralNetworkDecoder.<GameAction, GameState>builder()
                        .perspectiveParticipantId(1)
                        .behaviorType(PREDICTION_BEHAVIOR_TYPE)
                        .valueIndex(0)
                        .build()),
        DOUBLE_WITH_VALUE(10,
                EnumValue.sequence(Sequence.<OutputActivationFunctionType>builder()
                        .add(2, OutputActivationFunctionType.TAN_H)
                        .add(8, OutputActivationFunctionType.SIGMOID)
                        .build()),
                DoubleSolutionNeuronLayerTopologyDefinition.getInstance(),
                AlphaZeroNeuralNetworkDecoder.<GameAction, GameState>builder()
                        .perspectiveParticipantId(1)
                        .behaviorType(PREDICTION_BEHAVIOR_TYPE)
                        .valueIndex(0)
                        .build());

        private final int nodeCount;
        private final EnumValue<OutputActivationFunctionType> activationFunction;
        private final NeuronLayerTopologyDefinition topologyDefinition;
        private final NeuralNetworkDecoder<AlphaZeroPrediction<GameAction, GameState>, NeuralNetworkAlphaZeroModelContext<GameAction, GameState>> decoder;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private enum ValueCalculatorSettingsType {
        NONE(null),
        TWIN_VALUED_TILE(new TwinValuedTileValueCalculator()),
        AVERAGE_VALUED_TILE(AverageValuedTileObjective.createValueCalculator()),
        HIGHEST_VALUED_TILE_ON_CORNER(new HighestValuedTileOnCornerValueCalculator());

        private final AlphaZeroValueCalculator<GameAction, GameState> reference;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private enum CPuctCalculatorType {
        CONSTANT((simulations, visited) -> C_PUCT_CONSTANT),
        ROSIN(new RosinCPuctCalculator());

        private final CPuctCalculator reference;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private enum EnvironmentSettingsType {
        SCORE_BY_VALUED_TILE_COUNT(new ScoreByValuedTileCountEnvironment(GAME_SUPPORT)),
        AVERAGE_VALUED_TILE(AverageValuedTileObjective.createEnvironment(GAME_SUPPORT));

        private final IsolatedNeatEnvironment reference;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private enum MutationSettingsType {
        RECOMMENDED_MARKOV(FloatNumber.literal(0.03f), FloatNumber.literal(0.06f), FloatNumber.literal(0.015f)),
        DOUBLE_MARKOV(FloatNumber.literal(0.06f), FloatNumber.literal(0.12f), FloatNumber.literal(0.03f));

        private final FloatNumber addNodeRate;
        private final FloatNumber addConnectionRate;
        private final FloatNumber disableExpressedConnectionRate;
    }
}
