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
import com.dipasquale.ai.rl.neat.Sequence;
import com.dipasquale.ai.rl.neat.SupervisorTrainingPolicy;
import com.dipasquale.ai.rl.neat.common.TaskSetup;
import com.dipasquale.ai.rl.neat.common.TwoPlayerP55WinRateTrainingAssessor;
import com.dipasquale.ai.rl.neat.function.activation.ActivationFunctionType;
import com.dipasquale.ai.rl.neat.function.activation.OutputActivationFunctionType;
import com.dipasquale.ai.rl.neat.phenotype.DoubleSolutionNeuronLayerTopologyDefinition;
import com.dipasquale.ai.rl.neat.phenotype.NeuronLayerTopologyDefinition;
import com.dipasquale.common.time.MillisecondsDateTimeSupport;
import com.dipasquale.search.mcts.alphazero.AlphaZeroNeuralNetworkDecoder;
import com.dipasquale.search.mcts.alphazero.AlphaZeroPrediction;
import com.dipasquale.search.mcts.alphazero.AlphaZeroValueCalculator;
import com.dipasquale.search.mcts.alphazero.NeuralNetworkAlphaZeroHeuristicContext;
import com.dipasquale.search.mcts.alphazero.PredictionBehaviorType;
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
public final class AlphaZeroGame2048TaskSetup implements TaskSetup {
    private static final PopulationSettingsType POPULATION_SETTINGS_TYPE = PopulationSettingsType.VANILLA;
    private static final int TRAINING_MATCH_MAXIMUM_SIMULATIONS = 150;
    private static final int TRAINING_MATCH_MAXIMUM_DEPTH = 9;
    private static final boolean TRAINING_ALLOW_ROOT_EXPLORATION_NOISE = false;
    private static final VectorEncodingType VECTOR_ENCODING_TYPE = VectorEncodingType.INTEGER;
    private static final InputTopologySettingsType INPUT_TOPOLOGY_SETTINGS_TYPE = InputTopologySettingsType.VALUE_PER_ROW;
    private static final OutputTopologySettingsType OUTPUT_TOPOLOGY_SETTINGS_TYPE = OutputTopologySettingsType.DOUBLE_NO_VALUE;
    private static final float TRAINING_INITIAL_TEMPERATURE = 0.001f;
    private static final float TRAINING_FINAL_TEMPERATURE = 1f;
    private static final int TRAINING_TEMPERATURE_DEPTH_THRESHOLD = 15;
    private static final ValueCalculatorSettingsType VALUE_CALCULATOR_SETTINGS_TYPE = ValueCalculatorSettingsType.HIGHEST_VALUED_TILE_ON_CORNER;
    private static final int GOAL_MAXIMUM_VALUE = 2_048;
    private static final RandomGameFactory GAME_FACTORY = new RandomGameFactory(GOAL_MAXIMUM_VALUE);

    private static final RandomOutcomeGameSupport GAME_SUPPORT = RandomOutcomeGameSupport.builder()
            .trainingMatchMaximumSimulations(TRAINING_MATCH_MAXIMUM_SIMULATIONS)
            .trainingMatchMaximumDepth(TRAINING_MATCH_MAXIMUM_DEPTH)
            .trainingAllowRootExplorationNoise(TRAINING_ALLOW_ROOT_EXPLORATION_NOISE)
            .encoder(INPUT_TOPOLOGY_SETTINGS_TYPE.encoder)
            .decoder(OUTPUT_TOPOLOGY_SETTINGS_TYPE.decoder)
            .valueCalculator(VALUE_CALCULATOR_SETTINGS_TYPE.reference)
            .policyDistributor(new GameAlphaZeroPolicyDistributor())
            .trainingInitialTemperature(TRAINING_INITIAL_TEMPERATURE)
            .trainingFinalTemperature(TRAINING_FINAL_TEMPERATURE)
            .trainingTemperatureDepthThreshold(TRAINING_TEMPERATURE_DEPTH_THRESHOLD)
            .gameFactory(GAME_FACTORY)
            .build();

