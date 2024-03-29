package com.dipasquale.ai.rl.neat.common.xor;

import com.dipasquale.ai.common.fitness.LastValueFitnessControllerFactory;
import com.dipasquale.ai.rl.neat.ActivationSettings;
import com.dipasquale.ai.rl.neat.ConnectionGeneSettings;
import com.dipasquale.ai.rl.neat.ContinuousTrainingPolicy;
import com.dipasquale.ai.rl.neat.CrossOverSettings;
import com.dipasquale.ai.rl.neat.DelegatedTrainingPolicy;
import com.dipasquale.ai.rl.neat.EnumValue;
import com.dipasquale.ai.rl.neat.FloatNumber;
import com.dipasquale.ai.rl.neat.GeneralSettings;
import com.dipasquale.ai.rl.neat.GenesisGenomeTemplate;
import com.dipasquale.ai.rl.neat.InitialConnectionType;
import com.dipasquale.ai.rl.neat.InitialWeightType;
import com.dipasquale.ai.rl.neat.IntegerNumber;
import com.dipasquale.ai.rl.neat.MetricCollectionType;
import com.dipasquale.ai.rl.neat.MetricsSettings;
import com.dipasquale.ai.rl.neat.MutationSettings;
import com.dipasquale.ai.rl.neat.NeatSettings;
import com.dipasquale.ai.rl.neat.NeatTrainingAssessor;
import com.dipasquale.ai.rl.neat.NeatTrainingPolicy;
import com.dipasquale.ai.rl.neat.NeatTrainingPolicyController;
import com.dipasquale.ai.rl.neat.NodeGeneSettings;
import com.dipasquale.ai.rl.neat.ParallelismSettings;
import com.dipasquale.ai.rl.neat.RandomType;
import com.dipasquale.ai.rl.neat.RandomnessSettings;
import com.dipasquale.ai.rl.neat.RecurrentStateType;
import com.dipasquale.ai.rl.neat.SecludedNeatEnvironment;
import com.dipasquale.ai.rl.neat.SpeciationSettings;
import com.dipasquale.ai.rl.neat.SupervisorTrainingPolicy;
import com.dipasquale.ai.rl.neat.common.NeatObjective;
import com.dipasquale.ai.rl.neat.common.TaskSetup;
import com.dipasquale.ai.rl.neat.function.activation.ActivationFunctionType;
import com.dipasquale.ai.rl.neat.function.activation.OutputActivationFunctionType;
import com.dipasquale.ai.rl.neat.phenotype.DoubleSolutionNeuronLayerTopologyDefinition;
import com.dipasquale.ai.rl.neat.phenotype.IdentityNeuronLayerTopologyDefinition;
import com.dipasquale.ai.rl.neat.phenotype.NeuronLayerTopologyDefinition;
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
public final class XorTaskSetup implements TaskSetup {
    private static final FitnessFunctionSettingsType FITNESS_FUNCTION_SETTINGS_TYPE = FitnessFunctionSettingsType.DISTANCE_FROM_EXPECTED;
    private static final OutputTopologySettingsType OUTPUT_TOPOLOGY_SETTINGS_TYPE = OutputTopologySettingsType.DOUBLE;
    private final String name = "XOR";
    private final int populationSize = 150;
    private final boolean metricsEmissionEnabled;

