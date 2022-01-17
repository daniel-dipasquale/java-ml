package com.dipasquale.ai.rl.neat.core;

import com.dipasquale.ai.common.fitness.AverageFitnessDeterminerFactory;
import com.dipasquale.ai.common.function.activation.ActivationFunctionType;
import com.dipasquale.ai.common.function.activation.OutputActivationFunctionType;
import com.dipasquale.ai.rl.neat.phenotype.GenomeActivator;
import com.dipasquale.ai.rl.neat.phenotype.IdentityNeuronLayerNormalizer;
import com.dipasquale.ai.rl.neat.phenotype.NeuralNetwork;
import com.dipasquale.ai.rl.neat.phenotype.NeuronLayerNormalizer;
import com.dipasquale.ai.rl.neat.phenotype.TwoSolutionNeuronLayerNormalizer;
import com.dipasquale.common.time.MillisecondsDateTimeSupport;
import com.dipasquale.search.mcts.alphazero.AlphaZeroPrediction;
import com.dipasquale.search.mcts.classic.ClassicConfidenceCalculator;
import com.dipasquale.search.mcts.classic.ClassicPrevalentStrategyCalculator;
import com.dipasquale.search.mcts.core.DeterministicSearchPolicy;
import com.dipasquale.search.mcts.core.MonteCarloTreeSearch;
import com.dipasquale.simulation.mcts.alphazero.MultiPerspectiveAlphaZeroNeatDecoder;
import com.dipasquale.simulation.mcts.alphazero.NeatAlphaZeroHeuristic;
import com.dipasquale.simulation.mcts.alphazero.NeatAlphaZeroHeuristicContext;
import com.dipasquale.simulation.tictactoe.DoubleInputNeatEncoder;
import com.dipasquale.simulation.tictactoe.Game;
import com.dipasquale.simulation.tictactoe.GameEnvironment;
import com.dipasquale.simulation.tictactoe.GameResult;
import com.dipasquale.simulation.tictactoe.GameState;
import com.dipasquale.simulation.tictactoe.MctsPlayer;
import com.dipasquale.simulation.tictactoe.PlainInputNeatEncoder;
import com.dipasquale.simulation.tictactoe.Player;
import com.dipasquale.simulation.tictactoe.SingleInputNeatEncoder;
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
    private static final PopulationSettingsType POPULATION_SETTINGS_TYPE = PopulationSettingsType.SINGLE_DUEL;
    private static final InputTopologySettingsType INPUT_TOPOLOGY_SETTINGS_TYPE = InputTopologySettingsType.PLAIN;
    private static final OutputTopologySettingsType OUTPUT_TOPOLOGY_SETTINGS_TYPE = OutputTopologySettingsType.DOUBLE_SUB_SET;
    private static final FitnessFunctionType FITNESS_FUNCTION_TYPE = FitnessFunctionType.WON_DRAWN_MOVEMENT_SCORE_OR_EFFICIENCY;
    private static final MutationSettingsType MUTATION_SETTINGS_TYPE = MutationSettingsType.SINGLE_MARKOV;
    private static final SpeciationSettingsType SPECIATION_SETTINGS_TYPE = SpeciationSettingsType.SINGLE_MARKOV;
    private static final int VALIDATION_MATCHES = 10;
    private static final int VALIDATION_MATCH_SIMULATIONS = 200;
    private static final int VALIDATION_MATCH_DEPTH = 8;
    private static final int FITNESS_TEST_COUNT = 4;
    private static final float[][] MOVEMENT_SCORE_FITNESS_TABLE = createMovementScoreFitnessTable();
    private final String name = "Tic-Tac-Toe";
    private final int populationSize = POPULATION_SETTINGS_TYPE.populationSize;
    private final boolean metricsEmissionEnabled;

    private static float getValue(final float value, final float minimum, final float maximum) {
        return (value - minimum) / (maximum - minimum);
    }

    private static float[][] createMovementScoreFitnessTable() {
        float[][] table = { // move, location
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
                tableFixed[i1][i2] = getValue(table[i1][i2], minimum, maximum);
            }
        }

        return tableFixed;
    }

    private static Player createPlayer(final NeuralNetwork neuralNetwork) {
        return MctsPlayer.builder()
                .mcts(MonteCarloTreeSearch.<GameState, GameEnvironment>alphaZeroBuilder()
                        .searchPolicy(DeterministicSearchPolicy.builder()
                                .maximumSimulations(81)
                                .maximumDepth(9)
                                .build())
                        .heuristic(new NeatAlphaZeroHeuristic<>(INPUT_TOPOLOGY_SETTINGS_TYPE.encoder, OUTPUT_TOPOLOGY_SETTINGS_TYPE.decoder, neuralNetwork))
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

    private static float[] calculateFitness_wonDrawn_movementScore_or_efficiency(final List<GenomeActivator> genomeActivators, final int round) {
        Player player1 = createPlayer(genomeActivators.get(0));
        Player player2 = createPlayer(genomeActivators.get(1));
        GameResult result = Game.play(player1, player2);
        int outcomeId = result.getOutcomeId();
        List<Integer> moves = result.getMoves();
        float player1MovementScore = 0f;
        int player1Moves = 0;
        float player2MovementScore = 0f;
        int player2Moves = 0;

        for (int i = 0, c = moves.size(); i < c; i++) {
            float moveScore = MOVEMENT_SCORE_FITNESS_TABLE[i][moves.get(i)];

            if (i % 2 == 0) {
                player1MovementScore += moveScore;
                player1Moves++;
            } else {
                player2MovementScore += moveScore;
                player2Moves++;
            }
        }

        float player1MovesFixed = (float) player1Moves;
        float player2MovesFixed = (float) player2Moves;

        player1MovementScore /= player1MovesFixed;
        player2MovementScore /= player2MovesFixed;

        if (outcomeId == 0) {
            float player1Effectiveness = 1f - player1MovesFixed / 5f;

            return new float[]{3f + player1MovementScore + player1Effectiveness, player2MovementScore};
        }

        if (outcomeId == 1) {
            float player2Effectiveness = 1f - player2MovesFixed / 4f;

            return new float[]{player1MovementScore, 3f + player2MovementScore + player2Effectiveness};
        }

        return new float[]{1f + player1MovementScore, 1f + player2MovementScore};
    }

    private static Player createClassicMctsPlayer() {
        return MctsPlayer.builder()
                .mcts(MonteCarloTreeSearch.<GameState, GameEnvironment>classicBuilder()
                        .searchPolicy(DeterministicSearchPolicy.builder()
                                .maximumSimulations(VALIDATION_MATCH_SIMULATIONS)
                                .maximumDepth(VALIDATION_MATCH_DEPTH)
                                .build())
                        .confidenceCalculator(new ClassicConfidenceCalculator())
                        .strategyCalculator(ClassicPrevalentStrategyCalculator.builder()
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
                    case -1, 1 -> won++;
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
                        .outputLayerNormalizer(OUTPUT_TOPOLOGY_SETTINGS_TYPE.outputLayerNormalizer)
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
                        .maximumGeneration(1_000_000_000)
                        .maximumRestartCount(0)
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
        SINGLE_DUEL(256, 3, 7);

        private final int populationSize;
        private final int approximateMatchesPerGenome;
        private final int eliminationRounds;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private enum InputTopologySettingsType {
        SINGLE_VECTOR(IntegerNumber.literal(1),
                SingleInputNeatEncoder.builder()
                        .perspectiveParticipantId(1)
                        .vectorFormatEnabled(true)
                        .build()),
        SINGLE_CASTED(IntegerNumber.literal(1),
                SingleInputNeatEncoder.builder()
                        .perspectiveParticipantId(1)
                        .vectorFormatEnabled(false)
                        .build()),
        DOUBLE_VECTOR(IntegerNumber.literal(2),
                DoubleInputNeatEncoder.builder()
                        .perspectiveParticipantId(1)
                        .vectorFormatEnabled(true)
                        .build()),
        DOUBLE_CASTED(IntegerNumber.literal(2),
                DoubleInputNeatEncoder.builder()
                        .perspectiveParticipantId(1)
                        .vectorFormatEnabled(false)
                        .build()),
        PLAIN(IntegerNumber.literal(9),
                PlainInputNeatEncoder.builder()
                        .perspectiveParticipantId(1)
                        .build());

        private final IntegerNumber inputs;
        private final NeatEncoder<GameEnvironment> encoder;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private enum OutputTopologySettingsType {
        SINGLE_EXACT_SET(IntegerNumber.literal(10),
                EnumValue.sequence(Sequence.<OutputActivationFunctionType>builder()
                        .add(1, OutputActivationFunctionType.TAN_H)
                        .add(9, OutputActivationFunctionType.SIGMOID)
                        .build()),
                IdentityNeuronLayerNormalizer.getInstance(),
                MultiPerspectiveAlphaZeroNeatDecoder.<GameState, GameEnvironment>builder()
                        .perspectiveParticipantId(1)
                        .valueIndex(0)
                        .build()),
        DOUBLE_EXACT_SET(IntegerNumber.literal(20),
                EnumValue.sequence(Sequence.<OutputActivationFunctionType>builder()
                        .add(2, OutputActivationFunctionType.TAN_H)
                        .add(18, OutputActivationFunctionType.SIGMOID)
                        .build()),
                TwoSolutionNeuronLayerNormalizer.getInstance(),
                MultiPerspectiveAlphaZeroNeatDecoder.<GameState, GameEnvironment>builder()
                        .perspectiveParticipantId(1)
                        .valueIndex(0)
                        .build()),
        DOUBLE_SUB_SET(IntegerNumber.literal(8),
                EnumValue.sequence(Sequence.<OutputActivationFunctionType>builder()
                        .add(2, OutputActivationFunctionType.TAN_H)
                        .add(6, OutputActivationFunctionType.SIGMOID)
                        .build()),
                TwoSolutionNeuronLayerNormalizer.getInstance(),
                MultiPerspectiveAlphaZeroNeatDecoder.<GameState, GameEnvironment>builder()
                        .perspectiveParticipantId(1)
                        .valueIndex(0)
                        .build());

        private final IntegerNumber outputs;
        private final EnumValue<OutputActivationFunctionType> outputActivationFunction;
        private final NeuronLayerNormalizer outputLayerNormalizer;
        private final NeatDecoder<AlphaZeroPrediction, NeatAlphaZeroHeuristicContext<GameState, GameEnvironment>> decoder;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private enum FitnessFunctionType {
        WON_DRAWN(TicTacToeDuelTaskSetup::calculateFitness_wonDrawn),
        WON_DRAWN_MOVEMENT_SCORE_OR_EFFICIENCY(TicTacToeDuelTaskSetup::calculateFitness_wonDrawn_movementScore_or_efficiency);

        private final ContestNeatEnvironment environment;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private enum MutationSettingsType {
        SINGLE_DUEL(FloatNumber.literal(0.0025f), FloatNumber.literal(0.1f), FloatNumber.literal(0.00125f)),
        DOUBLE_DUEL(FloatNumber.literal(0.005f), FloatNumber.literal(0.15f), FloatNumber.literal(0.0025f)),
        SINGLE_MARKOV(FloatNumber.literal(0.03f), FloatNumber.literal(0.06f), FloatNumber.literal(0.015f)),
        DOUBLE_MARKOV(FloatNumber.literal(0.06f), FloatNumber.literal(0.12f), FloatNumber.literal(0.03f));

        private final FloatNumber addNodeRate;
        private final FloatNumber addConnectionRate;
        private final FloatNumber disableExpressedConnectionRate;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private enum SpeciationSettingsType {
        SINGLE_DUEL(IntegerNumber.literal(20), FloatNumber.literal(2f), IntegerNumber.literal(20), FloatNumber.literal(0.05f)),
        SINGLE_MARKOV(IntegerNumber.literal(256), FloatNumber.literal(0.4f), IntegerNumber.literal(15), FloatNumber.literal(0.001f));

        private final IntegerNumber maximumSpecies;
        private final FloatNumber weightDifferenceCoefficient;
        private final IntegerNumber stagnationDropOffAge;
        private final FloatNumber interSpeciesMatingRate;
    }
}
