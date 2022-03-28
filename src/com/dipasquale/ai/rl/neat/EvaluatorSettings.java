package com.dipasquale.ai.rl.neat;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter(AccessLevel.PACKAGE)
public final class EvaluatorSettings {
    @Builder.Default
    private final GeneralSupport general = GeneralSupport.builder()
            .build();

    @Builder.Default
    private final ParallelismSupport parallelism = ParallelismSupport.builder()
            .build();

    @Builder.Default
    private final RandomSupport random = RandomSupport.builder()
            .build();

    @Builder.Default
    private final NodeGeneSupport nodes = NodeGeneSupport.builder()
            .build();

    @Builder.Default
    private final ConnectionGeneSupport connections = ConnectionGeneSupport.builder()
            .build();

    @Builder.Default
    private final ActivationSupport activation = ActivationSupport.builder()
            .build();

    @Builder.Default
    private final MutationSupport mutation = MutationSupport.builder()
            .build();

    @Builder.Default
    private final CrossOverSupport crossOver = CrossOverSupport.builder()
            .build();

    @Builder.Default
    private final SpeciationSupport speciation = SpeciationSupport.builder()
            .build();

    @Builder.Default
    private final MetricsSupport metrics = MetricsSupport.builder()
            .build();

    Context createContext() {
        InitializationContext initializationContext = new InitializationContext(NeatEnvironmentType.from(general.getFitnessFunction()), parallelism, random);
        ContextObjectGeneralSupport fixedGeneral = general.create(initializationContext);
        ContextObjectParallelismSupport fixedParallelism = parallelism.create();
        ContextObjectRandomSupport fixedRandom = random.create(initializationContext);
        ContextObjectNodeGeneSupport fixedNodes = nodes.create(initializationContext, general.getGenesisGenomeTemplate(), connections);
        ContextObjectConnectionGeneSupport fixedConnections = connections.create(initializationContext, general.getGenesisGenomeTemplate());
        ContextObjectActivationSupport fixedActivation = activation.create(initializationContext, general, connections);
        ContextObjectMutationSupport fixedMutation = mutation.create(initializationContext);
        ContextObjectCrossOverSupport fixedCrossOver = crossOver.create(initializationContext);
        ContextObjectSpeciationSupport fixedSpeciation = speciation.create(initializationContext, general);
        ContextObjectMetricsSupport fixedMetrics = metrics.create(initializationContext, speciation);

        return new ContextObject(fixedGeneral, fixedParallelism, fixedRandom, fixedNodes, fixedConnections, fixedActivation, fixedMutation, fixedCrossOver, fixedSpeciation, fixedMetrics);
    }
}
