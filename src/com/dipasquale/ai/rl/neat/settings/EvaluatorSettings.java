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
    private final MetricSupport metrics = MetricSupport.builder()
            .build();

    public Context createContext() { // TODO: think of a better fix for this (as a reminder, the fact this is public, is not ideal)
        DefaultContextRandomSupport randomFixed = random.create(parallelism);
        DefaultContextGeneralSupport generalFixed = general.create(parallelism, randomFixed.getRandomSupports());
        DefaultContextParallelismSupport parallelismFixed = parallelism.create();
        DefaultContextNodeGeneSupport nodesFixed = nodes.create(general.getGenesisGenomeTemplate(), parallelism, randomFixed.getRandomSupports(), randomFixed.getRandomSupport());
        DefaultContextConnectionGeneSupport connectionsFixed = connections.create(general.getGenesisGenomeTemplate(), parallelism, randomFixed.getRandomSupports(), randomFixed.getRandomSupport());
        DefaultContextActivationSupport activationFixed = activation.create(general, connections, parallelism, randomFixed.getRandomSupports());
        DefaultContextMutationSupport mutationFixed = mutation.create(parallelism, randomFixed.getRandomSupports(), randomFixed.getRandomSupport());
        DefaultContextCrossOverSupport crossOverFixed = crossOver.create(parallelism, randomFixed.getRandomSupports(), randomFixed.getRandomSupport());
        DefaultContextSpeciationSupport speciationFixed = speciation.create(general, parallelism, randomFixed.getRandomSupports(), randomFixed.getRandomSupport());
        DefaultContextMetricSupport metricsFixed = metrics.create(speciation, parallelism, randomFixed.getRandomSupports());

        return new DefaultContext(generalFixed, parallelismFixed, randomFixed, nodesFixed, connectionsFixed, activationFixed, mutationFixed, crossOverFixed, speciationFixed, metricsFixed);
    }
}
