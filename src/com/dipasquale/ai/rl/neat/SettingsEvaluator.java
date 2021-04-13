package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.ai.rl.neat.context.ContextDefault;
import com.dipasquale.ai.rl.neat.context.ContextDefaultConnectionGeneSupport;
import com.dipasquale.ai.rl.neat.context.ContextDefaultCrossOverSupport;
import com.dipasquale.ai.rl.neat.context.ContextDefaultGeneralSupport;
import com.dipasquale.ai.rl.neat.context.ContextDefaultMutationSupport;
import com.dipasquale.ai.rl.neat.context.ContextDefaultNeuralNetworkSupport;
import com.dipasquale.ai.rl.neat.context.ContextDefaultNodeGeneSupport;
import com.dipasquale.ai.rl.neat.context.ContextDefaultParallelismSupport;
import com.dipasquale.ai.rl.neat.context.ContextDefaultRandomSupport;
import com.dipasquale.ai.rl.neat.context.ContextDefaultSpeciationSupport;
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
    private final SettingsParallelismSupport parallelism = SettingsParallelismSupport.builder()
            .build();

    @Builder.Default
    private final SettingsRandomSupport random = SettingsRandomSupport.builder()
            .build();

    @Builder.Default
    private final SettingsMutationSupport mutation = SettingsMutationSupport.builder()
            .build();

    @Builder.Default
    private final SettingsCrossOverSupport crossOver = SettingsCrossOverSupport.builder()
            .build();

    @Builder.Default
    private final SettingsSpeciationSupport speciation = SettingsSpeciationSupport.builder()
            .build();

    Context createContext() {
        ContextDefaultGeneralSupport generalFixed = general.create(connections, parallelism);
        ContextDefaultNodeGeneSupport nodesFixed = nodes.create(general.getGenesisGenomeConnector(), parallelism);
        ContextDefaultConnectionGeneSupport connectionsFixed = connections.create(general.getGenesisGenomeConnector(), neuralNetwork, parallelism);
        ContextDefaultNeuralNetworkSupport neuralNetworkFixed = neuralNetwork.create();
        ContextDefaultParallelismSupport parallelismFixed = parallelism.create();
        ContextDefaultRandomSupport randomFixed = random.create(parallelism);
        ContextDefaultMutationSupport mutationFixed = mutation.create(parallelism, random);
        ContextDefaultCrossOverSupport crossOverFixed = crossOver.create(parallelism, random);
        ContextDefaultSpeciationSupport speciationFixed = speciation.create(general, parallelism);

        return new ContextDefault(generalFixed, nodesFixed, connectionsFixed, neuralNetworkFixed, parallelismFixed, randomFixed, mutationFixed, crossOverFixed, speciationFixed);
    }
}
