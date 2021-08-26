package com.dipasquale.ai.rl.neat.genotype;

import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.common.factory.FloatFactory;
import com.dipasquale.synchronization.dual.profile.ObjectProfile;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
public final class AllToAllOutputsGenesisGenomeConnector implements GenesisGenomeConnector, Serializable {
    @Serial
    private static final long serialVersionUID = 4200039748076194340L;
    private final ObjectProfile<FloatFactory> weightFactory;
    private final boolean shouldConnectBiasNodes;

    @Override
    public void setupConnections(final Genome genome, final Context.ConnectionGeneSupport connectionGeneSupport) {
        FloatFactory weightFactoryFixed = weightFactory.getObject();

        for (NodeGene inputNode : genome.getNodes(NodeGeneType.INPUT)) {
            for (NodeGene outputNode : genome.getNodes(NodeGeneType.OUTPUT)) {
                InnovationId innovationId = connectionGeneSupport.getOrCreateInnovationId(inputNode, outputNode);
                ConnectionGene connection = new ConnectionGene(innovationId, weightFactoryFixed.create());

                genome.addConnection(connection);
            }
        }

        if (shouldConnectBiasNodes) {
            for (NodeGene biasNode : genome.getNodes(NodeGeneType.BIAS)) {
                for (NodeGene outputNode : genome.getNodes(NodeGeneType.OUTPUT)) {
                    InnovationId innovationId = connectionGeneSupport.getOrCreateInnovationId(biasNode, outputNode);
                    ConnectionGene connection = new ConnectionGene(innovationId, 1f); // TODO: double check if this is correct

                    genome.addConnection(connection);
                }
            }
        }
    }
}
