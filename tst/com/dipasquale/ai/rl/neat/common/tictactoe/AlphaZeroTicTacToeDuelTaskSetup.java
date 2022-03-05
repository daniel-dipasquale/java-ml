package com.dipasquale.ai.rl.neat.common.tictactoe;

import com.dipasquale.ai.common.NeuralNetworkDecoder;
import com.dipasquale.ai.common.NeuralNetworkEncoder;
import com.dipasquale.ai.common.fitness.AverageFitnessDeterminerFactory;
import com.dipasquale.ai.rl.neat.ActivationSupport;
import com.dipasquale.ai.rl.neat.ConnectionGeneSupport;
import com.dipasquale.ai.rl.neat.ContestNeatEnvironment;
import com.dipasquale.ai.rl.neat.ContinuousTrainingPolicy;
import com.dipasquale.ai.rl.neat.CrossOverSupport;
import com.dipasquale.ai.rl.neat.DelegatedTrainingPolicy;
import com.dipasquale.ai.rl.neat.EnumValue;
import com.dipasquale.ai.rl.neat.EvaluatorSettings;
import com.dipasquale.ai.rl.neat.FloatNumber;
import com.dipasquale.ai.rl.neat.GeneralSupport;
import com.dipasquale.ai.rl.neat.GenesisGenomeTemplate;
import com.dipasquale.ai.rl.neat.InitialConnectionType;
import com.dipasquale.ai.rl.neat.InitialWeightType;
import com.dipasquale.ai.rl.neat.IntegerNumber;
import com.dipasquale.ai.rl.neat.MetricCollectionType;
import com.dipasquale.ai.rl.neat.MetricCollectorTrainingPolicy;
import com.dipasquale.ai.rl.neat.MetricsSupport;
import com.dipasquale.ai.rl.neat.MutationSupport;
import com.dipasquale.ai.rl.neat.NeatTrainingPolicies;
import com.dipasquale.ai.rl.neat.NeatTrainingPolicy;
import com.dipasquale.ai.rl.neat.NodeGeneSupport;
import com.dipasquale.ai.rl.neat.ParallelismSupport;
import com.dipasquale.ai.rl.neat.RandomSupport;
import com.dipasquale.ai.rl.neat.RandomType;
import com.dipasquale.ai.rl.neat.RecurrentStateType;
import com.dipasquale.ai.rl.neat.RoundRobinDuelNeatEnvironment;
import com.dipasquale.ai.rl.neat.Sequence;
import com.dipasquale.ai.rl.neat.SpeciationSupport;
import com.dipasquale.ai.rl.neat.SupervisorTrainingPolicy;
import com.dipasquale.ai.rl.neat.common.TaskSetup;
import com.dipasquale.ai.rl.neat.common.TwoPlayerP55WinRateTrainingAssessor;
import com.dipasquale.ai.rl.neat.function.activation.ActivationFunctionType;
import com.dipasquale.ai.rl.neat.function.activation.OutputActivationFunctionType;
import com.dipasquale.ai.rl.neat.phenotype.DoubleSolutionNeuronLayerTopologyDefinition;
import com.dipasquale.ai.rl.neat.phenotype.GenomeActivator;
import com.dipasquale.ai.rl.neat.phenotype.IdentityNeuronLayerTopologyDefinition;
import com.dipasquale.ai.rl.neat.phenotype.NeuronLayerTopologyDefinition;
import com.dipasquale.common.time.MillisecondsDateTimeSupport;
import com.dipasquale.search.mcts.alphazero.AlphaZeroNeuralNetworkDecoder;
import com.dipasquale.search.mcts.alphazero.AlphaZeroPrediction;
import com.dipasquale.search.mcts.alphazero.AlphaZeroValueCalculator;
import com.dipasquale.search.mcts.alphazero.NeuralNetworkAlphaZeroHeuristicContext;
import com.dipasquale.search.mcts.alphazero.PredictionBehaviorType;
import com.dipasquale.simulation.tictactoe.GameAction;
import com.dipasquale.simulation.tictactoe.GameState;
import com.dipasquale.simulation.tictactoe.Player;
import com.dipasquale.simulation.tictactoe.ValuePerBoardInputNeuralNetworkEncoder;
import com.dipasquale.simulation.tictactoe.ValuePerPlayerInputNeuralNetworkEncoder;
import com.dipasquale.simulation.tictactoe.ValuePerTileInputNeuralNetworkEncoder;
import com.dipasquale.simulation.tictactoe.VectorEncodingType;
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
public final class AlphaZeroTicTacToeDuelTaskSetup implements TaskSetup {
    private static final int TRAINING_MATCH_MAXIMUM_SIMULATIONS = 30;
    private static final int TRAINING_MATCH_MAXIMUM_DEPTH = 9;
    private static final boolean TRAINING_ALLOW_ROOT_EXPLORATION_NOISE = false;
    private static final float TRAINING_INITIAL_TEMPERATURE = 0.001f;
    private static final float TRAINING_FINAL_TEMPERATURE = 1f;
    private static final int TRAINING_TEMPERATURE_DEPTH_THRESHOLD = 4;
    private static final PopulationSettingsType POPULATION_SETTINGS_TYPE = PopulationSettingsType.RECOMMENDED_DUEL;
    private static final VectorEncodingType VECTOR_ENCODING_TYPE = VectorEncodingType.INTEGER;
    private static final InputTopologySettingsType INPUT_TOPOLOGY_SETTINGS_TYPE = InputTopologySettingsType.VALUE_PER_BOARD;
    private static final EnumSet<PredictionBehaviorType> PREDICTION_BEHAVIOR_TYPE = EnumSet.of(PredictionBehaviorType.VALUE_FOR_INITIAL_STATE_IS_ZERO, PredictionBehaviorType.INVERSE_POLICY_FOR_OPPONENT, PredictionBehaviorType.INVERSE_VALUE_FOR_OPPONENT);
    private static final OutputTopologySettingsType OUTPUT_TOPOLOGY_SETTINGS_TYPE = OutputTopologySettingsType.DOUBLE;
    private static final ValueCalculatorSettingsType VALUE_CALCULATOR_SETTINGS_TYPE = ValueCalculatorSettingsType.ACTION_SCORE;
    private static final int VALIDATION_MATCH_MAXIMUM_SIMULATIONS = 1_600;
    private static final int VALIDATION_MATCH_MAXIMUM_DEPTH = 9;

