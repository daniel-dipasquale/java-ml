package com.dipasquale.ai.rl.neat.core;

import com.dipasquale.ai.common.fitness.AverageFitnessDeterminerFactory;
import com.dipasquale.ai.common.function.activation.ActivationFunctionType;
import com.dipasquale.ai.common.function.activation.OutputActivationFunctionType;
import com.dipasquale.ai.rl.neat.phenotype.GenomeActivator;
import com.dipasquale.ai.rl.neat.phenotype.NeuralNetwork;
import com.dipasquale.ai.rl.neat.phenotype.SubtractionNeuronLayerNormalizer;
import com.dipasquale.common.time.MillisecondsDateTimeSupport;
import com.dipasquale.search.mcts.alphazero.AlphaZeroPrediction;
import com.dipasquale.search.mcts.classic.ClassicPrevalentStrategyCalculator;
import com.dipasquale.search.mcts.classic.ClassicSimulationRolloutType;
import com.dipasquale.search.mcts.core.DeterministicSimulationPolicy;
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
import com.dipasquale.simulation.tictactoe.Player;
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
final class TicTacToeTaskSetup implements TaskSetup {
    private static final int VALIDATION_MATCHES = 2;
    private static final int VALIDATION_MATCH_SIMULATIONS = 10;
    private static final int VALIDATION_MATCH_DEPTH = 2;
    private static final int FITNESS_TEST_COUNT = 2;
    private static final FitnessFunctionType FITNESS_FUNCTION_TYPE = FitnessFunctionType.MOVEMENT_SCORE;

    private static final float[][] MOVEMENT_SCORE_FITNESS_TABLE = { // move, location
            {0.4771311f, 0.43507704f, 0.4771311f, 0.43507704f, 0.54081637f, 0.43507704f, 0.4771311f, 0.43507704f, 0.4771311f},
            {0.3080933f, 0.24144144f, 0.3080933f, 0.24144144f, 0.37584504f, 0.24144144f, 0.3080933f, 0.24144144f, 0.3080933f},
            {0.4771311f, 0.43507704f, 0.4771311f, 0.43507704f, 0.54081637f, 0.43507704f, 0.4771311f, 0.43507704f, 0.4771311f},
            {0.3080933f, 0.24144144f, 0.3080933f, 0.24144144f, 0.37584504f, 0.24144144f, 0.3080933f, 0.24144144f, 0.3080933f},
            {0.4771311f, 0.43507704f, 0.4771311f, 0.43507704f, 0.54081637f, 0.43507704f, 0.4771311f, 0.43507704f, 0.4771311f},
            {0.30972102f, 0.24300519f, 0.30972102f, 0.24300519f, 0.3774007f, 0.24300519f, 0.30972102f, 0.24300519f, 0.30972102f},
            {0.48921052f, 0.43307692f, 0.48921052f, 0.43307692f, 0.5635136f, 0.43307692f, 0.48921052f, 0.43307692f, 0.48921052f},
            {0.37638038f, 0.2597826f, 0.37638038f, 0.2597826f, 0.46595746f, 0.2597826f, 0.37638038f, 0.2597826f, 0.37638038f},
            {0.625f, 0.5f, 0.625f, 0.5f, 0.7026316f, 0.5f, 0.625f, 0.5f, 0.625f}
    };

//    private static final NeatEncoder<GameEnvironment> ENCODER = SingleInputNeatEncoder.builder()
//            .perspectiveParticipantId(1)
//            .vectorFormatEnabled(true)
//            .build();

    private static final NeatEncoder<GameEnvironment> ENCODER = DoubleInputNeatEncoder.builder()
            .perspectiveParticipantId(1)
            .vectorFormatEnabled(true)
            .build();

    private static final NeatDecoder<AlphaZeroPrediction, NeatAlphaZeroHeuristicContext<GameState, GameEnvironment>> DECODER = MultiPerspectiveAlphaZeroNeatDecoder.<GameState, GameEnvironment>builder()
            .perspectiveParticipantId(1)
            .valueIndex(0)
            .build();

