package com.dipasquale.ai.rl.neat.common.tictactoe;

import com.dipasquale.ai.common.NeuralNetworkEncoder;
import com.dipasquale.ai.common.fitness.AverageFitnessControllerFactory;
import com.dipasquale.ai.rl.neat.ActivationSupport;
import com.dipasquale.ai.rl.neat.ConnectionGeneSupport;
import com.dipasquale.ai.rl.neat.ContestedNeatEnvironment;
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
import com.dipasquale.ai.rl.neat.IsolatedNeatEnvironment;
import com.dipasquale.ai.rl.neat.MetricCollectionType;
import com.dipasquale.ai.rl.neat.MetricCollectorTrainingPolicy;
import com.dipasquale.ai.rl.neat.MetricsSupport;
import com.dipasquale.ai.rl.neat.MutationSupport;
import com.dipasquale.ai.rl.neat.NeatEnvironment;
import com.dipasquale.ai.rl.neat.NeatTrainingPolicy;
import com.dipasquale.ai.rl.neat.NeatTrainingPolicyController;
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
import com.dipasquale.ai.rl.neat.common.TwoPlayerWinRateTrainingAssessor;
import com.dipasquale.ai.rl.neat.function.activation.ActivationFunctionType;
import com.dipasquale.ai.rl.neat.function.activation.OutputActivationFunctionType;
import com.dipasquale.ai.rl.neat.phenotype.DoubleSolutionNeuronLayerTopologyDefinition;
import com.dipasquale.ai.rl.neat.phenotype.GenomeActivator;
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
import com.dipasquale.simulation.tictactoe.GameAction;
import com.dipasquale.simulation.tictactoe.GameState;
import com.dipasquale.simulation.tictactoe.Player;
import com.dipasquale.simulation.tictactoe.encoding.InputPerBoardInputNeuralNetworkEncoder;
import com.dipasquale.simulation.tictactoe.encoding.InputPerPlayerInputNeuralNetworkEncoder;
import com.dipasquale.simulation.tictactoe.encoding.InputPerTileInputNeuralNetworkEncoder;
import com.dipasquale.simulation.tictactoe.encoding.VectorEncodingType;
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
public final class TicTacToeTaskSetup implements TaskSetup {
    private static final int MAXIMUM_EXPANSIONS = 15;
    private static final float ROOT_EXPLORATION_PROBABILITY_NOISE_SHAPE = 0.03f;
    private static final float ROOT_EXPLORATION_PROBABILITY_NOISE_EPSILON = 0.25f;
    private static final RootExplorationProbabilityNoiseType ROOT_EXPLORATION_PROBABILITY_NOISE_TYPE = RootExplorationProbabilityNoiseType.ENABLED;
    private static final BufferType CACHE_TYPE = BufferType.AUTO_CLEAR;
    private static final PopulationSettingsType POPULATION_SETTINGS_TYPE = PopulationSettingsType.VANILLA;
    private static final VectorEncodingType VECTOR_ENCODING_TYPE = VectorEncodingType.INTEGER;
    private static final InputTopologySettingsType INPUT_TOPOLOGY_SETTINGS_TYPE = InputTopologySettingsType.VALUE_PER_PLAYER;
    private static final EnumSet<PredictionBehaviorType> PREDICTION_BEHAVIOR_TYPES = EnumSet.of(PredictionBehaviorType.VALUE_HEURISTIC_ALLOWED_ON_INTENTIONAL_STATES, PredictionBehaviorType.VALUE_REVERSED_ON_OPPONENT, PredictionBehaviorType.POLICY_REVERSED_ON_OPPONENT);
    private static final OutputTopologySettingsType OUTPUT_TOPOLOGY_SETTINGS_TYPE = OutputTopologySettingsType.DOUBLE;
    private static final ValueHeuristicSettingsType VALUE_HEURISTIC_SETTINGS_TYPE = ValueHeuristicSettingsType.NONE;
    private static final float C_PUCT_CONSTANT = 1f;
    private static final CPuctAlgorithmType C_PUCT_ALGORITHM_TYPE = CPuctAlgorithmType.CONSTANT;
    private static final BackPropagationType BACK_PROPAGATION_TYPE = BackPropagationType.REVERSED_ON_OPPONENT;
    private static final int TEMPERATURE_DEPTH_THRESHOLD = 3;
    private static final int CLASSIC_MAXIMUM_SELECTIONS = 30;
    private static final int CLASSIC_MAXIMUM_SIMULATION_ROLLOUT_DEPTH = 9;
    private static final BufferType CLASSIC_CACHE_TYPE = BufferType.AUTO_CLEAR;

