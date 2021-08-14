package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.ai.rl.neat.genotype.ConnectionGeneMap;
import com.dipasquale.ai.rl.neat.genotype.DefaultGenome;
import com.dipasquale.ai.rl.neat.genotype.NodeGeneMap;
import com.dipasquale.ai.rl.neat.phenotype.NeuralNetwork;
import com.dipasquale.ai.rl.neat.phenotype.NeuralNetworkFactory;
import com.dipasquale.common.SerializableInteroperableStateMap;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public final class DefaultNeuralNetworkSupportContext implements Context.NeuralNetworkSupport {
    private NeuralNetworkFactory factory;

    @Override
    public NeuralNetwork create(final DefaultGenome genome, final NodeGeneMap nodes, final ConnectionGeneMap connections) {
        return factory.create(genome, nodes, connections);
    }

    public void save(final SerializableInteroperableStateMap state) {
        state.put("neuralNetwork.factory", factory);
    }

    public void load(final SerializableInteroperableStateMap state) {
        factory = state.get("neuralNetwork.factory");
    }
}
