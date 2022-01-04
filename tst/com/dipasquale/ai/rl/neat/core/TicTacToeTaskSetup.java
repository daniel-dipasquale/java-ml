package com.dipasquale.ai.rl.neat.core;

import com.dipasquale.ai.common.fitness.LastValueFitnessDeterminerFactory;
import com.dipasquale.ai.common.function.activation.ActivationFunctionType;
import com.dipasquale.ai.common.function.activation.OutputActivationFunctionType;
import com.dipasquale.ai.rl.neat.phenotype.GenomeActivator;
import com.dipasquale.ai.rl.neat.phenotype.NeuralNetwork;
import com.dipasquale.search.mcts.alphazero.AlphaZeroPrediction;
import com.dipasquale.search.mcts.classic.ClassicPrevalentStrategyCalculator;
import com.dipasquale.search.mcts.classic.ClassicSimulationRolloutType;
import com.dipasquale.search.mcts.core.DeterministicSimulationPolicy;
import com.dipasquale.search.mcts.core.MonteCarloTreeSearch;
import com.dipasquale.simulation.mcts.alphazero.MultiPerspectiveAlphaZeroNeatDecoder;
import com.dipasquale.simulation.mcts.alphazero.NeatAlphaZeroHeuristic;
import com.dipasquale.simulation.mcts.alphazero.NeatAlphaZeroHeuristicContext;
import com.dipasquale.simulation.tictactoe.Game;
import com.dipasquale.simulation.tictactoe.GameEnvironment;
import com.dipasquale.simulation.tictactoe.GameState;
import com.dipasquale.simulation.tictactoe.MctsPlayer;
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
final class TicTacToeTaskSetup implements TaskSetup {
    private static final int MATCHES = 100;

    private static final NeatEncoder<GameEnvironment> ENCODER = SingleInputNeatEncoder.builder()
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
                                .maximumSimulation(81)
                                .maximumDepth(8)
                                .build())
                        .heuristic(new NeatAlphaZeroHeuristic<>(ENCODER, DECODER, neuralNetwork))
                        .build())
                .build();
    }

    private static Player createClassicMctsPlayer() {
        return MctsPlayer.builder()
                .mcts(MonteCarloTreeSearch.<GameState, GameEnvironment>classicBuilder()
                        .simulationPolicy(DeterministicSimulationPolicy.builder()
                                .maximumSimulation(1_600)
                                .maximumDepth(8)
                                .build())
                        .simulationRolloutType(ClassicSimulationRolloutType.STOCHASTIC_CHOICE_DETERMINISTIC_OUTCOME)
                        .strategyCalculator(ClassicPrevalentStrategyCalculator.builder()
                                .winningFactor(2f)
                                .notLosingFactor(0.5f)
                                .build())
                        .build())
                .build();
    }

    private static float[] calculateFitness(final GenomeActivator[] genomeActivators) {
        Player player1 = createPlayer(genomeActivators[0]);
        Player player2 = createPlayer(genomeActivators[1]);
        int statusId = Game.play(player1, player2);

        if (statusId == 0) {
            return new float[]{3f, 0f};
        }

        if (statusId == 1) {
            return new float[]{0f, 3f};
        }

        return new float[]{1f, 1f};
    }

    private static boolean determineTrainingResult(final NeatActivator activator) {
        boolean success = true;
        Player player1 = createPlayer(activator);
        Player player2 = createClassicMctsPlayer();

        for (int i = 0; success && i < MATCHES; i++) {
            if (i % 2 != 0) {
                success = Game.play(player2, player1) != 0;
            } else {
                success = Game.play(player1, player2) == 0;
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
                                .inputs(IntegerNumber.literal(1))
                                .outputs(IntegerNumber.literal(10))
                                .biases(List.of())
                                .initialConnectionType(InitialConnectionType.ALL_INPUTS_AND_BIASES_TO_ALL_OUTPUTS)
                                .initialWeightType(InitialWeightType.ALL_RANDOM)
                                .build())
                        .fitnessFunction(RoundRobinDuelNeatEnvironment.builder()
                                .environment(ga -> {
                                    for (GenomeActivator genomeActivator : ga) {
                                        genomeIds.add(genomeActivator.getGenome().getId());
                                    }

                                    return calculateFitness(ga);
                                })
                                .matchesRate(0.3f)
                                .rematches(1)
                                .build())
                        .fitnessDeterminerFactory(new LastValueFitnessDeterminerFactory())
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
                                .add(1, OutputActivationFunctionType.TAN_H)
                                .add(9, OutputActivationFunctionType.SIGMOID)
                                .build()))
                        .hiddenBias(FloatNumber.random(RandomType.QUADRUPLE_STEEPENED_SIGMOID, 30f))
                        .hiddenActivationFunction(EnumValue.literal(ActivationFunctionType.TAN_H))
                        .build())
                .connections(ConnectionGeneSupport.builder()
                        .weightFactory(FloatNumber.random(RandomType.BELL_CURVE, 2f))
                        .weightPerturber(FloatNumber.literal(2.5f))
                        .recurrentStateType(RecurrentStateType.DEFAULT)
                        .recurrentAllowanceRate(FloatNumber.literal(0.2f))
                        .unrestrictedDirectionAllowanceRate(FloatNumber.literal(1f))
                        .multiCycleAllowanceRate(FloatNumber.literal(0f))
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
                        .maximumGeneration(1_000)
                        .maximumRestartCount(9)
                        .build())
                .add(new DelegatedTrainingPolicy(TicTacToeTaskSetup::determineTrainingResult))
                .add(new ContinuousTrainingPolicy())
                .build();
    }
}
