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
    private final GeneralEvaluatorSupportSettings general = GeneralEvaluatorSupportSettings.builder()
            .build();

    @Builder.Default
    private final NodeGeneSupportSettings nodes = NodeGeneSupportSettings.builder()
            .build();

    @Builder.Default
    private final ConnectionGeneSupportSettings connections = ConnectionGeneSupportSettings.builder()
            .build();

    @Builder.Default
    private final NeuralNetworkSupportSettings neuralNetwork = NeuralNetworkSupportSettings.builder()
            .build();

    @Builder.Default
    private final ParallelismSupportSettings parallelism = ParallelismSupportSettings.builder()
            .build();

    @Builder.Default
    private final RandomSupportSettings random = RandomSupportSettings.builder()
            .build();

    @Builder.Default
    private final MutationSupportSettings mutation = MutationSupportSettings.builder()
            .build();

    @Builder.Default
    private final CrossOverSupportSettings crossOver = CrossOverSupportSettings.builder()
            .build();

    @Builder.Default
    private final SpeciationSupportSettings speciation = SpeciationSupportSettings.builder()
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