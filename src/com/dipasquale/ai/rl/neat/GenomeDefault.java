package com.dipasquale.ai.rl.neat;

import lombok.AccessLevel;
import lombok.Getter;

import java.util.Optional;

final class GenomeDefault implements Genome {
    private final Context context;
    @Getter
    private final String id;
    @Getter(AccessLevel.PACKAGE)
    private final NodeGeneMap nodes;
    @Getter(AccessLevel.PACKAGE)
    private final ConnectionGeneMap connections;
    private final NeuralNetwork neuralNetwork;

    private GenomeDefault(final Context context, final String id) {
        this.context = context;
        this.id = id;
        this.nodes = new NodeGeneMap(context);
        this.connections = new ConnectionGeneMap();
        this.neuralNetwork = context.neuralNetwork().create(this);
    }

    GenomeDefault(final Context context) {
        this(context, context.general().createGenomeId());
    }

    private GenomeDefault(final GenomeDefault genome, final String id) {
        this(genome.context, id);

        genome.nodes.forEach(this::addNode);

        for (ConnectionGene connection : genome.connections) {
            addConnection(connection.createCopy(connection.isExpressed()));
        }
    }

    @Override
    public int getComplexity() {
        return connections.sizeFromExpressed() + 1;
    }

    void addNode(final NodeGene node) {
        nodes.put(node);
    }

    void addConnection(final ConnectionGene connection) {
        connections.put(connection);
    }

    private void mutateSomeConnectionWeights() {
        for (ConnectionGene connection : connections) {
            if (context.random().isLessThan(context.mutation().perturbConnectionWeightRate())) {
                connection.setWeight(connection.getWeight() * context.random().next());
            } else {
                connection.setWeight(context.connections().nextWeight());
            }
        }
    }

    private void mutateSomeConnectionExpressed() {
        for (ConnectionGene connection : connections) {
            if (context.random().isLessThan(context.mutation().changeConnectionExpressedRate())) {
                connections.toggleExpressed(connection);
            }
        }
    }

    private boolean addNodeMutation() {
        if (connections.isEmptyFromExpressed()) {
            return false;
        }

        int connectionIndex = context.random().nextIndex(connections.sizeFromExpressed());
        ConnectionGene connection = connections.disableByIndex(connectionIndex);
        NodeGene inNode = nodes.getById(connection.getInnovationId().getSourceNodeId());
        NodeGene outNode = nodes.getById(connection.getInnovationId().getTargetNodeId());
        NodeGene newNode = context.nodes().create(NodeGeneType.Hidden);
        ConnectionGene inToNewConnection = new ConnectionGene(context.connections().getOrCreateInnovationId(inNode, newNode), 1f);
        ConnectionGene newToOutConnection = new ConnectionGene(context.connections().getOrCreateInnovationId(newNode, outNode), connection.getWeight());

        addNode(newNode);
        addConnection(inToNewConnection);
        addConnection(newToOutConnection);

        return true;
    }

    private NodeGene getRandomNode(final NodeGeneType type1, final NodeGeneType type2) {
        float size1 = (float) nodes.size(type1);
        float size2 = (float) nodes.size(type2);
        float size = size1 + size2;

        if (context.random().isLessThan(size1 / size)) {
            return Optional.ofNullable(nodes.getRandom(type1))
                    .orElseGet(() -> nodes.getRandom(type2));
        }

        return Optional.ofNullable(nodes.getRandom(type2))
                .orElseGet(() -> nodes.getRandom(type1));
    }

    private NodeGene getRandomNode(final NodeGeneType type1, final NodeGeneType type2, final NodeGeneType type3) {
        float size1 = (float) nodes.size(type1);
        float size2 = (float) nodes.size(type2);
        float size3 = (float) nodes.size(type3);
        float size = size1 + size2 + size3;

        if (context.random().isLessThan(size1 / size)) {
            return Optional.ofNullable(nodes.getRandom(type1))
                    .orElseGet(() -> getRandomNode(type2, type3));
        }

        if (context.random().isLessThan(size2 / size)) {
            return Optional.ofNullable(nodes.getRandom(type2))
                    .orElseGet(() -> getRandomNode(type1, type3));
        }

        return Optional.ofNullable(nodes.getRandom(type3))
                .orElseGet(() -> getRandomNode(type1, type2));
    }

    private NodeGene getRandomNodeToMatch(final NodeGeneType type) {
        return switch (type) {
            case Input, Bias -> getRandomNode(NodeGeneType.Output, NodeGeneType.Hidden);

            case Hidden -> nodes.getRandom();

            default -> getRandomNode(NodeGeneType.Input, NodeGeneType.Bias, NodeGeneType.Hidden);
        };
    }

    private InnovationId createInnovationId(final NodeGene node1, final NodeGene node2) {
        DirectedEdge directedEdge = new DirectedEdge(node1, node2);

        return context.connections().getOrCreateInnovationId(directedEdge);
    }

    private InnovationId createRandomInnovationId() {
        if (nodes.size() <= 1) {
            return null;
        }

        NodeGene node1 = nodes.getByIndex(context.random().nextIndex(nodes.size()));
        NodeGene node2 = getRandomNodeToMatch(node1.getType());

        return switch (node1.getType()) {
            case Input, Bias -> createInnovationId(node1, node2);

            case Output -> createInnovationId(node2, node1);

            default -> switch (node2.getType()) {
                case Input, Bias -> createInnovationId(node2, node1);

                default -> createInnovationId(node1, node2);
            };
        };
    }

    private boolean addConnectionMutation() {
        InnovationId innovationId = createRandomInnovationId();

        if (innovationId != null) {
            ConnectionGene connection = connections.getByIdFromAll(innovationId);

            if (connection == null) {
                addConnection(new ConnectionGene(innovationId, context.connections().nextWeight()));

                return true;
            }

            if (context.connections().multipleRecurrentCyclesAllowed()) {
                connection.increaseCyclesAllowed();

                return true;
            }
        }

        return false;
    }

    public void mutate() {
        mutateSomeConnectionWeights();
        mutateSomeConnectionExpressed();

        if (context.random().isLessThan(context.mutation().addNodeMutationsRate())) {
            addNodeMutation();
        }

        if (connections.sizeFromExpressed() == 0 || context.random().isLessThan(context.mutation().addConnectionMutationsRate())) {
            addConnectionMutation();
        }

        neuralNetwork.reset();
    }

    @Override
    public float[] activate(final float[] input) {
        return neuralNetwork.activate(input);
    }

    public GenomeDefault createCopy() {
        return new GenomeDefault(this, context.general().createGenomeId());
    }

    public GenomeDefault createClone() {
        return new GenomeDefault(this, id);
    }
}