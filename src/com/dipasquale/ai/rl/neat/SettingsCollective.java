package com.dipasquale.ai.rl.neat;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class SettingsCollective {
    private final SettingsGeneralSupport general;

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
        ContextDefaultComponentFactory<ContextDefaultGeneralSupport> generalFactory = general.createFactory();
        ContextDefaultComponentFactory<ContextDefaultNodeGeneSupport> nodesFactory = nodes.createFactory(general.getGenomeFactory());
        ContextDefaultComponentFactory<ContextDefaultConnectionGeneSupport> connectionsFactory = connections.createFactory();
        ContextDefaultComponentFactory<ContextDefaultNeuralNetworkSupport> neuralNetworkFactory = neuralNetwork.createFactory(connections);
        ContextDefaultComponentFactory<ContextDefaultRandom> randomFactory = random.createFactory();
        ContextDefaultComponentFactory<ContextDefaultMutation> mutationFactory = mutation.createFactory();
        ContextDefaultComponentFactory<ContextDefaultCrossOver> crossOverFactory = crossOver.createFactory();
        ContextDefaultComponentFactory<ContextDefaultSpeciation> speciationFactory = speciation.createFactory(general);

        return new ContextDefault(generalFactory, nodesFactory, connectionsFactory, neuralNetworkFactory, randomFactory, mutationFactory, crossOverFactory, speciationFactory);
    }
}