    private static final GameSupport GAME_SUPPORT = GameSupport.builder()
            .maximumExpansions(MAXIMUM_EXPANSIONS)
            .rootExplorationProbabilityNoise(ROOT_EXPLORATION_PROBABILITY_NOISE_TYPE.reference)
            .bufferType(CACHE_TYPE)
            .encoder(INPUT_TOPOLOGY_SETTINGS_TYPE.encoder)
            .decoder(OUTPUT_TOPOLOGY_SETTINGS_TYPE.decoder)
            .rewardHeuristic(VALUE_HEURISTIC_SETTINGS_TYPE.reference)
            .explorationHeuristic(null)
            .cpuctAlgorithm(C_PUCT_ALGORITHM_TYPE.reference)
            .backPropagationType(BACK_PROPAGATION_TYPE)
            .temperatureDepthThreshold(TEMPERATURE_DEPTH_THRESHOLD)
            .classicMaximumSelectionCount(CLASSIC_MAXIMUM_SELECTIONS)
            .classicMaximumSimulationDepth(CLASSIC_MAXIMUM_SIMULATION_ROLLOUT_DEPTH)
            .classicBufferType(CLASSIC_CACHE_TYPE)
            .build();

    private static final FitnessFunctionSettingsType FITNESS_FUNCTION_SETTINGS_TYPE = FitnessFunctionSettingsType.ACTION_SCORE;
    private static final int TRAINING_ASSESSOR_MATCHES = 100;
    private static final double TRAINING_ASSESSOR_WIN_RATE = 0.55D;
    private static final TwoPlayerWinRateTrainingAssessor<Player> WIN_RATE_TRAINING_ASSESSOR = new TwoPlayerWinRateTrainingAssessor<>(GAME_SUPPORT, TRAINING_ASSESSOR_MATCHES, TRAINING_ASSESSOR_WIN_RATE);
    private static final MutationSettingsType MUTATION_SETTINGS_TYPE = MutationSettingsType.RECOMMENDED_MARKOV;
    private static final SpeciationSettingsType SPECIATION_SETTINGS_TYPE = SpeciationSettingsType.RECOMMENDED_MARKOV;
    private static final EnvironmentSettingsType ENVIRONMENT_SETTINGS_TYPE = EnvironmentSettingsType.ISOLATED;
    private static final int MAXIMUM_GENERATIONS = 1_000;
    private static final int FITNESS_TEST_COUNT = 6;
    private final String name = "Tic-Tac-Toe";

    private final int populationSize = switch (ENVIRONMENT_SETTINGS_TYPE) {
        case ISOLATED -> POPULATION_SETTINGS_TYPE.isolatedPopulationSize;

        case DUEL -> POPULATION_SETTINGS_TYPE.duelPopulationSize;
    };

    private final boolean metricsEmissionEnabled;