    private final String name = "Tic-Tac-Toe";
    private final int populationSize = 256;
    private final boolean metricsEmissionEnabled;

    private static Player createPlayer(final NeuralNetwork neuralNetwork) {
        return MctsPlayer.builder()
                .mcts(MonteCarloTreeSearch.<GameState, GameEnvironment>alphaZeroBuilder()
                        .simulationPolicy(DeterministicSimulationPolicy.builder()
                                .maximumSimulations(400)
                                .maximumDepth(8)
                                .build())
                        .heuristic(new NeatAlphaZeroHeuristic<>(ENCODER, DECODER, neuralNetwork))
                        .build())
                .build();
    }

    private static float[] calculateFitness_wonDrawnRatio(final List<GenomeActivator> genomeActivators) {
        Player player1 = createPlayer(genomeActivators.get(0));
        Player player2 = createPlayer(genomeActivators.get(1));
        GameResult result = Game.play(player1, player2);
        int outcomeId = result.getOutcomeId();
        List<Integer> moves = result.getMoves();
        float averageMoves = (float) (moves.size() / 2);

        if (outcomeId == 0) {
            float player1Effectiveness = 1f - (averageMoves + 1f) / 5f;
            float player2Effectiveness = averageMoves / 4f;

            return new float[]{3f + player1Effectiveness, player2Effectiveness};
        }

        if (outcomeId == 1) {
            float player1Effectiveness = (averageMoves + 1f) / 5f;
            float player2Effectiveness = 1f - averageMoves / 4f;

            return new float[]{player1Effectiveness, 3f + player2Effectiveness};
        }

        float player1Effectiveness = (averageMoves + 1f) / 5f;
        float player2Effectiveness = averageMoves / 4f;

        return new float[]{1f + player1Effectiveness, 1f + player2Effectiveness};
    }

    private static float[] calculateFitness_movementScore(final List<GenomeActivator> genomeActivators) {
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

        player1MovementScore /= (float) player1Moves;
        player2MovementScore /= (float) player2Moves;

        if (outcomeId == 0) {
            return new float[]{3f + player1MovementScore, player2MovementScore};
        }

        if (outcomeId == 1) {
            return new float[]{player1MovementScore, 3f + player2MovementScore};
        }

        return new float[]{1f + player1MovementScore, 1f + player2MovementScore};
    }

    private static Player createClassicMctsPlayer() {
        return MctsPlayer.builder()
                .mcts(MonteCarloTreeSearch.<GameState, GameEnvironment>classicBuilder()
                        .simulationPolicy(DeterministicSimulationPolicy.builder()
                                .maximumSimulations(VALIDATION_MATCH_SIMULATIONS)
                                .maximumDepth(VALIDATION_MATCH_DEPTH)
                                .build())
                        .simulationRolloutType(ClassicSimulationRolloutType.STOCHASTIC_CHOICE_DETERMINISTIC_OUTCOME)
                        .strategyCalculator(ClassicPrevalentStrategyCalculator.builder()
                                .winningFactor(2f)
                                .notLosingFactor(0.5f)
                                .build())
                        .build())
                .build();
    }

    private static boolean determineTrainingResult(final NeatActivator activator) {
        boolean success = true;
        Player player1 = createPlayer(activator);
        Player player2 = createClassicMctsPlayer();

        for (int i = 0; success && i < VALIDATION_MATCHES; i++) {
            if (i % 2 != 0) {
                success = Game.play(player2, player1).getOutcomeId() != 0;
            } else {
                success = Game.play(player1, player2).getOutcomeId() == 0;
            }
        }

        return success;
    }