    private static final GameSupport GAME_SUPPORT = GameSupport.builder()
            .trainingMatchMaximumSimulations(TRAINING_MATCH_MAXIMUM_SIMULATIONS)
            .trainingMatchMaximumDepth(TRAINING_MATCH_MAXIMUM_DEPTH)
            .trainingAllowRootExplorationNoise(TRAINING_ALLOW_ROOT_EXPLORATION_NOISE)
            .trainingInitialTemperature(TRAINING_INITIAL_TEMPERATURE)
            .trainingFinalTemperature(TRAINING_FINAL_TEMPERATURE)
            .trainingTemperatureDepthThreshold(TRAINING_TEMPERATURE_DEPTH_THRESHOLD)
            .encoder(INPUT_TOPOLOGY_SETTINGS_TYPE.encoder)
            .decoder(OUTPUT_TOPOLOGY_SETTINGS_TYPE.decoder)
            .valueCalculator(VALUE_CALCULATOR_SETTINGS_TYPE.reference)
            .policyDistributor(null)
            .validationMatchMaximumSimulations(VALIDATION_MATCH_MAXIMUM_SIMULATIONS)
            .validationMatchMaximumDepth(VALIDATION_MATCH_MAXIMUM_DEPTH)
            .build();

    private static final EnvironmentSettingsType ENVIRONMENT_SETTINGS_TYPE = EnvironmentSettingsType.ACTION_SCORE;
    private static final int VALIDATION_MATCHES = 100;
    private static final TwoPlayerP55WinRateTrainingAssessor<Player> P55_WIN_RATE_TRAINING_ASSESSOR = new TwoPlayerP55WinRateTrainingAssessor<>(GAME_SUPPORT, VALIDATION_MATCHES);
    private static final MutationSettingsType MUTATION_SETTINGS_TYPE = MutationSettingsType.RECOMMENDED_MARKOV;
    private static final SpeciationSettingsType SPECIATION_SETTINGS_TYPE = SpeciationSettingsType.RECOMMENDED_MARKOV;
    private static final int FITNESS_TEST_COUNT = 4;
    private final String name = "Tic-Tac-Toe";
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
                        .fitnessFunction(RoundRobinDuelNeatEnvironment.builder()
                                .environment((genomeActivators, round) -> {
                                    for (GenomeActivator genomeActivator : genomeActivators) {
                                        genomeIds.add(genomeActivator.getGenome().getId());
                                    }

                                    return ENVIRONMENT_SETTINGS_TYPE.reference.test(genomeActivators, round);
                                })
                                .approximateMatchesPerGenome(POPULATION_SETTINGS_TYPE.approximateMatchesPerGenome)
                                .rematches(1)
                                .eliminationRounds(POPULATION_SETTINGS_TYPE.eliminationRounds)
                                .build())
                        .fitnessDeterminerFactory(AverageFitnessDeterminerFactory.getInstance())
                        .build())
                .parallelism(ParallelismSupport.builder()
                        .eventLoop(eventLoop)
                        .build())
                .random(RandomSupport.builder()
                        .type(RandomType.UNIFORM)
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
                .crossOver(CrossOverSupport.builder()
                        .overrideExpressedConnectionRate(FloatNumber.literal(0.5f))
                        .useWeightFromRandomParentRate(FloatNumber.literal(0.6f))
                        .build())
                .speciation(SpeciationSupport.builder()
                        .maximumSpecies(SPECIATION_SETTINGS_TYPE.maximumSpecies)
                        .weightDifferenceCoefficient(SPECIATION_SETTINGS_TYPE.weightDifferenceCoefficient)
                        .disjointCoefficient(FloatNumber.literal(1f))
                        .excessCoefficient(FloatNumber.literal(1f))
                        .compatibilityThreshold(FloatNumber.literal(3f))
                        .compatibilityThresholdModifier(FloatNumber.literal(1f))
                        .eugenicsThreshold(FloatNumber.literal(0.2f))
                        .elitistThreshold(FloatNumber.literal(0.01f))
                        .elitistThresholdMinimum(IntegerNumber.literal(2))
                        .stagnationDropOffAge(SPECIATION_SETTINGS_TYPE.stagnationDropOffAge)
                        .interSpeciesMatingRate(SPECIATION_SETTINGS_TYPE.interSpeciesMatingRate)
                        .mateOnlyRate(FloatNumber.literal(0.2f))
                        .mutateOnlyRate(FloatNumber.literal(0.25f))
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
                        .maximumGeneration(500)
                        .maximumRestartCount(2)
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
        HALF_DUEL(128, 4, 7),
        RECOMMENDED_DUEL(256, 3, 7);

        private final int populationSize;
        private final int approximateMatchesPerGenome;
        private final int eliminationRounds;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private enum InputTopologySettingsType {
        VALUE_PER_BOARD(IntegerNumber.literal(1),
                ValuePerBoardInputNeuralNetworkEncoder.builder()
                        .perspectiveParticipantId(1)
                        .vectorEncodingType(VECTOR_ENCODING_TYPE)
                        .build()),
        VALUE_PER_PLAYER(IntegerNumber.literal(2),
                ValuePerPlayerInputNeuralNetworkEncoder.builder()
                        .perspectiveParticipantId(1)
                        .vectorEncodingType(VECTOR_ENCODING_TYPE)
                        .build()),
        VALUE_PER_TILE(IntegerNumber.literal(9),
                ValuePerTileInputNeuralNetworkEncoder.builder()
                        .perspectiveParticipantId(1)
                        .build());

        private final IntegerNumber nodeCount;
        private final NeuralNetworkEncoder<GameState> encoder;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private enum OutputTopologySettingsType {
        VANILLA(IntegerNumber.literal(10),
                EnumValue.sequence(Sequence.<OutputActivationFunctionType>builder()
                        .add(1, OutputActivationFunctionType.TAN_H)
                        .add(9, OutputActivationFunctionType.SIGMOID)
                        .build()),
                IdentityNeuronLayerTopologyDefinition.getInstance(),
                AlphaZeroNeuralNetworkDecoder.<GameAction, GameState>builder()
                        .perspectiveParticipantId(1)
                        .behaviorType(PREDICTION_BEHAVIOR_TYPE)
                        .valueIndex(0)
                        .build()),
        DOUBLE(IntegerNumber.literal(20),
                EnumValue.sequence(Sequence.<OutputActivationFunctionType>builder()
                        .add(2, OutputActivationFunctionType.TAN_H)
                        .add(18, OutputActivationFunctionType.SIGMOID)
                        .build()),
                DoubleSolutionNeuronLayerTopologyDefinition.getInstance(),
                AlphaZeroNeuralNetworkDecoder.<GameAction, GameState>builder()
                        .perspectiveParticipantId(1)
                        .behaviorType(PREDICTION_BEHAVIOR_TYPE)
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
        ACTION_SCORE(ActionScoreFitnessObjective.createValueCalculator());

        private final AlphaZeroValueCalculator<GameAction, GameState> reference;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private enum EnvironmentSettingsType {
        WIN_DRAW(new WinOrDrawEnvironment(GAME_SUPPORT)),
        ACTION_SCORE(ActionScoreFitnessObjective.createEnvironment(GAME_SUPPORT));

        private final ContestNeatEnvironment reference;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private enum MutationSettingsType {
        RECOMMENDED_DUEL(FloatNumber.literal(0.0025f), FloatNumber.literal(0.1f), FloatNumber.literal(0.00125f)),
        DOUBLE_DUEL(FloatNumber.literal(0.005f), FloatNumber.literal(0.15f), FloatNumber.literal(0.0025f)),
        RECOMMENDED_MARKOV(FloatNumber.literal(0.03f), FloatNumber.literal(0.06f), FloatNumber.literal(0.015f)),
        DOUBLE_MARKOV(FloatNumber.literal(0.06f), FloatNumber.literal(0.12f), FloatNumber.literal(0.03f));

        private final FloatNumber addNodeRate;
        private final FloatNumber addConnectionRate;
        private final FloatNumber disableExpressedConnectionRate;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private enum SpeciationSettingsType {
        RECOMMENDED_DUEL(IntegerNumber.literal(20), FloatNumber.literal(2f), IntegerNumber.literal(20), FloatNumber.literal(0.05f)),
        RECOMMENDED_MARKOV(IntegerNumber.literal(256), FloatNumber.literal(0.4f), IntegerNumber.literal(15), FloatNumber.literal(0.001f));

        private final IntegerNumber maximumSpecies;
        private final FloatNumber weightDifferenceCoefficient;
        private final IntegerNumber stagnationDropOffAge;
        private final FloatNumber interSpeciesMatingRate;
    }
}
