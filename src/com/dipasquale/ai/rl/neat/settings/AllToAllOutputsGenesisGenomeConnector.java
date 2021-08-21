package com.dipasquale.ai.rl.neat.settings;

import com.dipasquale.ai.rl.neat.genotype.ConnectionGene;
import com.dipasquale.ai.rl.neat.genotype.DefaultGenome;
import com.dipasquale.ai.rl.neat.genotype.GenomeGenesisConnector;
import com.dipasquale.ai.rl.neat.genotype.GenomeHistoricalMarkings;
import com.dipasquale.ai.rl.neat.genotype.InnovationId;
import com.dipasquale.ai.rl.neat.genotype.NodeGene;
import com.dipasquale.ai.rl.neat.genotype.NodeGeneType;
import com.dipasquale.common.factory.FloatFactory;
import com.dipasquale.common.profile.ObjectProfile;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class AllToAllOutputsGenesisGenomeConnector implements GenomeGenesisConnector, Serializable {
    @Serial
    private static final long serialVersionUID = 4200039748076194340L;
    private final ObjectProfile<FloatFactory> weightFactory;
    private final boolean shouldConnectBiasNodes;

    @Override
    public void setupConnections(final DefaultGenome genome, final GenomeHistoricalMarkings historicalMarkings) {
        FloatFactory weightFactoryFixed = weightFactory.getObject();

        for (NodeGene inputNode : genome.getNodes(NodeGeneType.INPUT)) {
            for (NodeGene outputNode : genome.getNodes(NodeGeneType.OUTPUT)) {
                InnovationId innovationId = historicalMarkings.getOrCreateInnovationId(inputNode, outputNode);
                ConnectionGene connection = new ConnectionGene(innovationId, weightFactoryFixed.create());

                genome.addConnection(connection);
            }
        }

        if (shouldConnectBiasNodes) {
            for (NodeGene biasNode : genome.getNodes(NodeGeneType.BIAS)) {
                for (NodeGene outputNode : genome.getNodes(NodeGeneType.OUTPUT)) {
                    InnovationId innovationId = historicalMarkings.getOrCreateInnovationId(biasNode, outputNode);
                    ConnectionGene connection = new ConnectionGene(innovationId, 1f);

                    genome.addConnection(connection);
                }
            }
        }
    }
}
