package com.dipasquale.ai.rl.neat.core;

import com.dipasquale.ai.common.fitness.AverageFitnessDeterminerFactory;
import com.dipasquale.ai.rl.neat.function.activation.ActivationFunctionType;
import com.dipasquale.ai.rl.neat.function.activation.OutputActivationFunctionType;
import com.dipasquale.ai.rl.neat.phenotype.DoubleSolutionNeuronLayerTopologyDefinition;
import com.dipasquale.ai.rl.neat.phenotype.GenomeActivator;
import com.dipasquale.ai.rl.neat.phenotype.IdentityNeuronLayerTopologyDefinition;
import com.dipasquale.ai.rl.neat.phenotype.NeuralNetwork;
import com.dipasquale.ai.rl.neat.phenotype.NeuronLayerTopologyDefinition;
import com.dipasquale.common.time.MillisecondsDateTimeSupport;
import com.dipasquale.search.mcts.alphazero.AlphaZeroPrediction;
import com.dipasquale.search.mcts.alphazero.MostVisitedProposalStrategy;
import com.dipasquale.search.mcts.alphazero.TemperatureCalculator;
import com.dipasquale.search.mcts.classic.ClassicConfidenceCalculator;
import com.dipasquale.search.mcts.classic.PrevalentProposalStrategy;
import com.dipasquale.search.mcts.core.MaximumSearchPolicy;
import com.dipasquale.search.mcts.core.MonteCarloTreeSearch;
import com.dipasquale.search.mcts.core.SearchNodeCacheSettings;
import com.dipasquale.search.mcts.core.SearchNodeProviderSettings;
import com.dipasquale.simulation.mcts.alphazero.MultiPerspectiveAlphaZeroNeatDecoder;
import com.dipasquale.simulation.mcts.alphazero.NeatAlphaZeroHeuristic;
import com.dipasquale.simulation.mcts.alphazero.NeatAlphaZeroHeuristicContext;
import com.dipasquale.simulation.tictactoe.Game;
import com.dipasquale.simulation.tictactoe.GameAction;
import com.dipasquale.simulation.tictactoe.GameResult;
import com.dipasquale.simulation.tictactoe.GameState;
import com.dipasquale.simulation.tictactoe.MctsPlayer;
import com.dipasquale.simulation.tictactoe.Player;
import com.dipasquale.simulation.tictactoe.ValuePerBoardInputNeatEncoder;
import com.dipasquale.simulation.tictactoe.ValuePerPlayerInputNeatEncoder;
import com.dipasquale.simulation.tictactoe.ValuePerSquareInputNeatEncoder;
import com.dipasquale.simulation.tictactoe.VectorEncodingType;
import com.dipasquale.synchronization.event.loop.IterableEventLoop;
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
final class TicTacToeDuelTaskSetup implements TaskSetup {
    private static final PopulationSettingsType POPULATION_SETTINGS_TYPE = PopulationSettingsType.RECOMMENDED_DUEL;
    private static final VectorEncodingType VECTOR_ENCODING_TYPE = VectorEncodingType.INTEGER;
    private static final InputTopologySettingsType INPUT_TOPOLOGY_SETTINGS_TYPE = InputTopologySettingsType.VALUE_PER_BOARD;
    private static final boolean INVERSE_OUTPUT_FOR_OPPONENT = false;
    private static final OutputTopologySettingsType OUTPUT_TOPOLOGY_SETTINGS_TYPE = OutputTopologySettingsType.DOUBLE_MATCHING_SET;
    private static final FitnessFunctionType FITNESS_FUNCTION_TYPE = FitnessFunctionType.WON_DRAWN_ACTION_SCORE_OR_EFFICIENCY;
    private static final MutationSettingsType MUTATION_SETTINGS_TYPE = MutationSettingsType.RECOMMENDED_MARKOV;
    private static final SpeciationSettingsType SPECIATION_SETTINGS_TYPE = SpeciationSettingsType.RECOMMENDED_MARKOV;
    private static final int TRAINING_MATCH_MAXIMUM_SIMULATIONS = 30;
    private static final int TRAINING_MATCH_MAXIMUM_DEPTH = 9;
    private static final boolean TRAINING_ALLOW_ROOT_EXPLORATION_NOISE = false;
    private static final float TRAINING_INITIAL_TEMPERATURE = 0.001f;
    private static final float TRAINING_FINAL_TEMPERATURE = 1f;
    private static final int TRAINING_TEMPERATURE_SIMULATION_THRESHOLD = 10;
    private static final int VALIDATION_MATCHES = 100;
    private static final int VALIDATION_MATCH_MAXIMUM_SIMULATIONS = 1_600;
    private static final int VALIDATION_MATCH_MAXIMUM_DEPTH = 9;
    private static final int FITNESS_TEST_COUNT = 4;
    private static final float[][] ACTION_SCORE_FITNESS_TABLE = createActionScoreFitnessTable();
    private final String name = "Tic-Tac-Toe";
    private final int populationSize = POPULATION_SETTINGS_TYPE.populationSize;
    private final boolean metricsEmissionEnabled;

