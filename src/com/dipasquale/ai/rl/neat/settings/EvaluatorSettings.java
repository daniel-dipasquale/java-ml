/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.ai.rl.neat.settings;

import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.ai.rl.neat.context.DefaultConnectionGeneSupportContext;
import com.dipasquale.ai.rl.neat.context.DefaultContext;
import com.dipasquale.ai.rl.neat.context.DefaultCrossOverSupportContext;
import com.dipasquale.ai.rl.neat.context.DefaultGeneralSupportContext;
import com.dipasquale.ai.rl.neat.context.DefaultMutationSupportContext;
import com.dipasquale.ai.rl.neat.context.DefaultNeuralNetworkSupportContext;
import com.dipasquale.ai.rl.neat.context.DefaultNodeGeneSupportContext;
import com.dipasquale.ai.rl.neat.context.DefaultParallelismSupportContext;
import com.dipasquale.ai.rl.neat.context.DefaultRandomSupportContext;
import com.dipasquale.ai.rl.neat.context.DefaultSpeciationSupportContext;
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
        DefaultGeneralSupportContext generalFixed = general.create();
        DefaultNodeGeneSupportContext nodesFixed = nodes.create(general.getGenesisGenomeFactory(), parallelism);
        DefaultConnectionGeneSupportContext connectionsFixed = connections.create(general.getGenesisGenomeFactory(), neuralNetwork, parallelism);
        DefaultNeuralNetworkSupportContext neuralNetworkFixed = neuralNetwork.create();
        DefaultParallelismSupportContext parallelismFixed = parallelism.create();
        DefaultRandomSupportContext randomFixed = random.create(parallelism);
        DefaultMutationSupportContext mutationFixed = mutation.create(parallelism, random);
        DefaultCrossOverSupportContext crossOverFixed = crossOver.create(parallelism, random);
        DefaultSpeciationSupportContext speciationFixed = speciation.create(general, parallelism);

        return new DefaultContext(generalFixed, nodesFixed, connectionsFixed, neuralNetworkFixed, parallelismFixed, randomFixed, mutationFixed, crossOverFixed, speciationFixed);
    }
}
