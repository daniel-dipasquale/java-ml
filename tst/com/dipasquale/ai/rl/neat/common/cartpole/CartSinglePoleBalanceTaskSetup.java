package com.dipasquale.ai.rl.neat.common.cartpole;

import com.dipasquale.ai.common.fitness.AverageFitnessControllerFactory;
import com.dipasquale.ai.rl.neat.ActivationSettings;
import com.dipasquale.ai.rl.neat.ConnectionGeneSettings;
import com.dipasquale.ai.rl.neat.ContinuousTrainingPolicy;
import com.dipasquale.ai.rl.neat.DelegatedTrainingPolicy;
import com.dipasquale.ai.rl.neat.EnumValue;
import com.dipasquale.ai.rl.neat.FloatNumber;
import com.dipasquale.ai.rl.neat.GeneralSettings;
import com.dipasquale.ai.rl.neat.GenesisGenomeTemplate;
import com.dipasquale.ai.rl.neat.InitialConnectionType;
import com.dipasquale.ai.rl.neat.InitialWeightType;
import com.dipasquale.ai.rl.neat.MetricCollectionType;
import com.dipasquale.ai.rl.neat.MetricCollectorTrainingPolicy;
import com.dipasquale.ai.rl.neat.MetricsSettings;
import com.dipasquale.ai.rl.neat.NeatSettings;
import com.dipasquale.ai.rl.neat.NeatTrainingAssessor;
import com.dipasquale.ai.rl.neat.NeatTrainingPolicy;
import com.dipasquale.ai.rl.neat.NeatTrainingPolicyController;
import com.dipasquale.ai.rl.neat.NodeGeneSettings;
import com.dipasquale.ai.rl.neat.ParallelismSettings;
import com.dipasquale.ai.rl.neat.SecludedNeatEnvironment;
import com.dipasquale.ai.rl.neat.SupervisorTrainingPolicy;
import com.dipasquale.ai.rl.neat.common.NeatObjective;
import com.dipasquale.ai.rl.neat.common.TaskSetup;
import com.dipasquale.ai.rl.neat.function.activation.ActivationFunctionType;
import com.dipasquale.ai.rl.neat.phenotype.DoubleSolutionNeuronLayerTopologyDefinition;
import com.dipasquale.ai.rl.neat.phenotype.IdentityNeuronLayerTopologyDefinition;
import com.dipasquale.ai.rl.neat.phenotype.NeuronLayerTopologyDefinition;
import com.dipasquale.common.time.MillisecondsDateTimeSupport;
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
public final class CartSinglePoleBalanceTaskSetup implements TaskSetup {
    private static final double TIME_BALANCING_GOAL = 60D;
    private static final int VALIDATION_SCENARIO_COUNT = 2; // NOTE: the higher this number the more consistent the solution will be
    private static final EnvironmentSettingsType ENVIRONMENT_SETTINGS_TYPE = EnvironmentSettingsType.BALANCE_UNTIL_DONE;
    private static final OutputTopologySettingsType OUTPUT_TOPOLOGY_SETTINGS_TYPE = OutputTopologySettingsType.DOUBLE;
    private static final int FITNESS_TEST_COUNT = 5;
    private final String name = "Cart Single Pole Balance";
    private final int populationSize = 150;
    private final boolean metricsEmissionEnabled;

    @Override
    public NeatSettings createSettings(final Set<Integer> genomeIds, final ParallelEventLoop eventLoop) {
        return NeatSettings.builder()
                .general(GeneralSettings.builder()
                        .populationSize(populationSize)
                        .genesisGenomeTemplate(GenesisGenomeTemplate.builder()
                                .inputs(4)
                                .outputs(OUTPUT_TOPOLOGY_SETTINGS_TYPE.nodeCount)
                                .biases(List.of(1f))
                                .hiddenLayers(List.of())
                                .initialConnectionType(InitialConnectionType.FULLY_CONNECTED)
                                .initialWeightType(InitialWeightType.ALL_RANDOM)
                                .build())
                        .fitnessFunction((SecludedNeatEnvironment) genomeActivator -> {
                            genomeIds.add(genomeActivator.getGenome().getId());

                            return ENVIRONMENT_SETTINGS_TYPE.environment.test(genomeActivator);
                        })
                        .fitnessControllerFactory(AverageFitnessControllerFactory.getInstance())
                        .build())
                .parallelism(ParallelismSettings.builder()
                        .eventLoop(eventLoop)
                        .build())
                .nodeGenes(NodeGeneSettings.builder()
                        .hiddenActivationFunction(EnumValue.literal(ActivationFunctionType.RE_LU))
                        .build())
                .connectionGenes(ConnectionGeneSettings.builder()
                        .recurrentAllowanceRate(FloatNumber.literal(0f))
                        .build())
                .activation(ActivationSettings.builder()
                        .outputTopologyDefinition(OUTPUT_TOPOLOGY_SETTINGS_TYPE.topologyDefinition)
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
                        .maximumGeneration(75)
                        .maximumRestartCount(9)
                        .build())
                .add(new MetricCollectorTrainingPolicy(new MillisecondsDateTimeSupport()))
                .add(new DelegatedTrainingPolicy(ENVIRONMENT_SETTINGS_TYPE.trainingAssessor))
                .add(ContinuousTrainingPolicy.builder()
                        .fitnessTestCount(FITNESS_TEST_COUNT)
                        .build())
                .build();
    }

    private enum EnvironmentSettingsType {
        BALANCE_UNTIL_DONE(new BalanceUntilDoneObjective(TIME_BALANCING_GOAL, VALIDATION_SCENARIO_COUNT));

        EnvironmentSettingsType(final NeatObjective<SecludedNeatEnvironment> objective) {
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