    private static float calculateValue(final float value, final float minimum, final float maximum) {
        return (value - minimum) / (maximum - minimum);
    }

    private static float[][] createActionScoreFitnessTable() {
        float[][] table = { // x: move, y: location
                {0.24361748f, 0.13706407f, 0.24361748f, 0.13706407f, 0.3877551f, 0.13706407f, 0.24361748f, 0.13706407f, 0.24361748f},
                {-0.16081564f, -0.3153153f, -0.16081564f, -0.3153153f, 0.012366035f, -0.3153153f, -0.16081564f, -0.3153153f, -0.16081564f},
                {0.24361748f, 0.13706407f, 0.24361748f, 0.13706407f, 0.3877551f, 0.13706407f, 0.24361748f, 0.13706407f, 0.24361748f},
                {-0.16081564f, -0.3153153f, -0.16081564f, -0.3153153f, 0.012366035f, -0.3153153f, -0.16081564f, -0.3153153f, -0.16081564f},
                {0.24361748f, 0.13706407f, 0.24361748f, 0.13706407f, 0.3877551f, 0.13706407f, 0.24361748f, 0.13706407f, 0.24361748f},
                {-0.15638208f, -0.31088084f, -0.15638208f, -0.31088084f, 0.01655629f, -0.31088084f, -0.15638208f, -0.31088084f, -0.15638208f},
                {0.2736842f, 0.13846155f, 0.2736842f, 0.13846155f, 0.43783784f, 0.13846155f, 0.2736842f, 0.13846155f, 0.2736842f},
                {0.024539877f, -0.22463769f, 0.024539877f, -0.22463769f, 0.23404256f, -0.22463769f, 0.024539877f, -0.22463769f, 0.024539877f},
                {0.6785714f, 0.5f, 0.6785714f, 0.5f, 0.7894737f, 0.5f, 0.6785714f, 0.5f, 0.6785714f}
        };

        float[][] tableFixed = new float[table.length][table[0].length];

        for (int i1 = 0, c1 = table.length, c2 = table[0].length; i1 < c1; i1++) {
            float minimum = Float.MAX_VALUE;
            float maximum = -Float.MAX_VALUE;

            for (int i2 = 0; i2 < c2; i2++) {
                float value = table[i1][i2];

                if (Float.compare(value, minimum) < 0) {
                    minimum = value;
                }

                if (Float.compare(value, maximum) > 0) {
                    maximum = value;
                }
            }

            for (int i2 = 0; i2 < c2; i2++) {
                tableFixed[i1][i2] = calculateValue(table[i1][i2], minimum, maximum);
            }
        }

        return tableFixed;
    }

    private static Player createPlayer(final NeuralNetwork neuralNetwork) {
        return MctsPlayer.builder()
                .mcts(MonteCarloTreeSearch.<GameAction, GameState>alphaZeroBuilder()
                        .searchPolicy(MaximumSearchPolicy.builder()
                                .maximumSimulations(TRAINING_MATCH_MAXIMUM_SIMULATIONS)
                                .maximumDepth(TRAINING_MATCH_MAXIMUM_DEPTH)
                                .build())
                        .searchNodeProviderSettings(SearchNodeProviderSettings.builder()
                                .allowRootExplorationNoise(TRAINING_ALLOW_ROOT_EXPLORATION_NOISE)
                                .build())
                        .heuristic(new NeatAlphaZeroHeuristic<>(INPUT_TOPOLOGY_SETTINGS_TYPE.encoder, OUTPUT_TOPOLOGY_SETTINGS_TYPE.decoder, neuralNetwork))
                        .proposalStrategy(MostVisitedProposalStrategy.builder()
                                .temperatureCalculator(TemperatureCalculator.builder()
                                        .initialValue(TRAINING_INITIAL_TEMPERATURE)
                                        .finalValue(TRAINING_FINAL_TEMPERATURE)
                                        .simulationThreshold(TRAINING_TEMPERATURE_SIMULATION_THRESHOLD)
                                        .build())
                                .build())
                        .build())
                .build();
    }

