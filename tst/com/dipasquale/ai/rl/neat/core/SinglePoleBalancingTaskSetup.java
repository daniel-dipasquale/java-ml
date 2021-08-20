package com.dipasquale.ai.rl.neat.core;

import com.dipasquale.ai.common.fitness.FitnessDeterminerFactory;
import com.dipasquale.ai.common.function.activation.ActivationFunctionType;
import com.dipasquale.ai.common.function.activation.OutputActivationFunctionType;
import com.dipasquale.ai.rl.neat.common.RandomType;
import com.dipasquale.ai.rl.neat.genotype.Genome;
import com.dipasquale.ai.rl.neat.settings.ConnectionGeneSupport;
import com.dipasquale.ai.rl.neat.settings.CrossOverSupport;
import com.dipasquale.ai.rl.neat.settings.EnumValue;
import com.dipasquale.ai.rl.neat.settings.EvaluatorSettings;
import com.dipasquale.ai.rl.neat.settings.FloatNumber;
import com.dipasquale.ai.rl.neat.settings.GeneralEvaluatorSupport;
import com.dipasquale.ai.rl.neat.settings.GenesisGenomeTemplate;
import com.dipasquale.ai.rl.neat.settings.InitialConnectionType;
import com.dipasquale.ai.rl.neat.settings.InitialWeightType;
import com.dipasquale.ai.rl.neat.settings.IntegerNumber;
import com.dipasquale.ai.rl.neat.settings.MutationSupport;
import com.dipasquale.ai.rl.neat.settings.NeuralNetworkSupport;
import com.dipasquale.ai.rl.neat.settings.NeuralNetworkType;
import com.dipasquale.ai.rl.neat.settings.NodeGeneSupport;
import com.dipasquale.ai.rl.neat.settings.ParallelismSupport;
import com.dipasquale.ai.rl.neat.settings.RandomSupport;
import com.dipasquale.ai.rl.neat.settings.SpeciationSupport;
import com.dipasquale.common.random.float2.ConstantRandomSupport;
import com.dipasquale.common.random.float2.ThreadLocalRandomSupport;
import com.dipasquale.simulation.cart.pole.CartPoleEnvironment;
import com.dipasquale.threading.event.loop.IterableEventLoop;
import com.google.common.collect.ImmutableList;
import lombok.Getter;

import java.util.Set;

final class SinglePoleBalancingTaskSetup implements TaskSetup { // TODO: this test might not be working as expected
    private static final String NAME = "Single Pole Balancing";
    private static final double TIME_SPENT_GOAL = 3D;
    private static final com.dipasquale.common.random.float2.RandomSupport RANDOM_SUPPORT = new ThreadLocalRandomSupport();
    private static final int SUCCESSFUL_SCENARIOS_WHILE_FITNESS_TEST = 5;
    private static final int SUCCESSFUL_SCENARIOS = SUCCESSFUL_SCENARIOS_WHILE_FITNESS_TEST * 1;
    @Getter
    private final int populationSize = 150;

    private static float[] convertToFloat(final double[] input) {
        float[] output = new float[input.length];

        for (int i = 0; i < input.length; i++) {
            output[i] = (float) input[i];
        }

        return output;
    }

    private static float calculateFitness(final Genome genome) {
        float minimumTimeSpent = Float.MAX_VALUE;

        for (int i = 0; i < SUCCESSFUL_SCENARIOS_WHILE_FITNESS_TEST; i++) {
            CartPoleEnvironment cartPole = CartPoleEnvironment.createRandom(RANDOM_SUPPORT);

            while (!cartPole.isLimitHit() && Double.compare(cartPole.getTimeSpent(), TIME_SPENT_GOAL) < 0) {
                float[] input = convertToFloat(cartPole.getState());
                float[] output = genome.activate(input);

                cartPole.stepInDiscrete(output[0]);
            }

            minimumTimeSpent = Math.min(minimumTimeSpent, (float) cartPole.getTimeSpent());
        }

        if (Float.compare(minimumTimeSpent, Float.MAX_VALUE) == 0) {
            return 0f;
        }

        return minimumTimeSpent;
    }