    @Override
    public EvaluatorSettings createSettings(final Set<String> genomeIds, final ParallelEventLoop eventLoop) {
        return EvaluatorSettings.builder()
                .general(GeneralSupport.builder()
                        .populationSize(populationSize)
                        .genesisGenomeTemplate(GenesisGenomeTemplate.builder()
                                .inputs(INPUT_TOPOLOGY_SETTINGS_TYPE.nodeCount)
                                .outputs(OUTPUT_TOPOLOGY_SETTINGS_TYPE.nodeCount)
                                .biases(List.of())
                                .hiddenLayers(List.of(5, 5))
                                .initialConnectionType(InitialConnectionType.FULLY_CONNECTED)
                                .initialWeightType(InitialWeightType.ALL_RANDOM)
                                .build())
                        .fitnessFunction(ENVIRONMENT_SETTINGS_TYPE.factory.create(genomeIds))
                        .fitnessControllerFactory(AverageFitnessControllerFactory.getInstance())
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
                        .maximumGeneration(MAXIMUM_GENERATIONS)
                        .maximumRestartCount(2)
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
                .shape(ROOT_EXPLORATION_PROBABILITY_NOISE_SHAPE)
                .epsilon(ROOT_EXPLORATION_PROBABILITY_NOISE_EPSILON)
                .build());

        private final RootExplorationProbabilityNoiseSettings reference;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private enum PopulationSettingsType {
        LESS_THAN_VANILLA(125, 128, 4, 7),
        VANILLA(150, 256, 3, 7);

        private final int isolatedPopulationSize;
        private final int duelPopulationSize;
        private final int approximateMatchesPerGenome;
        private final int eliminationRounds;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private enum InputTopologySettingsType {
        VALUE_PER_BOARD(1,
                InputPerBoardInputNeuralNetworkEncoder.builder()
                        .perspectiveParticipantId(1)
                        .vectorEncodingType(VECTOR_ENCODING_TYPE)
                        .build()),
        VALUE_PER_PLAYER(2,
                InputPerPlayerInputNeuralNetworkEncoder.builder()
                        .perspectiveParticipantId(1)
                        .vectorEncodingType(VECTOR_ENCODING_TYPE)
                        .build()),
        VALUE_PER_TILE(9,
                InputPerTileInputNeuralNetworkEncoder.builder()
                        .perspectiveParticipantId(1)
                        .build());

        private final int nodeCount;
        private final NeuralNetworkEncoder<GameState> encoder;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private enum OutputTopologySettingsType {
        VANILLA(10,
                EnumValue.sequence(Sequence.<OutputActivationFunctionType>builder()
                        .add(1, OutputActivationFunctionType.TAN_H)
                        .add(9, OutputActivationFunctionType.SIGMOID)
                        .build()),
                IdentityNeuronLayerTopologyDefinition.getInstance(),
                AlphaZeroNeuralNetworkDecoder.<GameAction, GameState, StandardSearchNode<GameAction, AlphaZeroEdge, GameState>>builder()
                        .perspectiveParticipantId(1)
                        .behaviorTypes(PREDICTION_BEHAVIOR_TYPES)
                        .valueIndex(0)
                        .build()),
        VANILLA_WITHOUT_VALUE(9,
                EnumValue.literal(OutputActivationFunctionType.SIGMOID),
                IdentityNeuronLayerTopologyDefinition.getInstance(),
                AlphaZeroNeuralNetworkDecoder.<GameAction, GameState, StandardSearchNode<GameAction, AlphaZeroEdge, GameState>>builder()
                        .perspectiveParticipantId(1)
                        .behaviorTypes(PREDICTION_BEHAVIOR_TYPES)
                        .valueIndex(-1)
                        .build()),
        DOUBLE(20,
                EnumValue.sequence(Sequence.<OutputActivationFunctionType>builder()
                        .add(2, OutputActivationFunctionType.TAN_H)
                        .add(18, OutputActivationFunctionType.SIGMOID)
                        .build()),
                DoubleSolutionNeuronLayerTopologyDefinition.getInstance(),
                AlphaZeroNeuralNetworkDecoder.<GameAction, GameState, StandardSearchNode<GameAction, AlphaZeroEdge, GameState>>builder()
                        .perspectiveParticipantId(1)
                        .behaviorTypes(PREDICTION_BEHAVIOR_TYPES)
                        .valueIndex(0)
                        .build()),
        DOUBLE_WITHOUT_VALUE(18,
                EnumValue.literal(OutputActivationFunctionType.SIGMOID),
                DoubleSolutionNeuronLayerTopologyDefinition.getInstance(),
                AlphaZeroNeuralNetworkDecoder.<GameAction, GameState, StandardSearchNode<GameAction, AlphaZeroEdge, GameState>>builder()
                        .perspectiveParticipantId(1)
                        .behaviorTypes(PREDICTION_BEHAVIOR_TYPES)
                        .valueIndex(-1)
                        .build());

        private final int nodeCount;
        private final EnumValue<OutputActivationFunctionType> activationFunction;
        private final NeuronLayerTopologyDefinition topologyDefinition;
        private final AlphaZeroNeuralNetworkDecoder<GameAction, GameState, StandardSearchNode<GameAction, AlphaZeroEdge, GameState>> decoder;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private enum ValueHeuristicSettingsType {
        NONE(null),
        INDIVIDUAL_ACTION_SCORE(ActionScoreFitnessObjective.createValueHeuristic(false)),
        ACTION_SCORE_VS_OPPONENT(ActionScoreFitnessObjective.createValueHeuristic(true));

        private final RewardHeuristic<GameAction, GameState> reference;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private enum CPuctAlgorithmType {
        CONSTANT((simulations, visited) -> C_PUCT_CONSTANT),
        ROSIN(new RosinCPuctAlgorithm());

        private final CPuctAlgorithm reference;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private enum FitnessFunctionSettingsType {
        WIN_OR_DRAW(WinOrDrawFitnessObjective.createIsolatedEnvironment(GAME_SUPPORT),
                WinOrDrawFitnessObjective.createContestedEnvironment(GAME_SUPPORT)),
        ACTION_SCORE(ActionScoreFitnessObjective.createIsolatedEnvironment(GAME_SUPPORT),
                ActionScoreFitnessObjective.createContestedEnvironment(GAME_SUPPORT));

        private final IsolatedNeatEnvironment isolated;
        private final ContestedNeatEnvironment contested;
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

    @FunctionalInterface
    private interface NeatEnvironmentFactory {
        NeatEnvironment create(Set<String> genomeIds);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private enum EnvironmentSettingsType {
        ISOLATED(genomeIds -> (IsolatedNeatEnvironment) genomeActivator -> {
            genomeIds.add(genomeActivator.getGenome().getId());

            return FITNESS_FUNCTION_SETTINGS_TYPE.isolated.test(genomeActivator);
        }),
        DUEL(genomeIds -> RoundRobinDuelNeatEnvironment.builder()
                .environment((genomeActivators, round) -> {
                    for (GenomeActivator genomeActivator : genomeActivators) {
                        genomeIds.add(genomeActivator.getGenome().getId());
                    }

                    return FITNESS_FUNCTION_SETTINGS_TYPE.contested.test(genomeActivators, round);
                })
                .approximateMatchesPerGenome(POPULATION_SETTINGS_TYPE.approximateMatchesPerGenome)
                .rematches(1)
                .eliminationRounds(POPULATION_SETTINGS_TYPE.eliminationRounds)
                .build());

        private final NeatEnvironmentFactory factory;
    }
}