    private static float[] calculateFitness_wonDrawn(final List<GenomeActivator> genomeActivators, final int round) {
        Player player1 = createPlayer(genomeActivators.get(0));
        Player player2 = createPlayer(genomeActivators.get(1));
        GameResult result = Game.play(player1, player2);
        int outcomeId = result.getOutcomeId();

        if (outcomeId == 0) {
            return new float[]{3f, 0f};
        }

        if (outcomeId == 1) {
            return new float[]{0f, 3f};
        }

        return new float[]{1f, 1f};
    }

    private static float[] calculateFitness_wonDrawn_actionScore_or_efficiency(final List<GenomeActivator> genomeActivators, final int round) {
        Player player1 = createPlayer(genomeActivators.get(0));
        Player player2 = createPlayer(genomeActivators.get(1));
        GameResult result = Game.play(player1, player2);
        int outcomeId = result.getOutcomeId();
        List<Integer> actionIds = result.getActionIds();
        float player1ActionScore = 0f;
        int player1Actions = 0;
        float player2ActionScore = 0f;
        int player2Actions = 0;

        for (int i = 0, c = actionIds.size(); i < c; i++) {
            float actionScore = ACTION_SCORE_FITNESS_TABLE[i][actionIds.get(i)];

            if (i % 2 == 0) {
                player1ActionScore += actionScore;
                player1Actions++;
            } else {
                player2ActionScore += actionScore;
                player2Actions++;
            }
        }

        float player1ActionsFixed = (float) player1Actions;
        float player2ActionsFixed = (float) player2Actions;

        player1ActionScore /= player1ActionsFixed;
        player2ActionScore /= player2ActionsFixed;

        if (outcomeId == 0) {
            float player1Effectiveness = 1f - player1ActionsFixed / 5f;

            return new float[]{3f + player1ActionScore + player1Effectiveness, player2ActionScore};
        }

        if (outcomeId == 1) {
            float player2Effectiveness = 1f - player2ActionsFixed / 4f;

            return new float[]{player1ActionScore, 3f + player2ActionScore + player2Effectiveness};
        }

        return new float[]{1f + player1ActionScore, 1f + player2ActionScore};
    }

    private static Player createClassicMctsPlayer() {
        return MctsPlayer.builder()
                .mcts(MonteCarloTreeSearch.<GameAction, GameState>classicBuilder()
                        .searchPolicy(MaximumSearchPolicy.builder()
                                .maximumSimulations(VALIDATION_MATCH_MAXIMUM_SIMULATIONS)
                                .maximumDepth(VALIDATION_MATCH_MAXIMUM_DEPTH)
                                .build())
                        .searchNodeCacheSettings(SearchNodeCacheSettings.builder()
                                .participants(2)
                                .build())
                        .confidenceCalculator(new ClassicConfidenceCalculator())
                        .proposalStrategy(PrevalentProposalStrategy.builder()
                                .winningFactor(2f)
                                .notLosingFactor(0.5f)
                                .build())
                        .build())
                .build();
    }

    private static boolean determineTrainingResult(final NeatActivator activator) {
        Player player1 = createPlayer(activator);
        Player player2 = createClassicMctsPlayer();
        int won = 0;
        int expectedWins = (int) Math.ceil((double) VALIDATION_MATCHES * 0.55D);

        for (int i = 0; won < expectedWins && i - won < expectedWins; i++) {
            if (i % 2 == 0) {
                switch (Game.play(player1, player2).getOutcomeId()) {
                    case 0 -> won++;
                }
            } else {
                switch (Game.play(player2, player1).getOutcomeId()) {
                    case 1 -> won++;
                }
            }
        }

        return won == expectedWins;
    }