    @Override
    public NeatSettings createSettings(final Set<Integer> genomeIds, final ParallelEventLoop eventLoop) {
        return NeatSettings.builder()
                .general(GeneralSettings.builder()
                        .populationSize(populationSize)
                        .genesisGenomeTemplate(GenesisGenomeTemplate.builder()
                                .inputs(2)
                                .outputs(OUTPUT_TOPOLOGY_SETTINGS_TYPE.nodeCount)
                                .biases(List.of(1f))
                                .hiddenLayers(List.of())
                                .initialConnectionType(InitialConnectionType.FULLY_CONNECTED)
                                .initialWeightType(InitialWeightType.ALL_RANDOM)
                                .build())
                        .fitnessFunction((SecludedNeatEnvironment) genomeActivator -> {
                            genomeIds.add(genomeActivator.getGenome().getId());

                            return FITNESS_FUNCTION_SETTINGS_TYPE.environment.test(genomeActivator);
                        })
                        .fitnessControllerFactory(LastValueFitnessControllerFactory.getInstance())
                        .build())
                .parallelism(ParallelismSettings.builder()
                        .eventLoop(eventLoop)
                        .build())
                .randomness(RandomnessSettings.builder()
                        .type(RandomType.UNIFORM)
                        .build())
                .nodeGenes(NodeGeneSettings.builder()
                        .inputBias(FloatNumber.literal(0f))
                        .inputActivationFunction(EnumValue.literal(ActivationFunctionType.IDENTITY))
                        .outputBias(FloatNumber.random(RandomType.UNIFORM, 2f))
                        .outputActivationFunction(EnumValue.literal(OutputActivationFunctionType.SIGMOID))
                        .hiddenBias(FloatNumber.random(RandomType.UNIFORM, 4f))
                        .hiddenActivationFunction(EnumValue.literal(ActivationFunctionType.TAN_H))
                        .build())
                .connectionGenes(ConnectionGeneSettings.builder()
                        .weightFactory(FloatNumber.random(RandomType.BELL_CURVE, 2f))
                        .weightPerturber(FloatNumber.literal(2.5f))
                        .recurrentStateType(RecurrentStateType.DEFAULT)
                        .recurrentAllowanceRate(FloatNumber.literal(0.2f))
                        .unrestrictedDirectionAllowanceRate(FloatNumber.literal(0.5f))
                        .multiCycleAllowanceRate(FloatNumber.literal(0f))
                        .build())
                .activation(ActivationSettings.builder()
                        .outputTopologyDefinition(OUTPUT_TOPOLOGY_SETTINGS_TYPE.topologyDefinition)
                        .build())
                .mutation(MutationSettings.builder()
                        .addNodeRate(FloatNumber.literal(0.03f))
                        .addConnectionRate(FloatNumber.literal(0.06f))
                        .perturbWeightRate(FloatNumber.literal(0.75f))
                        .replaceWeightRate(FloatNumber.literal(0.5f))
                        .disableExpressedConnectionRate(FloatNumber.literal(0.015f))
                        .build())
                .crossOver(CrossOverSettings.builder()
                        .overrideExpressedConnectionRate(FloatNumber.literal(0.5f))
                        .useWeightFromRandomParentRate(FloatNumber.literal(0.6f))
                        .build())
                .speciation(SpeciationSettings.builder()
                        .maximumSpecies(IntegerNumber.literal(populationSize))
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
                .metrics(MetricsSettings.builder()
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
                        .maximumGeneration(200)
                        .maximumRestartCount(9)
                        .build())
                .add(new DelegatedTrainingPolicy(FITNESS_FUNCTION_SETTINGS_TYPE.trainingAssessor))
                .add(new ContinuousTrainingPolicy())
                .build();
    }

    private enum FitnessFunctionSettingsType {
        DISTANCE_FROM_EXPECTED(new DistanceFromExpectedObjective());

        FitnessFunctionSettingsType(final NeatObjective<SecludedNeatEnvironment> objective) {
            this.environment = objective.getEnvironment();
            this.trainingAssessor = objective.getTrainingAssessor();
        }

        private final SecludedNeatEnvironment environment;
        private final NeatTrainingAssessor trainingAssessor;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private enum OutputTopologySettingsType {
        VANILLA(1, IdentityNeuronLayerTopologyDefinition.getInstance()),
        DOUBLE(2, DoubleSolutionNeuronLayerTopologyDefinition.getInstance());

        private final int nodeCount;
        private final NeuronLayerTopologyDefinition topologyDefinition;
    }
}
