package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.ai.rl.neat.genotype.ConnectionGeneMap;
import com.dipasquale.ai.rl.neat.genotype.GenomeDefault;
import com.dipasquale.ai.rl.neat.genotype.NodeGeneMap;
import com.dipasquale.ai.rl.neat.phenotype.NeuralNetwork;
import com.dipasquale.ai.rl.neat.phenotype.NeuralNetworkFactory;
import com.dipasquale.data.structure.map.SerializableInteroperableStateMap;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public final class ContextDefaultNeuralNetworkSupport implements Context.NeuralNetworkSupport {
    private NeuralNetworkFactory factory;

    @Override
    public NeuralNetwork create(final GenomeDefault genome, final NodeGeneMap nodes, final ConnectionGeneMap connections) {
        return factory.create(genome, nodes, connections);
    }

    public void save(final SerializableInteroperableStateMap state) {
        state.put("neuralNetwork.factory", factory);
    }

    public void load(final SerializableInteroperableStateMap state) {
        factory = state.get("neuralNetwork.factory");
    }
}
