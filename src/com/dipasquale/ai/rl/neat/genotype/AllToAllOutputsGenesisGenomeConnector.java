package com.dipasquale.ai.rl.neat.genotype;

import com.dipasquale.ai.rl.neat.core.Context;
import com.dipasquale.common.factory.FloatFactory;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
public final class AllToAllOutputsGenesisGenomeConnector implements GenesisGenomeConnector, Serializable {
    @Serial
    private static final long serialVersionUID = 4200039748076194340L;
    private final FloatFactory weightFactory;
    private final boolean shouldConnectBiasNodes;

    private static Iterable<? extends NodeGene> getNodes(final Genome genome, final NodeGeneType type) {
        return () -> genome.getNodes().iterator(type);
    }

    @Override
    public void setupConnections(final Genome genome, final Context.ConnectionGeneSupport connectionGeneSupport) {
        for (NodeGene inputNode : getNodes(genome, NodeGeneType.INPUT)) {
            for (NodeGene outputNode : getNodes(genome, NodeGeneType.OUTPUT)) {
                InnovationId innovationId = connectionGeneSupport.provideInnovationId(inputNode, outputNode);
                ConnectionGene connection = new ConnectionGene(innovationId, weightFactory.create(), connectionGeneSupport.generateRecurrentWeights());

                genome.getConnections().put(connection);
            }
        }

        if (shouldConnectBiasNodes) {
            for (NodeGene biasNode : getNodes(genome, NodeGeneType.BIAS)) {
                for (NodeGene outputNode : getNodes(genome, NodeGeneType.OUTPUT)) {
                    InnovationId innovationId = connectionGeneSupport.provideInnovationId(biasNode, outputNode);
                    ConnectionGene connection = new ConnectionGene(innovationId, 1f, connectionGeneSupport.generateRecurrentWeights());

                    genome.getConnections().put(connection);
                }
            }
        }
    }
}
