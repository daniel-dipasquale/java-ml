package com.dipasquale.ai.rl.neat.settings;

import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.ai.rl.neat.context.DefaultContext;
import com.dipasquale.ai.rl.neat.context.DefaultContextActivationSupport;
import com.dipasquale.ai.rl.neat.context.DefaultContextConnectionGeneSupport;
import com.dipasquale.ai.rl.neat.context.DefaultContextCrossOverSupport;
import com.dipasquale.ai.rl.neat.context.DefaultContextGeneralSupport;
import com.dipasquale.ai.rl.neat.context.DefaultContextMetricSupport;
import com.dipasquale.ai.rl.neat.context.DefaultContextMutationSupport;
import com.dipasquale.ai.rl.neat.context.DefaultContextNodeGeneSupport;
import com.dipasquale.ai.rl.neat.context.DefaultContextParallelismSupport;
import com.dipasquale.ai.rl.neat.context.DefaultContextRandomSupport;
import com.dipasquale.ai.rl.neat.context.DefaultContextSpeciationSupport;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public final class EvaluatorSettings {
    @Builder.Default
    private final GeneralEvaluatorSupport general = GeneralEvaluatorSupport.builder()
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
    private final ParallelismSupport parallelism = ParallelismSupport.builder()
            .build();

    @Builder.Default
    private final RandomSupport random = RandomSupport.builder()
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
    private final MetricSupport metrics = MetricSupport.builder()
            .build();

    public Context createContext() { // TODO: think of a better fix for this (as a reminder, the fact this is public, is not ideal)
        DefaultContextGeneralSupport generalFixed = general.create();
        DefaultContextNodeGeneSupport nodesFixed = nodes.create(general.getGenesisGenomeTemplate(), parallelism);
        DefaultContextConnectionGeneSupport connectionsFixed = connections.create(general.getGenesisGenomeTemplate(), activation, parallelism);
        DefaultContextActivationSupport activationFixed = activation.create(general, parallelism);
        DefaultContextParallelismSupport parallelismFixed = parallelism.create();
        DefaultContextRandomSupport randomFixed = random.create(parallelism);
        DefaultContextMutationSupport mutationFixed = mutation.create(parallelism, randomFixed.getFloatRandomSupportProfile());
        DefaultContextCrossOverSupport crossOverFixed = crossOver.create(parallelism, randomFixed.getFloatRandomSupportProfile());
        DefaultContextSpeciationSupport speciationFixed = speciation.create(parallelism, randomFixed.getFloatRandomSupportProfile());
        DefaultContextMetricSupport metricsFixed = metrics.create(parallelism);

        return new DefaultContext(generalFixed, nodesFixed, connectionsFixed, activationFixed, parallelismFixed, randomFixed, mutationFixed, crossOverFixed, speciationFixed, metricsFixed);
    }
}
