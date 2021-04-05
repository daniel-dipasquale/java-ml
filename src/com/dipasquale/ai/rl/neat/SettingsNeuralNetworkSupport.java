package com.dipasquale.ai.rl.neat;

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
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter(AccessLevel.PACKAGE)
public final class SettingsNeuralNetworkSupport {
    @Builder.Default
    private final SettingsNeuralNetworkType type = SettingsNeuralNetworkType.FEED_FORWARD;

    private static NeuralNetworkFactory createFeedForwardNeuralNetworkFactory(final NeuronFactory neuronFactory) {
        return (genome, nodes, connections) -> {
            NeuronPathBuilder neuronPathBuilder = new NeuronPathBuilderDefault();

            return new NeuralNetworkDefault(nodes, connections, neuronPathBuilder, neuronFactory);
        };
    }

    private static NeuralNetworkFactory createRecurrentNeuralNetworkFactory(final NeuronFactory neuronFactory) {
        return (genome, nodes, connections) -> {
            NeuronPathBuilder neuronPathBuilder = new NeuronPathBuilderRecurrent<>(NeuronDefault::createRecurrentSingleMemory);

            return new NeuralNetworkDefault(nodes, connections, neuronPathBuilder, neuronFactory);
        };
    }

    ContextDefaultNeuralNetworkSupport create() {
        NeuronFactory neuronFactory = NeuronDefault::new;

        NeuralNetworkFactory neuralNetworkFactory = switch (type) {
            case FEED_FORWARD -> createFeedForwardNeuralNetworkFactory(neuronFactory);

            default -> createRecurrentNeuralNetworkFactory(neuronFactory);
        };

        return new ContextDefaultNeuralNetworkSupport(neuralNetworkFactory);
    }
}
