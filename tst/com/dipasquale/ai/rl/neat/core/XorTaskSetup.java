package com.dipasquale.ai.rl.neat.core;

import com.dipasquale.ai.common.fitness.LastValueFitnessDeterminerFactory;
import com.dipasquale.ai.common.function.activation.ActivationFunctionType;
import com.dipasquale.ai.common.function.activation.OutputActivationFunctionType;
import com.dipasquale.ai.rl.neat.common.RandomType;
import com.dipasquale.ai.rl.neat.phenotype.GenomeActivator;
import com.dipasquale.ai.rl.neat.settings.ActivationSupport;
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
import com.dipasquale.ai.rl.neat.settings.MetricCollectionType;
import com.dipasquale.ai.rl.neat.settings.MetricSupport;
import com.dipasquale.ai.rl.neat.settings.MutationSupport;
import com.dipasquale.ai.rl.neat.settings.NeuralNetworkType;
import com.dipasquale.ai.rl.neat.settings.NodeGeneSupport;
import com.dipasquale.ai.rl.neat.settings.ParallelismSupport;
import com.dipasquale.ai.rl.neat.settings.RandomSupport;
import com.dipasquale.ai.rl.neat.settings.SpeciationSupport;
import com.dipasquale.synchronization.event.loop.IterableEventLoop;
import com.google.common.collect.ImmutableList;
import lombok.Getter;

import java.util.EnumSet;
import java.util.Set;

final class XorTaskSetup implements TaskSetup {
    private static final String NAME = "XOR";

    private static final float[][] INPUTS = new float[][]{
            new float[]{1f, 1f}, // 0f
            new float[]{1f, 0f}, // 1f
            new float[]{0f, 1f}, // 1f
            new float[]{0f, 0f}  // 0f
    };

    private static final float[] EXPECTED_OUTPUTS = new float[]{0f, 1f, 1f, 0f};
    @Getter
    private final int populationSize = 150;

    private static float calculateFitness(final GenomeActivator genomeActivator) {
        float error = 0f;

        for (int i = 0; i < INPUTS.length; i++) {
            float[] output = genomeActivator.activate(INPUTS[i]);

            error += (float) Math.pow(EXPECTED_OUTPUTS[i] - output[0], 2D);
        }

        return (float) INPUTS.length - error;
    }

    private static boolean determineTrainingResult(final NeatActivator activator) {
        boolean success = true;

        for (int i = 0; success && i < INPUTS.length; i++) {
            float[] output = activator.activate(INPUTS[i]);
            int comparison = Float.compare(EXPECTED_OUTPUTS[i], (float) Math.round(output[0]));

            success = comparison == 0;
        }

        return success;
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
                        .genesisGenomeTemplate(GenesisGenomeTemplate.builder()
                                .inputs(IntegerNumber.literal(2))
                                .outputs(IntegerNumber.literal(1))
                                .biases(ImmutableList.of())
                                .initialConnectionType(InitialConnectionType.ALL_INPUTS_AND_BIASES_TO_ALL_OUTPUTS)
                                .initialWeightType(InitialWeightType.RANDOM)
                                .build())
                        .fitnessFunction(g -> {
                            genomeIds.add(g.getId());

                            return calculateFitness(g);
                        })
                        .fitnessDeterminerFactory(new LastValueFitnessDeterminerFactory())
                        .build())
                .nodes(NodeGeneSupport.builder()
                        .inputBias(FloatNumber.literal(0f))
                        .inputActivationFunction(EnumValue.literal(ActivationFunctionType.IDENTITY))
                        .outputBias(FloatNumber.random(RandomType.UNIFORM, -1f, 1f))
                        .outputActivationFunction(EnumValue.literal(OutputActivationFunctionType.SIGMOID))
                        .hiddenBias(FloatNumber.random(RandomType.UNIFORM, -1f, 1f))
                        .hiddenActivationFunction(EnumValue.literal(ActivationFunctionType.TAN_H))
                        .build())
                .connections(ConnectionGeneSupport.builder()
                        .weightFactory(FloatNumber.random(RandomType.UNIFORM, -1f, 1f))
                        .weightPerturber(FloatNumber.literal(2.5f))
                        .build())
                .activation(ActivationSupport.builder()
                        .neuralNetworkType(NeuralNetworkType.FEED_FORWARD)
                        .build())
                .parallelism(ParallelismSupport.builder()
                        .eventLoop(eventLoop)
                        .build())
                .random(RandomSupport.builder()
                        .type(RandomType.UNIFORM)
                        .build())
                .mutation(MutationSupport.builder()
                        .addNodeRate(FloatNumber.literal(0.03f))
                        .addConnectionRate(FloatNumber.literal(0.06f))
                        .perturbWeightRate(FloatNumber.literal(0.75f))
                        .replaceWeightRate(FloatNumber.literal(0.5f))
                        .disableExpressedConnectionRate(FloatNumber.literal(0.015f))
                        .build())
                .crossOver(CrossOverSupport.builder()
                        .overrideExpressedConnectionRate(FloatNumber.literal(0.5f))
                        .useWeightFromRandomParentRate(FloatNumber.literal(0.6f))
                        .build())
                .speciation(SpeciationSupport.builder()
                        .weightDifferenceCoefficient(FloatNumber.literal(0.4f))
                        .disjointCoefficient(FloatNumber.literal(1f))
                        .excessCoefficient(FloatNumber.literal(1f))
                        .compatibilityThreshold(FloatNumber.literal(3f))
                        .compatibilityThresholdModifier(FloatNumber.literal(1f))
                        .eugenicsThreshold(FloatNumber.literal(0.2f))
                        .elitistThreshold(FloatNumber.literal(0.01f))
                        .elitistThresholdMinimum(IntegerNumber.literal(2))
                        .stagnationDropOffAge(IntegerNumber.literal(15))
                        .interSpeciesMatingRate(FloatNumber.literal(0.001f))
                        .mateOnlyRate(FloatNumber.literal(0.2f))
                        .mutateOnlyRate(FloatNumber.literal(0.25f))
                        .build())
                .metrics(MetricSupport.builder()
                        .type(NeatTest.METRICS_EMISSION_ENABLED
                                ? EnumSet.of(MetricCollectionType.ENABLED)
                                : EnumSet.noneOf(MetricCollectionType.class))
                        .build())
                .build();
    }

    @Override
    public NeatTrainingPolicy createTrainingPolicy() {
        return NeatTrainingPolicies.builder()
                .add(SupervisorTrainingPolicy.builder()
                        .maximumGeneration(300)
                        .maximumRestartCount(1)
                        .build())
                .add(new DelegatedTrainingPolicy(XorTaskSetup::determineTrainingResult))
                .add(new ContinuousTrainingPolicy())
                .build();
    }
}
