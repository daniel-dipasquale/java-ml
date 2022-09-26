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
    private final ParallelismSettings parallelism = ParallelismSettings.builder()
            .build();

    @Builder.Default
    private final RandomnessSettings randomness = RandomnessSettings.builder()
            .build();

    @Builder.Default
    private final ActivationSettings activation = ActivationSettings.builder()
            .build();

    @Builder.Default
    private final NodeGeneSettings nodeGenes = NodeGeneSettings.builder()
            .build();

    @Builder.Default
    private final ConnectionGeneSettings connectionGenes = ConnectionGeneSettings.builder()
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

    DefaultNeatContext createContext() {
        NeatInitializationContext initializationContext = new NeatInitializationContext(parallelism, randomness, activation);
        DefaultNeatContextParallelismSupport parallelismSupport = parallelism.create();
        DefaultNeatContextRandomnessSupport randomnessSupport = randomness.create(initializationContext);
        DefaultNeatContextActivationSupport activationSupport = activation.create(initializationContext, connectionGenes);
        DefaultNeatContextNodeGeneSupport nodeGeneSupport = nodeGenes.create(initializationContext, activation.getGenesisGenomeTemplate(), connectionGenes);
        DefaultNeatContextConnectionGeneSupport connectionGeneSupport = connectionGenes.create(initializationContext, activation.getGenesisGenomeTemplate());
        DefaultNeatContextMutationSupport mutationSupport = mutation.create(initializationContext);
        DefaultNeatContextCrossOverSupport crossOverSupport = crossOver.create(initializationContext);
        DefaultNeatContextSpeciationSupport speciationSupport = speciation.create(initializationContext);
        DefaultNeatContextMetricsSupport metricsSupport = metrics.create(initializationContext, speciation);

        return new DefaultNeatContext(parallelismSupport, randomnessSupport, activationSupport, nodeGeneSupport, connectionGeneSupport, mutationSupport, crossOverSupport, speciationSupport, metricsSupport);
    }
}
