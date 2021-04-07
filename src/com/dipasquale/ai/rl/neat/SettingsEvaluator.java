package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.ai.rl.neat.context.ContextDefault;
import com.dipasquale.ai.rl.neat.context.ContextDefaultConnectionGeneSupport;
import com.dipasquale.ai.rl.neat.context.ContextDefaultCrossOver;
import com.dipasquale.ai.rl.neat.context.ContextDefaultGeneralSupport;
import com.dipasquale.ai.rl.neat.context.ContextDefaultMutation;
import com.dipasquale.ai.rl.neat.context.ContextDefaultNeuralNetworkSupport;
import com.dipasquale.ai.rl.neat.context.ContextDefaultNodeGeneSupport;
import com.dipasquale.ai.rl.neat.context.ContextDefaultParallelism;
import com.dipasquale.ai.rl.neat.context.ContextDefaultRandom;
import com.dipasquale.ai.rl.neat.context.ContextDefaultSpeciation;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public final class SettingsEvaluator {
    private final SettingsGeneralEvaluatorSupport general;

    @Builder.Default
    private final SettingsNodeGeneSupport nodes = SettingsNodeGeneSupport.builder()
            .build();

    @Builder.Default
    private final SettingsConnectionGeneSupport connections = SettingsConnectionGeneSupport.builder()
            .build();

    @Builder.Default
    private final SettingsNeuralNetworkSupport neuralNetwork = SettingsNeuralNetworkSupport.builder()
            .build();

    @Builder.Default
    private final SettingsParallelism parallelism = SettingsParallelism.builder()
            .build();

    @Builder.Default
    private final SettingsRandom random = SettingsRandom.builder()
            .build();

    @Builder.Default
    private final SettingsMutation mutation = SettingsMutation.builder()
            .build();

    @Builder.Default
    private final SettingsCrossOver crossOver = SettingsCrossOver.builder()
            .build();

    @Builder.Default
    private final SettingsSpeciation speciation = SettingsSpeciation.builder()
            .build();

    Context createContext() {
        ContextDefaultGeneralSupport generalFixed = general.create(connections, parallelism);
        ContextDefaultNodeGeneSupport nodesFixed = nodes.create(general.getGenomeFactory(), parallelism);
        ContextDefaultConnectionGeneSupport connectionsFixed = connections.create(neuralNetwork, parallelism);
        ContextDefaultNeuralNetworkSupport neuralNetworkFixed = neuralNetwork.create();
        ContextDefaultParallelism parallelismFixed = parallelism.create();
        ContextDefaultRandom randomFixed = random.create(parallelism);
        ContextDefaultMutation mutationFixed = mutation.create(parallelism, random);
        ContextDefaultCrossOver crossOverFixed = crossOver.create(parallelism, random);
        ContextDefaultSpeciation speciationFixed = speciation.create(general, parallelism);

        return new ContextDefault(generalFixed, nodesFixed, connectionsFixed, neuralNetworkFixed, parallelismFixed, randomFixed, mutationFixed, crossOverFixed, speciationFixed);
    }
}