    @Override
    public EvaluatorSettings createSettings(final Set<String> genomeIds, final IterableEventLoop eventLoop) {
        return EvaluatorSettings.builder()
                .general(GeneralSupport.builder()
                        .populationSize(IntegerNumber.literal(populationSize))
                        .genesisGenomeTemplate(GenesisGenomeTemplate.builder()
                                .inputs(IntegerNumber.literal(2))
                                .outputs(IntegerNumber.literal(20))
                                .biases(List.of())
                                .initialConnectionType(InitialConnectionType.ALL_INPUTS_AND_BIASES_TO_ALL_OUTPUTS)
                                .initialWeightType(InitialWeightType.ALL_RANDOM)
                                .build())
                        .fitnessFunction(RoundRobinDuelNeatEnvironment.builder()
                                .environment(ga -> {
                                    for (GenomeActivator genomeActivator : ga) {
                                        genomeIds.add(genomeActivator.getGenome().getId());
                                    }

                                    return FITNESS_FUNCTION_TYPE.environment.test(ga);
                                })
                                .approximateMatchesPerGenome(3)
                                .rematches(1)
                                .build())
                        .fitnessDeterminerFactory(new AverageFitnessDeterminerFactory())
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
                        .outputActivationFunction(EnumValue.sequence(Sequence.<OutputActivationFunctionType>builder()
                                .add(2, OutputActivationFunctionType.TAN_H)
                                .add(18, OutputActivationFunctionType.SIGMOID)
                                .build()))
                        .hiddenBias(FloatNumber.random(RandomType.QUADRUPLE_STEEPENED_SIGMOID, 30f))
                        .hiddenActivationFunction(EnumValue.literal(ActivationFunctionType.TAN_H))
                        .build())
                .connections(ConnectionGeneSupport.builder()
                        .weightFactory(FloatNumber.random(RandomType.UNIFORM, 0.5f))
                        .weightPerturber(FloatNumber.literal(2.5f))
                        .recurrentStateType(RecurrentStateType.DEFAULT)
                        .recurrentAllowanceRate(FloatNumber.literal(0f))
                        .unrestrictedDirectionAllowanceRate(FloatNumber.literal(1f))
                        .multiCycleAllowanceRate(FloatNumber.literal(0f))
                        .build())
                .activation(ActivationSupport.builder()
                        .outputLayerNormalizer(new SubtractionNeuronLayerNormalizer())
                        .build())
                .mutation(MutationSupport.builder()
                        .addNodeRate(FloatNumber.literal(0.0025f))
                        .addConnectionRate(FloatNumber.literal(0.1f))
                        .perturbWeightRate(FloatNumber.literal(0.75f))
                        .replaceWeightRate(FloatNumber.literal(0.5f))
                        .disableExpressedConnectionRate(FloatNumber.literal(0.015f))
                        .build())
                .crossOver(CrossOverSupport.builder()
                        .overrideExpressedConnectionRate(FloatNumber.literal(0.5f))
                        .useWeightFromRandomParentRate(FloatNumber.literal(0.6f))
                        .build())
                .speciation(SpeciationSupport.builder()
                        .maximumSpecies(IntegerNumber.literal(20))
                        .weightDifferenceCoefficient(FloatNumber.literal(2f))
                        .disjointCoefficient(FloatNumber.literal(1f))
                        .excessCoefficient(FloatNumber.literal(1f))
                        .compatibilityThreshold(FloatNumber.literal(3f))
                        .compatibilityThresholdModifier(FloatNumber.literal(1f))
                        .eugenicsThreshold(FloatNumber.literal(0.2f))
                        .elitistThreshold(FloatNumber.literal(0.01f))
                        .elitistThresholdMinimum(IntegerNumber.literal(2))
                        .stagnationDropOffAge(IntegerNumber.literal(20))
                        .interSpeciesMatingRate(FloatNumber.literal(0.05f))
                        .mateOnlyRate(FloatNumber.literal(0.2f))
                        .mutateOnlyRate(FloatNumber.literal(0.25f))
                        .build())
                .metrics(MetricSupport.builder()
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
                .add(new DelegatedTrainingPolicy(TicTacToeTaskSetup::determineTrainingResult))
                .add(ContinuousTrainingPolicy.builder()
                        .fitnessTestCount(FITNESS_TEST_COUNT)
                        .build())
                .build();
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    private enum FitnessFunctionType {
        WON_DRAWN_RATIO(TicTacToeTaskSetup::calculateFitness_wonDrawnRatio),
        MOVEMENT_SCORE(TicTacToeTaskSetup::calculateFitness_movementScore);
        private final ContestNeatEnvironment environment;
    }
}
