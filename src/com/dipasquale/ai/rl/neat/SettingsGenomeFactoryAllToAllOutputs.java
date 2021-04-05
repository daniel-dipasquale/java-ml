package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.ai.rl.neat.genotype.ConnectionGene;
import com.dipasquale.ai.rl.neat.genotype.GenomeDefault;
import com.dipasquale.ai.rl.neat.genotype.GenomeDefaultFactory;
import com.dipasquale.ai.rl.neat.genotype.InnovationId;
import com.dipasquale.ai.rl.neat.genotype.NodeGene;
import com.dipasquale.ai.rl.neat.genotype.NodeGeneType;
import com.dipasquale.common.FloatFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class SettingsGenomeFactoryAllToAllOutputs implements GenomeDefaultFactory {
    @Serial
    private static final long serialVersionUID = -6010453981237789595L;
    private final SettingsGenomeFactoryNoConnections genomeFactoryNoConnections;
    private final FloatFactory weightFactory;
    private final boolean shouldConnectBiasNodes;

    @Override
    public GenomeDefault create(final Context context) {
        GenomeDefault genome = genomeFactoryNoConnections.create(context);

        for (NodeGene inputNode : genome.getNodes(NodeGeneType.INPUT)) {
            for (NodeGene outputNode : genome.getNodes(NodeGeneType.OUTPUT)) {
                InnovationId innovationId = context.connections().getOrCreateInnovationId(inputNode, outputNode);
                ConnectionGene connection = new ConnectionGene(innovationId, weightFactory.create());

                genome.addConnection(connection);
            }
        }

        if (shouldConnectBiasNodes) {
            for (NodeGene biasNode : genome.getNodes(NodeGeneType.BIAS)) {
                for (NodeGene outputNode : genome.getNodes(NodeGeneType.OUTPUT)) {
                    InnovationId innovationId = context.connections().getOrCreateInnovationId(biasNode, outputNode);
                    ConnectionGene connection = new ConnectionGene(innovationId, 1f);

                    genome.addConnection(connection);
                }
            }
        }

        return genome;
    }
}
