package com.dipasquale.ai.rl.neat;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class Settings<T extends Comparable<T>> {
    private final SettingsGeneralSupport<T> general;
    private final SettingsNodeGeneSupport<T> nodes;

    Context<T> createContext() {
        ContextDefaultComponentFactory<T, ContextDefaultGeneralSupport<T>> generalFactory = general.createFactory();
        ContextDefaultComponentFactory<T, ContextDefaultNodeGeneSupport<T>> nodesFactory = nodes.createFactory(general.getGenomeFactory());
        ContextDefaultComponentFactory<T, ContextDefaultConnectionGeneSupport<T>> connectionsFactory = c -> new ContextDefaultConnectionGeneSupport<>(false, null, null, null, null);
        ContextDefaultComponentFactory<T, ContextDefaultNeuralNetworkSupport<T>> neuralNetworkFactory = c -> new ContextDefaultNeuralNetworkSupport<>(null);
        ContextDefaultComponentFactory<T, ContextDefaultRandom> randomFactory = c -> new ContextDefaultRandom(null, null, null);
        ContextDefaultComponentFactory<T, ContextDefaultMutation> mutationFactory = c -> new ContextDefaultMutation(0f, 0f, 0f, 0f);
        ContextDefaultComponentFactory<T, ContextDefaultCrossOver<T>> crossOverFactory = c -> new ContextDefaultCrossOver<>(0f, 0f, 0f, null);
        ContextDefaultComponentFactory<T, ContextDefaultSpeciation<T>> speciationFactory = c -> new ContextDefaultSpeciation<>(0, 0f, 0f, 0f, 0f, 0f, null, 0f, 0f, 0, 0, 0f);

        return new ContextDefault<>(generalFactory, nodesFactory, connectionsFactory, neuralNetworkFactory, randomFactory, mutationFactory, crossOverFactory, speciationFactory);
    }
}
