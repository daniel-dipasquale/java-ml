package com.dipasquale.ai.rl.neat;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class SettingsNeuralNetworkSupport {
    @Builder.Default
    private final SettingsNeuralNetworkType type = SettingsNeuralNetworkType.Default;

    ContextDefaultComponentFactory<ContextDefaultNeuralNetworkSupport> createFactory(final SettingsConnectionGeneSupport connections) {
        return c -> {
            NeuronFactory neuronFactory = NeuronDefault::new;

            if (connections.isRecurrentConnectionsAllowed()) {
                return new ContextDefaultNeuralNetworkSupport(g -> {
                    NeuronPathBuilder neuronPathBuilder = new NeuronPathBuilderRecurrent<>(NeuronDefault::createRecurrentSingleMemory);

                    return new NeuralNetworkDefault(g, neuronPathBuilder, neuronFactory);
                });
            }

            return new ContextDefaultNeuralNetworkSupport(g -> {
                NeuronPathBuilder neuronPathBuilder = new NeuronPathBuilderDefault();

                return new NeuralNetworkDefault(g, neuronPathBuilder, neuronFactory);
            });
        };
    }
}
