package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.rl.neat.context.ContextDefaultComponentFactory;
import com.dipasquale.ai.rl.neat.context.ContextDefaultNeuralNetworkSupport;
import com.dipasquale.ai.rl.neat.phenotype.NeuralNetworkDefault;
import com.dipasquale.ai.rl.neat.phenotype.NeuralNetworkFactory;
import com.dipasquale.ai.rl.neat.phenotype.NeuronDefault;
import com.dipasquale.ai.rl.neat.phenotype.NeuronFactory;
import com.dipasquale.ai.rl.neat.phenotype.NeuronPathBuilder;
import com.dipasquale.ai.rl.neat.phenotype.NeuronPathBuilderDefault;
import com.dipasquale.ai.rl.neat.phenotype.NeuronPathBuilderRecurrent;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class SettingsNeuralNetworkSupport {
    @Builder.Default
    private final SettingsNeuralNetworkType type = SettingsNeuralNetworkType.Default;

    private static NeuralNetworkFactory createRecurrentNeuralNetworkFactory(final NeuronFactory neuronFactory) {
        return (genome, nodes, connections) -> {
            NeuronPathBuilder neuronPathBuilder = new NeuronPathBuilderRecurrent<>(NeuronDefault::createRecurrentSingleMemory);

            return new NeuralNetworkDefault(genome, nodes, connections, neuronPathBuilder, neuronFactory);
        };
    }

    private static NeuralNetworkFactory createDefaultNeuralNetworkFactory(final NeuronFactory neuronFactory) {
        return (genome, nodes, connections) -> {
            NeuronPathBuilder neuronPathBuilder = new NeuronPathBuilderDefault();

            return new NeuralNetworkDefault(genome, nodes, connections, neuronPathBuilder, neuronFactory);
        };
    }

    ContextDefaultComponentFactory<ContextDefaultNeuralNetworkSupport> createFactory(final SettingsConnectionGeneSupport connections) {
        return context -> {
            NeuronFactory neuronFactory = NeuronDefault::new;

            if (connections.isRecurrentConnectionsAllowed()) {
                return new ContextDefaultNeuralNetworkSupport(createRecurrentNeuralNetworkFactory(neuronFactory));
            }

            return new ContextDefaultNeuralNetworkSupport(createDefaultNeuralNetworkFactory(neuronFactory));
        };
    }
}
