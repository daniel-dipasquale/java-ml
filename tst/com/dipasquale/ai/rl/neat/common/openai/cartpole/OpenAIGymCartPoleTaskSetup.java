package com.dipasquale.ai.rl.neat.common.openai.cartpole;

import com.dipasquale.ai.common.NeuralNetwork;
import com.dipasquale.ai.common.fitness.AverageFitnessControllerFactory;
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
import com.dipasquale.ai.rl.neat.IsolatedNeatEnvironment;
import com.dipasquale.ai.rl.neat.MetricCollectionType;
import com.dipasquale.ai.rl.neat.MetricCollectorTrainingPolicy;
import com.dipasquale.ai.rl.neat.MetricsSupport;
import com.dipasquale.ai.rl.neat.NeatActivator;
import com.dipasquale.ai.rl.neat.NeatTrainingPolicy;
import com.dipasquale.ai.rl.neat.NeatTrainingPolicyController;
import com.dipasquale.ai.rl.neat.NodeGeneSupport;
import com.dipasquale.ai.rl.neat.ParallelismSupport;
import com.dipasquale.ai.rl.neat.SupervisorTrainingPolicy;
import com.dipasquale.ai.rl.neat.common.openai.OpenAIGymTaskSetup;
import com.dipasquale.ai.rl.neat.function.activation.ActivationFunctionType;
import com.dipasquale.ai.rl.neat.phenotype.DoubleSolutionNeuronLayerTopologyDefinition;
import com.dipasquale.ai.rl.neat.phenotype.GenomeActivator;
import com.dipasquale.ai.rl.neat.phenotype.IdentityNeuronLayerTopologyDefinition;
import com.dipasquale.ai.rl.neat.phenotype.NeatNeuronMemory;
import com.dipasquale.ai.rl.neat.phenotype.NeuronLayerTopologyDefinition;
import com.dipasquale.common.time.MillisecondsDateTimeSupport;
import com.dipasquale.simulation.openai.gym.client.GymClient;
import com.dipasquale.simulation.openai.gym.client.StepResult;
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
public final class OpenAIGymCartPoleTaskSetup implements OpenAIGymTaskSetup {
    private static final TopologySettingsType TOPOLOGY_SETTINGS_TYPE = TopologySettingsType.DOUBLE_OUTPUT;
    private static final int VALIDATION_SCENARIO_COUNT = 2; // NOTE: the higher this number the more consistent the solution will be
    private static final double REWARD_GOAL = 195D;
    private static final int FITNESS_TEST_COUNT = 5;
    private final String name = "CartPole-v0";
    private final GymClient gymClient;
    private final int populationSize = 150;
    private final boolean metricsEmissionEnabled;

    private static float[] convertToFloat(final double[] input) {
        float[] output = new float[input.length];

        for (int i = 0; i < input.length; i++) {
            output[i] = (float) input[i];
        }

        return output;
    }

    private float calculateFitness(final NeuralNetwork<NeatNeuronMemory> neuralNetwork, final boolean render) {
        double fitness = 0D;
        float[] input = convertToFloat(gymClient.restart(name));
        NeatNeuronMemory neuronMemory = neuralNetwork.createMemory();

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

        for (int i = 0; success && i < VALIDATION_SCENARIO_COUNT; i++) {
            float fitness = calculateFitness(activator, false);

            success = Double.compare(fitness, REWARD_GOAL) >= 0;
        }

        return success;
    }

    @Override
    public EvaluatorSettings createSettings(final Set<String> genomeIds, final ParallelEventLoop eventLoop) {
        return EvaluatorSettings.builder()
                .general(GeneralSupport.builder()
                        .populationSize(populationSize)
                        .genesisGenomeTemplate(GenesisGenomeTemplate.builder()
                                .inputs(4)
                                .outputs(TOPOLOGY_SETTINGS_TYPE.nodeCount)
                                .biases(List.of(1f))
                                .hiddenLayers(List.of())
                                .initialConnectionType(InitialConnectionType.FULLY_CONNECTED)
                                .initialWeightType(InitialWeightType.ALL_RANDOM)
                                .build())
                        .fitnessFunction((IsolatedNeatEnvironment) genomeActivator -> {
                            genomeIds.add(genomeActivator.getGenome().getId());

                            return calculateFitness(genomeActivator);
                        })
                        .fitnessControllerFactory(AverageFitnessControllerFactory.getInstance())
                        .build())
                .parallelism(ParallelismSupport.builder()
                        .eventLoop(eventLoop)
                        .build())
                .nodes(NodeGeneSupport.builder()
                        .hiddenActivationFunction(EnumValue.literal(ActivationFunctionType.RE_LU))
                        .build())
                .connections(ConnectionGeneSupport.builder()
                        .recurrentAllowanceRate(FloatNumber.literal(0f))
                        .build())
                .activation(ActivationSupport.builder()
                        .outputTopologyDefinition(TOPOLOGY_SETTINGS_TYPE.outputTopologyDefinition)
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
                        .maximumGeneration(500)
                        .maximumRestartCount(4)
                        .build())
                .add(new MetricCollectorTrainingPolicy(new MillisecondsDateTimeSupport()))
                .add(new DelegatedTrainingPolicy(this::determineTrainingResult))
                .add(ContinuousTrainingPolicy.builder()
                        .fitnessTestCount(FITNESS_TEST_COUNT)
                        .build())
                .build();
    }

    @Override
    public void visualize(final NeatActivator activator) {
        calculateFitness(activator, true);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private enum TopologySettingsType {
        VANILLA_OUTPUT(1, IdentityNeuronLayerTopologyDefinition.getInstance()),
        DOUBLE_OUTPUT(2, DoubleSolutionNeuronLayerTopologyDefinition.getInstance());

        private final int nodeCount;
        private final NeuronLayerTopologyDefinition outputTopologyDefinition;
    }
}
