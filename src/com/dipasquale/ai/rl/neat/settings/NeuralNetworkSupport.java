package com.dipasquale.ai.rl.neat.settings;

import com.dipasquale.ai.rl.neat.context.DefaultContextNeuralNetworkSupport;
import com.dipasquale.ai.rl.neat.phenotype.DefaultNeuron;
import com.dipasquale.ai.rl.neat.phenotype.FeedForwardNeuralNetworkFactory;
import com.dipasquale.ai.rl.neat.phenotype.NeuralNetworkFactory;
import com.dipasquale.ai.rl.neat.phenotype.NeuronFactory;
import com.dipasquale.ai.rl.neat.phenotype.RecurrentNeuralNetworkFactory;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter(AccessLevel.PACKAGE)
public final class NeuralNetworkSupport {
    @Builder.Default
    private final NeuralNetworkType type = NeuralNetworkType.MULTI_CYCLE_RECURRENT;

    DefaultContextNeuralNetworkSupport create() {
        NeuronFactory neuronFactory = (NeuronFactory & Serializable) DefaultNeuron::new;

        NeuralNetworkFactory neuralNetworkFactory = switch (type) {
            case FEED_FORWARD -> new FeedForwardNeuralNetworkFactory(neuronFactory);

            default -> new RecurrentNeuralNetworkFactory(neuronFactory);
        };

        return new DefaultContextNeuralNetworkSupport(neuralNetworkFactory);
    }
}
