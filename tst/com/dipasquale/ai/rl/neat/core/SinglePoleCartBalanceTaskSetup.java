package com.dipasquale.ai.rl.neat.core;

import com.dipasquale.ai.common.fitness.AverageFitnessDeterminerFactory;
import com.dipasquale.ai.rl.neat.phenotype.GenomeActivator;
import com.dipasquale.ai.rl.neat.phenotype.NeuralNetwork;
import com.dipasquale.ai.rl.neat.phenotype.NeuronMemory;
import com.dipasquale.common.random.float2.CyclicRandomSupport;
import com.dipasquale.common.random.float2.RandomSupport;
import com.dipasquale.common.random.float2.ThreadLocalRandomSupport;
import com.dipasquale.simulation.cart.pole.CartPoleEnvironment;
import com.dipasquale.synchronization.event.loop.IterableEventLoop;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
final class SinglePoleCartBalanceTaskSetup implements TaskSetup {
    private static final double TIME_SPENT_GOAL = 60D;
    private static final RandomSupport RANDOM_SUPPORT = new ThreadLocalRandomSupport();
    private static final int SUCCESSFUL_SCENARIOS_WHILE_FITNESS_TEST = 5;
    private static final int SUCCESSFUL_SCENARIOS = 2; // NOTE: the higher this number the more consistent the solution will be
    private final String name = "Single Pole Cart Balance";
    private final boolean metricsEmissionEnabled;
    private final int populationSize = 150;

    private static float[] convertToFloat(final double[] input) {
        float[] output = new float[input.length];

        for (int i = 0; i < input.length; i++) {
            output[i] = (float) input[i];
        }

        return output;
    }

    private static void testFitness(final NeuralNetwork neuralNetwork, final CartPoleEnvironment cartPole) {
        NeuronMemory neuronMemory = neuralNetwork.createMemory();

        while (!cartPole.isLimitHit() && Double.compare(cartPole.getTimeSpent(), TIME_SPENT_GOAL) < 0) {
            float[] input = convertToFloat(cartPole.getState());
            float[] output = neuralNetwork.activate(input, neuronMemory);

            cartPole.stepInDiscrete(output[0]);
        }
    }

    private static float calculateFitness(final GenomeActivator genomeActivator) {
        CartPoleEnvironment cartPole = CartPoleEnvironment.createRandom(RANDOM_SUPPORT);

        testFitness(genomeActivator, cartPole);

        return (float) cartPole.getTimeSpent();
    }

    private static boolean determineTrainingResult(final NeatActivator activator) {
        boolean success = true;
        CyclicRandomSupport randomSupport = new CyclicRandomSupport(SUCCESSFUL_SCENARIOS * 4);

        for (int i = 0; success && i < SUCCESSFUL_SCENARIOS; i++) {
            CartPoleEnvironment cartPole = CartPoleEnvironment.createRandom(randomSupport);

            testFitness(activator, cartPole);

            success = Double.compare(cartPole.getTimeSpent(), TIME_SPENT_GOAL) >= 0;
        }

        return success;
    }

    @Override
    public EvaluatorSettings createSettings(final Set<String> genomeIds, final IterableEventLoop eventLoop) {
        return EvaluatorSettings.builder()
                .general(GeneralSupport.builder()
                        .populationSize(IntegerNumber.literal(populationSize))
                        .genesisGenomeTemplate(GenesisGenomeTemplate.builder()
                                .inputs(IntegerNumber.literal(4))
                                .outputs(IntegerNumber.literal(1))
                                .biases(List.of())
                                .initialConnectionType(InitialConnectionType.ALL_INPUTS_AND_BIASES_TO_ALL_OUTPUTS)
                                .initialWeightType(InitialWeightType.ALL_RANDOM)
                                .build())
                        .fitnessFunction((IsolatedNeatEnvironment) ga -> {
                            genomeIds.add(ga.getGenome().getId());

                            return calculateFitness(ga);
                        })
                        .fitnessDeterminerFactory(new AverageFitnessDeterminerFactory())
                        .build())
                .parallelism(ParallelismSupport.builder()
                        .eventLoop(eventLoop)
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
                        .maximumGeneration(50)
                        .maximumRestartCount(9)
                        .build())
                .add(new DelegatedTrainingPolicy(SinglePoleCartBalanceTaskSetup::determineTrainingResult))
                .add(ContinuousTrainingPolicy.builder()
                        .fitnessTestCount(SUCCESSFUL_SCENARIOS_WHILE_FITNESS_TEST)
                        .build())
                .build();
    }
}
