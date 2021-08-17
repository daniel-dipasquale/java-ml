package com.dipasquale.ai.rl.neat.settings;

import com.dipasquale.ai.rl.neat.context.DefaultContextNeuralNetworkSupport;
import com.dipasquale.ai.rl.neat.phenotype.FeedForwardNeuralNetworkFactory;
import com.dipasquale.ai.rl.neat.phenotype.NeuralNetworkFactory;
import com.dipasquale.ai.rl.neat.phenotype.RecurrentNeuralNetworkFactory;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter(AccessLevel.PACKAGE)
public final class NeuralNetworkSupport {
    @Builder.Default
    private final NeuralNetworkType type = NeuralNetworkType.MULTI_CYCLE_RECURRENT;

    DefaultContextNeuralNetworkSupport create() {
        NeuralNetworkFactory neuralNetworkFactory = switch (type) {
            case FEED_FORWARD -> new FeedForwardNeuralNetworkFactory();

            default -> new RecurrentNeuralNetworkFactory();
        };

        return new DefaultContextNeuralNetworkSupport(neuralNetworkFactory);
    }
}
