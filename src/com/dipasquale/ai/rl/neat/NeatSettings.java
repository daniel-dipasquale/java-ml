package com.dipasquale.ai.rl.neat;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter(AccessLevel.PACKAGE)
public final class NeatSettings {
    @Builder.Default
    private final GeneralSettings general = GeneralSettings.builder()
            .build();

    @Builder.Default
    private final ParallelismSettings parallelism = ParallelismSettings.builder()
            .build();

    @Builder.Default
    private final RandomnessSettings randomness = RandomnessSettings.builder()
            .build();

    @Builder.Default
    private final NodeGeneSettings nodeGenes = NodeGeneSettings.builder()
            .build();

    @Builder.Default
    private final ConnectionGeneSettings connectionGenes = ConnectionGeneSettings.builder()
            .build();

    @Builder.Default
    private final ActivationSettings activation = ActivationSettings.builder()
            .build();

    @Builder.Default
    private final MutationSettings mutation = MutationSettings.builder()
            .build();

    @Builder.Default
    private final CrossOverSettings crossOver = CrossOverSettings.builder()
            .build();

    @Builder.Default
    private final SpeciationSettings speciation = SpeciationSettings.builder()
            .build();

    @Builder.Default
    private final MetricsSettings metrics = MetricsSettings.builder()
            .build();

    Context createContext() {
        InitializationContext initializationContext = new InitializationContext(general, parallelism, randomness);
        ContextObjectGeneralSupport generalSupport = general.create();
        ContextObjectParallelismSupport parallelismSupport = parallelism.create();
        ContextObjectRandomnessSupport randomnessSupport = randomness.create(initializationContext);
        ContextObjectNodeGeneSupport nodeGeneSupport = nodeGenes.create(initializationContext, general.getGenesisGenomeTemplate(), connectionGenes);
        ContextObjectConnectionGeneSupport connectionGeneSupport = connectionGenes.create(initializationContext, general.getGenesisGenomeTemplate());
        ContextObjectActivationSupport activationSupport = activation.create(initializationContext, connectionGenes);
        ContextObjectMutationSupport mutationSupport = mutation.create(initializationContext);
        ContextObjectCrossOverSupport crossOverSupport = crossOver.create(initializationContext);
        ContextObjectSpeciationSupport speciationSupport = speciation.create(initializationContext, general);
        ContextObjectMetricsSupport metricsSupport = metrics.create(initializationContext, speciation);

        return new ContextObject(generalSupport, parallelismSupport, randomnessSupport, nodeGeneSupport, connectionGeneSupport, activationSupport, mutationSupport, crossOverSupport, speciationSupport, metricsSupport);
    }
}
