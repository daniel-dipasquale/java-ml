package com.dipasquale.ai.rl.neat.genotype;

import com.dipasquale.ai.rl.neat.Context;
import com.dipasquale.common.factory.FloatFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@RequiredArgsConstructor
public final class FullyConnectedGenesisGenomeConnector implements GenesisGenomeConnector, Serializable {
    @Serial
    private static final long serialVersionUID = 4200039748076194340L;
    private static final float BIAS_WEIGHT = 1f;
    private final List<Integer> hiddenLayers;
    private final FloatFactory weightFactory;
    private final boolean shouldConnectBiasNodes;

    private static Iterable<NodeGene> getNodeGenes(final Genome genome, final NodeGeneType type) {
        return () -> genome.getNodeGenes().iterator(type);
    }

    private static NodeGeneLayer createNodeGeneLayer(final int count, final Iterator<NodeGene> nodeGenes) {
        List<NodeGene> copiedNodeGenes = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            copiedNodeGenes.add(nodeGenes.next());
        }

        return new NodeGeneLayer(copiedNodeGenes);
    }

    private List<NodeGeneLayer> createNodeLayers(final Genome genome) {
        List<NodeGeneLayer> nodeGeneLayers = new ArrayList<>();
        Iterable<NodeGene> inputNodeGenes = getNodeGenes(genome, NodeGeneType.INPUT);

        nodeGeneLayers.add(new NodeGeneLayer(inputNodeGenes));

        if (!hiddenLayers.isEmpty()) {
            Iterator<NodeGene> hiddenNodeGenes = getNodeGenes(genome, NodeGeneType.HIDDEN).iterator();

            for (int nodeGeneCount : hiddenLayers) {
                nodeGeneLayers.add(createNodeGeneLayer(nodeGeneCount, hiddenNodeGenes));
            }
        }

        Iterable<NodeGene> outputNodeGenes = getNodeGenes(genome, NodeGeneType.OUTPUT);

        nodeGeneLayers.add(new NodeGeneLayer(outputNodeGenes));

        return nodeGeneLayers;
    }

    private static ConnectionGene createConnectionGene(final Context.ConnectionGeneSupport connectionGeneSupport, final NodeGene inputNodeGene, final NodeGene outputNodeGene, final float weight) {
        InnovationId innovationId = connectionGeneSupport.provideInnovationId(inputNodeGene, outputNodeGene);

        return new ConnectionGene(innovationId, weight, connectionGeneSupport.generateRecurrentWeights());
    }

    @Override
    public void setupConnections(final Genome genome, final Context.ConnectionGeneSupport connectionGeneSupport) {
        ConnectionGeneGroup connectionGenes = genome.getConnectionGenes();
        List<NodeGeneLayer> nodeGeneLayers = createNodeLayers(genome);

        for (int i = 1, c = nodeGeneLayers.size(); i < c; i++) {
            NodeGeneLayer inputLayer = nodeGeneLayers.get(i - 1);
            NodeGeneLayer outputLayer = nodeGeneLayers.get(i);

            for (NodeGene inputNodeGene : inputLayer.nodeGenes) {
                for (NodeGene outputNodeGene : outputLayer.nodeGenes) {
                    ConnectionGene connectionGene = createConnectionGene(connectionGeneSupport, inputNodeGene, outputNodeGene, weightFactory.create());

                    connectionGenes.put(connectionGene);
                }
            }
        }

        if (shouldConnectBiasNodes) {
            NodeGeneLayer outputLayer = nodeGeneLayers.get(1);

            for (NodeGene biasNodeGene : getNodeGenes(genome, NodeGeneType.BIAS)) {
                for (NodeGene outputNodeGene : outputLayer.nodeGenes) {
                    ConnectionGene connectionGene = createConnectionGene(connectionGeneSupport, biasNodeGene, outputNodeGene, BIAS_WEIGHT);

                    connectionGenes.put(connectionGene);
                }
            }
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class NodeGeneLayer {
        private final Iterable<NodeGene> nodeGenes;
    }
}