    private static final int VALIDATION_MATCHES = 10;
    private static final TwoPlayerP55WinRateTrainingAssessor<Player> P55_WIN_RATE_TRAINING_ASSESSOR = new TwoPlayerP55WinRateTrainingAssessor<>(GAME_SUPPORT.createAsTwo(), VALIDATION_MATCHES);
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
                                .initialConnectionType(InitialConnectionType.ALL_INPUTS_AND_BIASES_TO_ALL_OUTPUTS)
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
                        .outputBias(FloatNumber.random(RandomType.QUADRUPLE_SIGMOID, 15f))
                        .outputActivationFunction(OUTPUT_TOPOLOGY_SETTINGS_TYPE.activationFunction)
                        .hiddenBias(FloatNumber.random(RandomType.QUADRUPLE_STEEPENED_SIGMOID, 30f))
                        .hiddenActivationFunction(EnumValue.literal(ActivationFunctionType.SIGMOID))
                        .build())
                .connections(ConnectionGeneSupport.builder()
                        .weightFactory(FloatNumber.random(RandomType.UNIFORM, 0.5f))
                        .recurrentAllowanceRate(FloatNumber.literal(0f))
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
                .add(new DelegatedTrainingPolicy(P55_WIN_RATE_TRAINING_ASSESSOR))
                .add(ContinuousTrainingPolicy.builder()
                        .fitnessTestCount(FITNESS_TEST_COUNT)
                        .build())
                .build();
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private enum PopulationSettingsType {
        VANILLA(150),
        DOUBLE(300);

        private final int populationSize;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private enum InputTopologySettingsType {
        VALUE_PER_TILE(IntegerNumber.literal(16),
                new ValuePerTileInputNeuralNetworkEncoder()),
        VALUE_PER_ROW(IntegerNumber.literal(4),
                ValuePerRowNeuralNetworkEncoder.builder()
                        .vectorEncodingType(VECTOR_ENCODING_TYPE)
                        .build()),
        VALUE_PER_TILE_LINE(IntegerNumber.literal(8),
                ValuePerTileLineInputNeuralNetworkEncoder.builder()
                        .vectorEncodingType(VECTOR_ENCODING_TYPE)
                        .build());

        private final IntegerNumber nodeCount;
        private final NeuralNetworkEncoder<GameState> encoder;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private enum OutputTopologySettingsType {
        DOUBLE_NO_VALUE(IntegerNumber.literal(8),
                EnumValue.literal(OutputActivationFunctionType.SIGMOID),
                DoubleSolutionNeuronLayerTopologyDefinition.getInstance(),
                AlphaZeroNeuralNetworkDecoder.<GameAction, GameState>builder()
                        .perspectiveParticipantId(1)
                        .behaviorType(EnumSet.noneOf(PredictionBehaviorType.class))
                        .valueIndex(-1)
                        .build()),
        DOUBLE_WITH_VALUE(IntegerNumber.literal(10),
                EnumValue.sequence(Sequence.<OutputActivationFunctionType>builder()
                        .add(2, OutputActivationFunctionType.TAN_H)
                        .add(8, OutputActivationFunctionType.SIGMOID)
                        .build()),
                DoubleSolutionNeuronLayerTopologyDefinition.getInstance(),
                AlphaZeroNeuralNetworkDecoder.<GameAction, GameState>builder()
                        .perspectiveParticipantId(1)
                        .behaviorType(EnumSet.noneOf(PredictionBehaviorType.class))
                        .valueIndex(0)
                        .build());

        private final IntegerNumber nodeCount;
        private final EnumValue<OutputActivationFunctionType> activationFunction;
        private final NeuronLayerTopologyDefinition topologyDefinition;
        private final NeuralNetworkDecoder<AlphaZeroPrediction<GameAction, GameState>, NeuralNetworkAlphaZeroHeuristicContext<GameAction, GameState>> decoder;
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
