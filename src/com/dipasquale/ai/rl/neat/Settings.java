package com.dipasquale.ai.rl.neat;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class Settings {
    private final SettingsGeneralSupport general;
    private final SettingsNodeGeneSupport nodes;
    private final SettingsConnectionGeneSupport connections;
    private final SettingsNeuralNetworkSupport neuralNetwork;
    private final SettingsRandom random;
    private final SettingsMutation mutation;
    private final SettingsCrossOver crossOver;
    private final SettingsSpeciation speciation;

    Context createContext() {
        ContextDefaultComponentFactory<ContextDefaultGeneralSupport> generalFactory = general.createFactory();
        ContextDefaultComponentFactory<ContextDefaultNodeGeneSupport> nodesFactory = nodes.createFactory(general.getGenomeFactory());
        ContextDefaultComponentFactory<ContextDefaultConnectionGeneSupport> connectionsFactory = connections.createFactory();
        ContextDefaultComponentFactory<ContextDefaultNeuralNetworkSupport> neuralNetworkFactory = neuralNetwork.createFactory();
        ContextDefaultComponentFactory<ContextDefaultRandom> randomFactory = random.createFactory();
        ContextDefaultComponentFactory<ContextDefaultMutation> mutationFactory = mutation.createFactory();
        ContextDefaultComponentFactory<ContextDefaultCrossOver> crossOverFactory = crossOver.createFactory();
        ContextDefaultComponentFactory<ContextDefaultSpeciation> speciationFactory = speciation.createFactory();

        return new ContextDefault(generalFactory, nodesFactory, connectionsFactory, neuralNetworkFactory, randomFactory, mutationFactory, crossOverFactory, speciationFactory);
    }
}
