package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.rl.neat.genotype.Genome;
import com.dipasquale.common.factory.ObjectFactory;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
public final class FeedForwardNeuralNetworkFactory implements NeuralNetworkFactory, Serializable {
    @Serial
    private static final long serialVersionUID = -35249703641428233L;

    @Override
    public NeuralNetwork create(final Genome genome) {
        NeuronPathBuilder neuronPathBuilder = new FeedForwardNeuronPathBuilder();
        ObjectFactory<NeuronValueGroup> neuronValuesFactory = (ObjectFactory<NeuronValueGroup> & Serializable) FeedForwardNeuronValueGroup::new;

        return new DefaultNeuralNetwork(genome, neuronPathBuilder, neuronValuesFactory);
    }
}
