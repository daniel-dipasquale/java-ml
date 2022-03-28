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

    private static Iterable<NodeGene> getNodes(final Genome genome, final NodeGeneType type) {
        return () -> genome.getNodes().iterator(type);
    }

    private static NodeLayer createNodeLayer(final int count, final Iterator<NodeGene> nodes) {
        List<NodeGene> copiedNodes = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            copiedNodes.add(nodes.next());
        }

        return new NodeLayer(copiedNodes);
    }

    private List<NodeLayer> createNodeLayers(final Genome genome) {
        List<NodeLayer> nodeLayers = new ArrayList<>();
        Iterable<NodeGene> inputNodes = getNodes(genome, NodeGeneType.INPUT);

        nodeLayers.add(new NodeLayer(inputNodes));

        if (!hiddenLayers.isEmpty()) {
            Iterator<NodeGene> hiddenNodes = getNodes(genome, NodeGeneType.HIDDEN).iterator();

            for (int nodeCount : hiddenLayers) {
                nodeLayers.add(createNodeLayer(nodeCount, hiddenNodes));
            }
        }

        Iterable<NodeGene> outputNodes = getNodes(genome, NodeGeneType.OUTPUT);

        nodeLayers.add(new NodeLayer(outputNodes));

        return nodeLayers;
    }

    private static ConnectionGene createConnection(final Context.ConnectionGeneSupport connectionGeneSupport, final NodeGene inputNode, final NodeGene outputNode, final float weight) {
        InnovationId innovationId = connectionGeneSupport.provideInnovationId(inputNode, outputNode);

        return new ConnectionGene(innovationId, weight, connectionGeneSupport.generateRecurrentWeights());
    }

    @Override
    public void setupConnections(final Genome genome, final Context.ConnectionGeneSupport connectionGeneSupport) {
        ConnectionGeneGroup connections = genome.getConnections();
        List<NodeLayer> nodeLayers = createNodeLayers(genome);

        for (int i = 1, c = nodeLayers.size(); i < c; i++) {
            NodeLayer inputLayer = nodeLayers.get(i - 1);
            NodeLayer outputLayer = nodeLayers.get(i);

            for (NodeGene inputNode : inputLayer.nodes) {
                for (NodeGene outputNode : outputLayer.nodes) {
                    ConnectionGene connection = createConnection(connectionGeneSupport, inputNode, outputNode, weightFactory.create());

                    connections.put(connection);
                }
            }
        }

        if (shouldConnectBiasNodes) {
            NodeLayer outputLayer = nodeLayers.get(1);

            for (NodeGene biasNode : getNodes(genome, NodeGeneType.BIAS)) {
                for (NodeGene outputNode : outputLayer.nodes) {
                    ConnectionGene connection = createConnection(connectionGeneSupport, biasNode, outputNode, BIAS_WEIGHT);

                    connections.put(connection);
                }
            }
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class NodeLayer {
        private final Iterable<NodeGene> nodes;
    }
}