    private static NeatTrainingResult determineTrainingResult(final NeatActivator activator) {
        boolean success = true;
        ConstantRandomSupport randomSupport = new ConstantRandomSupport(SUCCESSFUL_SCENARIOS * 4);

        for (int i = 0; success && i < SUCCESSFUL_SCENARIOS; i++) {
            CartPoleEnvironment cartPole = CartPoleEnvironment.createRandom(randomSupport);

            while (!cartPole.isLimitHit() && Double.compare(cartPole.getTimeSpent(), TIME_SPENT_GOAL) < 0) {
                float[] input = convertToFloat(cartPole.getState());
                float[] output = activator.activate(input);

                cartPole.stepInDiscrete(output[0]);
            }

            success = Double.compare(cartPole.getTimeSpent(), TIME_SPENT_GOAL) >= 0;
        }

        if (success) {
            return NeatTrainingResult.WORKING_SOLUTION_FOUND;
        }

        return NeatTrainingResult.EVALUATE_FITNESS_AND_EVOLVE;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public EvaluatorSettings createSettings(final Set<String> genomeIds, final IterableEventLoop eventLoop) {
        return EvaluatorSettings.builder()
                .general(GeneralEvaluatorSupport.builder()
                        .populationSize(populationSize)
                        .genesisGenomeFactory(GenesisGenomeTemplate.builder()
                                .inputs(IntegerNumber.literal(4))
                                .outputs(IntegerNumber.literal(1))
                                .biases(ImmutableList.of())
                                .initialConnectionType(InitialConnectionType.ALL_INPUTS_AND_BIASES_TO_ALL_OUTPUTS)
                                .initialWeightType(InitialWeightType.RANDOM)
                                .build())
                        .fitnessFunction(g -> {
                            genomeIds.add(g.getId());

                            return calculateFitness(g);
                        })
                        .fitnessDeterminerFactory(FitnessDeterminerFactory.createLastValue())
                        .build())
                .nodes(NodeGeneSupport.builder()
                        .inputBias(FloatNumber.literal(0f))
                        .inputActivationFunction(EnumValue.literal(ActivationFunctionType.IDENTITY))
                        .outputBias(FloatNumber.random(RandomType.UNIFORM, -1f, 1f))
                        .outputActivationFunction(EnumValue.literal(OutputActivationFunctionType.TAN_H))
                        .hiddenBias(FloatNumber.random(RandomType.UNIFORM, -1f, 1f))
                        .hiddenActivationFunction(EnumValue.literal(ActivationFunctionType.SIGMOID))
                        .build())
                .connections(ConnectionGeneSupport.builder()
                        .weightFactory(FloatNumber.random(RandomType.UNIFORM, -1f, 1f))
                        .weightPerturber(FloatNumber.literal(1.8f))
                        .build())
                .neuralNetwork(NeuralNetworkSupport.builder()
                        .type(NeuralNetworkType.MULTI_CYCLE_RECURRENT)
                        .build())
                .parallelism(ParallelismSupport.builder()
                        .eventLoop(eventLoop)
                        .build())
                .random(RandomSupport.builder()
                        .nextIndex(RandomType.UNIFORM)
                        .isLessThan(RandomType.UNIFORM)
                        .build())
                .mutation(MutationSupport.builder()
                        .addNodeMutationRate(FloatNumber.literal(0.1f))
                        .addConnectionMutationRate(FloatNumber.literal(0.2f))
                        .perturbConnectionsWeightRate(FloatNumber.literal(0.75f))
                        .replaceConnectionsWeightRate(FloatNumber.literal(0.5f))
                        .disableConnectionExpressedRate(FloatNumber.literal(0.05f))
                        .build())
                .crossOver(CrossOverSupport.builder()
                        .overrideConnectionExpressedRate(FloatNumber.literal(0.5f))
                        .useRandomParentConnectionWeightRate(FloatNumber.literal(0.6f))
                        .build())
                .speciation(SpeciationSupport.builder()
                        .maximumSpecies(IntegerNumber.literal(20))
                        .maximumGenomes(IntegerNumber.literal(20))
                        .weightDifferenceCoefficient(FloatNumber.literal(0.5f))
                        .disjointCoefficient(FloatNumber.literal(1f))
                        .excessCoefficient(FloatNumber.literal(1f))
                        .compatibilityThreshold(FloatNumber.literal(4f))
                        .compatibilityThresholdModifier(FloatNumber.literal(1.2f))
                        .eugenicsThreshold(FloatNumber.literal(0.4f))
                        .elitistThreshold(FloatNumber.literal(0.01f))
                        .elitistThresholdMinimum(IntegerNumber.literal(2))
                        .stagnationDropOffAge(IntegerNumber.literal(15))
                        .interSpeciesMatingRate(FloatNumber.literal(0.001f))
                        .mateOnlyRate(FloatNumber.literal(0.2f))
                        .mutateOnlyRate(FloatNumber.literal(0.25f))
                        .build())
                .build();
    }

    @Override
    public NeatTrainingPolicy createTrainingPolicy() {
        return new NeatTrainingPolicy() {
            @Override
            public NeatTrainingResult test(final NeatActivator activator) {
                return determineTrainingResult(activator);
            }

            @Override
            public void complete() {
            }
        };
    }
}
