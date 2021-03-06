package com.dipasquale.ai.rl.neat;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class Settings<T extends Comparable<T>> {
    private final SettingsGeneralSupport general;
    private final SettingsNodeGeneSupport<T> nodes;
    private final SettingsConnectionGeneSupport<T> connections;
    private final SettingsNeuralNetworkSupport neuralNetwork;
    private final SettingsRandom random;
    private final SettingsMutation mutation;
    private final SettingsCrossOver crossOver;
    private final SettingsSpeciation speciation;

    Context<T> createContext() {
        ContextDefaultComponentFactory<T, ContextDefaultGeneralSupport<T>> generalFactory = general.createFactory();
        ContextDefaultComponentFactory<T, ContextDefaultNodeGeneSupport<T>> nodesFactory = nodes.createFactory(general.getGenomeFactory());
        ContextDefaultComponentFactory<T, ContextDefaultConnectionGeneSupport<T>> connectionsFactory = connections.createFactory();
        ContextDefaultComponentFactory<T, ContextDefaultNeuralNetworkSupport<T>> neuralNetworkFactory = neuralNetwork.createFactory();
        ContextDefaultComponentFactory<T, ContextDefaultRandom> randomFactory = random.createFactory();
        ContextDefaultComponentFactory<T, ContextDefaultMutation> mutationFactory = mutation.createFactory();
        ContextDefaultComponentFactory<T, ContextDefaultCrossOver<T>> crossOverFactory = crossOver.createFactory();
        ContextDefaultComponentFactory<T, ContextDefaultSpeciation<T>> speciationFactory = speciation.createFactory();

        return new ContextDefault<>(generalFactory, nodesFactory, connectionsFactory, neuralNetworkFactory, randomFactory, mutationFactory, crossOverFactory, speciationFactory);
    }
}
