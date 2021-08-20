package com.dipasquale.ai.rl.neat.settings;

import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.ai.rl.neat.context.DefaultContext;
import com.dipasquale.ai.rl.neat.context.DefaultContextConnectionGeneSupport;
import com.dipasquale.ai.rl.neat.context.DefaultContextCrossOverSupport;
import com.dipasquale.ai.rl.neat.context.DefaultContextGeneralSupport;
import com.dipasquale.ai.rl.neat.context.DefaultContextMutationSupport;
import com.dipasquale.ai.rl.neat.context.DefaultContextNeuralNetworkSupport;
import com.dipasquale.ai.rl.neat.context.DefaultContextNodeGeneSupport;
import com.dipasquale.ai.rl.neat.context.DefaultContextRandomSupport;
import com.dipasquale.ai.rl.neat.context.DefaultContextSpeciationSupport;
import com.dipasquale.ai.rl.neat.context.StrategyContextParallelismSupport;
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
    private final NeuralNetworkSupport neuralNetwork = NeuralNetworkSupport.builder()
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

    public Context createContext() { // TODO: think of a better fix for this
        DefaultContextGeneralSupport generalFixed = general.create();
        DefaultContextNodeGeneSupport nodesFixed = nodes.create(general.getGenesisGenomeFactory(), parallelism);
        DefaultContextConnectionGeneSupport connectionsFixed = connections.create(general.getGenesisGenomeFactory(), neuralNetwork, parallelism);
        DefaultContextNeuralNetworkSupport neuralNetworkFixed = neuralNetwork.create();
        StrategyContextParallelismSupport parallelismFixed = parallelism.create();
        DefaultContextRandomSupport randomFixed = random.create(parallelism);
        DefaultContextMutationSupport mutationFixed = mutation.create(parallelism, random);
        DefaultContextCrossOverSupport crossOverFixed = crossOver.create(parallelism, random);
        DefaultContextSpeciationSupport speciationFixed = speciation.create(general, parallelism, random);

        return new DefaultContext(generalFixed, nodesFixed, connectionsFixed, neuralNetworkFixed, parallelismFixed, randomFixed, mutationFixed, crossOverFixed, speciationFixed);
    }
}
