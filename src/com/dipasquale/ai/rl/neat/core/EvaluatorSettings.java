package com.dipasquale.ai.rl.neat.core;

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
        ContextObjectGeneralSupport generalFixed = general.create(initializationContext);
        ContextObjectParallelismSupport parallelismFixed = parallelism.create();
        ContextObjectRandomSupport randomFixed = random.create(initializationContext);
        ContextObjectNodeGeneSupport nodesFixed = nodes.create(initializationContext, general.getGenesisGenomeTemplate(), connections);
        ContextObjectConnectionGeneSupport connectionsFixed = connections.create(initializationContext, general.getGenesisGenomeTemplate());
        ContextObjectActivationSupport activationFixed = activation.create(initializationContext, general, connections);
        ContextObjectMutationSupport mutationFixed = mutation.create(initializationContext);
        ContextObjectCrossOverSupport crossOverFixed = crossOver.create(initializationContext);
        ContextObjectSpeciationSupport speciationFixed = speciation.create(initializationContext, general);
        ContextObjectMetricsSupport metricsFixed = metrics.create(initializationContext, speciation);

        return new ContextObject(generalFixed, parallelismFixed, randomFixed, nodesFixed, connectionsFixed, activationFixed, mutationFixed, crossOverFixed, speciationFixed, metricsFixed);
    }
}
