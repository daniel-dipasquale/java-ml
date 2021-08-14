package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.rl.neat.genotype.ConnectionGeneMap;
import com.dipasquale.ai.rl.neat.genotype.DefaultGenome;
import com.dipasquale.ai.rl.neat.genotype.NodeGeneMap;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
public final class RecurrentNeuralNetworkFactory implements NeuralNetworkFactory, Serializable {
    @Serial
    private static final long serialVersionUID = -7222558913199103708L;
    private final NeuronFactory neuronFactory;

    @Override
    public NeuralNetwork create(final DefaultGenome genome, final NodeGeneMap nodes, final ConnectionGeneMap connections) {
        NeuronPromoter<DefaultNeuron> neuronPromoter = DefaultNeuron::cloneIntoSingleMemoryRecurrent;
        NeuronPathBuilder neuronPathBuilder = new RecurrentNeuronPathBuilder<>(neuronPromoter);

        return new DefaultNeuralNetwork(nodes, connections, neuronPathBuilder, neuronFactory);
    }
}
