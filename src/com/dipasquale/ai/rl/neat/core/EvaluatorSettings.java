package com.dipasquale.ai.rl.neat.core;

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
    private final MetricSupport metrics = MetricSupport.builder()
            .build();

    Context createContext() {
        DefaultContextRandomSupport randomFixed = random.create(parallelism);
        InitializationContext initializationContext = new InitializationContext(NeatEnvironmentType.from(general.getFitnessFunction()), parallelism, randomFixed.getRandomSupports(), randomFixed.getRandomSupport());
        DefaultContextGeneralSupport generalFixed = general.create(initializationContext);
        DefaultContextParallelismSupport parallelismFixed = parallelism.create();
        DefaultContextNodeGeneSupport nodesFixed = nodes.create(initializationContext, general.getGenesisGenomeTemplate());
        DefaultContextConnectionGeneSupport connectionsFixed = connections.create(initializationContext, general.getGenesisGenomeTemplate());
        DefaultContextActivationSupport activationFixed = activation.create(initializationContext, general, connections);
        DefaultContextMutationSupport mutationFixed = mutation.create(initializationContext);
        DefaultContextCrossOverSupport crossOverFixed = crossOver.create(initializationContext);
        DefaultContextSpeciationSupport speciationFixed = speciation.create(initializationContext, general);
        DefaultContextMetricSupport metricsFixed = metrics.create(initializationContext, speciation);

        return new DefaultContext(generalFixed, parallelismFixed, randomFixed, nodesFixed, connectionsFixed, activationFixed, mutationFixed, crossOverFixed, speciationFixed, metricsFixed);
    }
}
