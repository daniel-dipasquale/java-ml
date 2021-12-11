package com.dipasquale.ai.rl.neat.core;

import com.dipasquale.ai.common.fitness.AverageFitnessDeterminerFactory;
import com.dipasquale.ai.rl.neat.phenotype.GenomeActivator;
import com.dipasquale.ai.rl.neat.phenotype.NeuralNetwork;
import com.dipasquale.ai.rl.neat.phenotype.NeuronMemory;
import com.dipasquale.simulation.openai.gym.client.GymClient;
import com.dipasquale.simulation.openai.gym.client.StepResult;
import com.dipasquale.synchronization.event.loop.IterableEventLoop;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class OpenAIGymCartPoleTaskSetup implements OpenAIGymTaskSetup {
    private static final int SUCCESSFUL_SCENARIOS_WHILE_FITNESS_TEST = 5;
    private static final int SUCCESSFUL_SCENARIOS = 2; // NOTE: the higher this number the more consistent the solution will be
    private static final double REWARD_GOAL = 195D;
    private final GymClient gymClient;
    @Getter
    private final String name = "CartPole-v0";
    @Getter
    private final boolean metricsEmissionEnabled;
    @Getter
    private final int populationSize = 150;

    private static float[] convertToFloat(final double[] input) {
        float[] output = new float[input.length];

        for (int i = 0; i < input.length; i++) {
            output[i] = (float) input[i];
        }

        return output;
    }

    private float calculateFitness(final NeuralNetwork neuralNetwork, final boolean render) {
        double fitness = 0D;
        float[] input = convertToFloat(gymClient.restart(name));
        NeuronMemory neuronMemory = neuralNetwork.createMemory();

        for (boolean done = false; !done; ) {
            float[] output = neuralNetwork.activate(input, neuronMemory);
            double action = Math.round(output[0]);
            StepResult stepResult = gymClient.step(name, action, render);

            done = stepResult.isDone();
            input = convertToFloat(stepResult.getObservation());
            fitness += stepResult.getReward();
        }

        return (float) fitness;
    }

    private float calculateFitness(final GenomeActivator genomeActivator) {
        return calculateFitness(genomeActivator, false);
    }

    private boolean determineTrainingResult(final NeatActivator activator) {
        boolean success = true;

        for (int i = 0; success && i < SUCCESSFUL_SCENARIOS; i++) {
            float fitness = calculateFitness(activator, false);

            success = Double.compare(fitness, REWARD_GOAL) >= 0;
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
                        .maximumGeneration(500)
                        .maximumRestartCount(4)
                        .build())
                .add(new DelegatedTrainingPolicy(this::determineTrainingResult))
                .add(ContinuousTrainingPolicy.builder()
                        .fitnessTestCount(SUCCESSFUL_SCENARIOS_WHILE_FITNESS_TEST)
                        .build())
                .build();
    }

    @Override
    public void visualize(final NeatActivator activator) {
        calculateFitness(activator, true);
    }
}