    @Override
    public EvaluatorSettings createSettings(final Set<String> genomeIds, final IterableEventLoop eventLoop) {
        return EvaluatorSettings.builder()
                .general(GeneralSupport.builder()
                        .populationSize(IntegerNumber.literal(populationSize))
                        .genesisGenomeTemplate(GenesisGenomeTemplate.builder()
                                .inputs(INPUT_TOPOLOGY_SETTINGS_TYPE.inputs)
                                .outputs(OUTPUT_TOPOLOGY_SETTINGS_TYPE.outputs)
                                .biases(List.of())
                                .initialConnectionType(InitialConnectionType.ALL_INPUTS_AND_BIASES_TO_ALL_OUTPUTS)
                                .initialWeightType(InitialWeightType.ALL_RANDOM)
                                .build())
                        .fitnessFunction(RoundRobinDuelNeatEnvironment.builder()
                                .environment((genomeActivators, round) -> {
                                    for (GenomeActivator genomeActivator : genomeActivators) {
                                        genomeIds.add(genomeActivator.getGenome().getId());
                                    }

                                    return FITNESS_FUNCTION_TYPE.environment.test(genomeActivators, round);
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
                        .outputActivationFunction(OUTPUT_TOPOLOGY_SETTINGS_TYPE.outputActivationFunction)
                        .hiddenBias(FloatNumber.random(RandomType.QUADRUPLE_STEEPENED_SIGMOID, 30f))
                        .hiddenActivationFunction(EnumValue.literal(ActivationFunctionType.SIGMOID))
                        .build())
                .connections(ConnectionGeneSupport.builder()
                        .weightFactory(FloatNumber.random(RandomType.UNIFORM, 0.5f))
                        .weightPerturber(FloatNumber.literal(2.5f))
                        .recurrentStateType(RecurrentStateType.DEFAULT)
                        .recurrentAllowanceRate(FloatNumber.literal(0f))
                        .unrestrictedDirectionAllowanceRate(FloatNumber.literal(0.5f))
                        .multiCycleAllowanceRate(FloatNumber.literal(0f))
                        .build())
                .activation(ActivationSupport.builder()
                        .outputTopologyDefinition(OUTPUT_TOPOLOGY_SETTINGS_TYPE.outputTopologyDefinition)
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
                .add(new DelegatedTrainingPolicy(TicTacToeDuelTaskSetup::determineTrainingResult))
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
                ValuePerBoardInputNeatEncoder.builder()
                        .perspectiveParticipantId(1)
                        .vectorEncodingType(VECTOR_ENCODING_TYPE)
                        .build()),
        VALUE_PER_PLAYER(IntegerNumber.literal(2),
                ValuePerPlayerInputNeatEncoder.builder()
                        .perspectiveParticipantId(1)
                        .vectorEncodingType(VECTOR_ENCODING_TYPE)
                        .build()),
        VALUE_PER_SQUARE(IntegerNumber.literal(9),
                ValuePerSquareInputNeatEncoder.builder()
                        .perspectiveParticipantId(1)
                        .build());

        private final IntegerNumber inputs;
        private final NeatEncoder<GameState> encoder;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private enum OutputTopologySettingsType {
        VANILLA_MATCHING_SET(IntegerNumber.literal(10),
                EnumValue.sequence(Sequence.<OutputActivationFunctionType>builder()
                        .add(1, OutputActivationFunctionType.TAN_H)
                        .add(9, OutputActivationFunctionType.SIGMOID)
                        .build()),
                IdentityNeuronLayerTopologyDefinition.getInstance(),
                MultiPerspectiveAlphaZeroNeatDecoder.<GameAction, GameState>builder()
                        .perspectiveParticipantId(1)
                        .inverseOutputForOpponent(INVERSE_OUTPUT_FOR_OPPONENT)
                        .valueIndex(0)
                        .build()),
        DOUBLE_MATCHING_SET(IntegerNumber.literal(20),
                EnumValue.sequence(Sequence.<OutputActivationFunctionType>builder()
                        .add(2, OutputActivationFunctionType.TAN_H)
                        .add(18, OutputActivationFunctionType.SIGMOID)
                        .build()),
                DoubleSolutionNeuronLayerTopologyDefinition.getInstance(),
                MultiPerspectiveAlphaZeroNeatDecoder.<GameAction, GameState>builder()
                        .perspectiveParticipantId(1)
                        .inverseOutputForOpponent(INVERSE_OUTPUT_FOR_OPPONENT)
                        .valueIndex(0)
                        .build()),
        DOUBLE_POOLING_SET(IntegerNumber.literal(8),
                EnumValue.sequence(Sequence.<OutputActivationFunctionType>builder()
                        .add(2, OutputActivationFunctionType.TAN_H)
                        .add(6, OutputActivationFunctionType.SIGMOID)
                        .build()),
                DoubleSolutionNeuronLayerTopologyDefinition.getInstance(),
                MultiPerspectiveAlphaZeroNeatDecoder.<GameAction, GameState>builder()
                        .perspectiveParticipantId(1)
                        .inverseOutputForOpponent(INVERSE_OUTPUT_FOR_OPPONENT)
                        .valueIndex(0)
                        .build());

        private final IntegerNumber outputs;
        private final EnumValue<OutputActivationFunctionType> outputActivationFunction;
        private final NeuronLayerTopologyDefinition outputTopologyDefinition;
        private final NeatDecoder<AlphaZeroPrediction<GameAction, GameState>, NeatAlphaZeroHeuristicContext<GameAction, GameState>> decoder;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private enum FitnessFunctionType {
        WON_DRAWN(TicTacToeDuelTaskSetup::calculateFitness_wonDrawn),
        WON_DRAWN_ACTION_SCORE_OR_EFFICIENCY(TicTacToeDuelTaskSetup::calculateFitness_wonDrawn_actionScore_or_efficiency);

        private final ContestNeatEnvironment environment;
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
