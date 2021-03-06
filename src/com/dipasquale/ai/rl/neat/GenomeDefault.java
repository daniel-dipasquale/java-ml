package com.dipasquale.ai.rl.neat;

import lombok.AccessLevel;
import lombok.Getter;

import java.util.Optional;

final class GenomeDefault<T extends Comparable<T>> implements Genome {
    private final Context<T> context;
    private final String id;
    @Getter(AccessLevel.PACKAGE)
    private final NodeGeneMap<T> nodes;
    @Getter(AccessLevel.PACKAGE)
    private final ConnectionGeneMap<T> connections;
    private final NeuralNetwork neuralNetwork;

    private GenomeDefault(final Context<T> context, final String id) {
        this.context = context;
        this.id = id;
        this.nodes = new NodeGeneMap<>(context);
        this.connections = new ConnectionGeneMap<>();
        this.neuralNetwork = context.neuralNetwork().create(this);
    }

    GenomeDefault(final Context<T> context) {
        this(context, context.general().createGenomeId());
    }

    private GenomeDefault(final GenomeDefault<T> genome, final String id) {
        this(genome.context, id);

        genome.nodes.forEach(this::addNode);

        for (ConnectionGene<T> connection : genome.connections) {
            addConnection(connection.createCopy(connection.isExpressed()));
        }
    }

    @Override
    public String getId() {
        return id.toString();
    }

    @Override
    public int getComplexity() {
        return connections.sizeFromExpressed() + 1;
    }

    void addNode(final NodeGene<T> node) {
        nodes.put(node);
    }

    void addConnection(final ConnectionGene<T> connection) {
        connections.put(connection);
    }

    private void mutateSomeConnectionWeights() {
        for (ConnectionGene<T> connection : connections) {
            if (context.random().isLessThan(context.mutation().perturbConnectionWeightRate())) {
                connection.setWeight(connection.getWeight() * context.random().next());
            } else {
                connection.setWeight(context.connections().nextWeight());
            }
        }
    }

    private void mutateSomeConnectionExpressed() {
        for (ConnectionGene<T> connection : connections) {
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
        ConnectionGene<T> connection = connections.disableByIndex(connectionIndex);
        NodeGene<T> inNode = nodes.getById(connection.getInnovationId().getSourceNodeId());
        NodeGene<T> outNode = nodes.getById(connection.getInnovationId().getTargetNodeId());
        NodeGene<T> newNode = context.nodes().create(NodeGeneType.Hidden);
        ConnectionGene<T> inToNewConnection = new ConnectionGene<>(context.connections().getOrCreateInnovationId(inNode, newNode), 1f);
        ConnectionGene<T> newToOutConnection = new ConnectionGene<>(context.connections().getOrCreateInnovationId(newNode, outNode), connection.getWeight());

        addNode(newNode);
        addConnection(inToNewConnection);
        addConnection(newToOutConnection);

        return true;
    }

    private NodeGene<T> getRandomNode(final NodeGeneType type1, final NodeGeneType type2) {
        if (context.random().isLessThan(0.5f)) {
            return Optional.ofNullable(nodes.getRandom(type1))
                    .orElseGet(() -> nodes.getRandom(type2));
        }

        return Optional.ofNullable(nodes.getRandom(type2))
                .orElseGet(() -> nodes.getRandom(type1));
    }

    private NodeGene<T> getRandomNode(final NodeGeneType type1, final NodeGeneType type2, final NodeGeneType type3) {
        if (context.random().isLessThan(1f / 3f)) {
            return Optional.ofNullable(nodes.getRandom(type1))
                    .orElseGet(() -> getRandomNode(type2, type3));
        }

        if (context.random().isLessThan(0.5f)) {
            return Optional.ofNullable(nodes.getRandom(type2))
                    .orElseGet(() -> getRandomNode(type1, type3));
        }

        return Optional.ofNullable(nodes.getRandom(type3))
                .orElseGet(() -> getRandomNode(type1, type2));
    }

    private NodeGene<T> getRandomNodeToMatch(final NodeGeneType type) {
        return switch (type) {
            case Input, Bias -> getRandomNode(NodeGeneType.Output, NodeGeneType.Hidden);

            case Hidden -> nodes.getRandom();

            default -> getRandomNode(NodeGeneType.Input, NodeGeneType.Bias, NodeGeneType.Hidden);
        };
    }

    private InnovationId<T> createInnovationId(final NodeGene<T> node1, final NodeGene<T> node2) {
        DirectedEdge<T> directedEdge = new DirectedEdge<>(node1, node2);

        return context.connections().getOrCreateInnovationId(directedEdge);
    }

    private InnovationId<T> createRandomInnovationId() {
        if (nodes.size() <= 1) {
            return null;
        }

        NodeGene<T> node1 = nodes.getByIndex(context.random().nextIndex(nodes.size()));
        NodeGene<T> node2 = getRandomNodeToMatch(node1.getType());

        if (node1 == node2) {
            return null;
        }

        return switch (node1.getType()) {
            case Input -> createInnovationId(node1, node2);

            case Output -> createInnovationId(node2, node1);

            default -> switch (node2.getType()) {
                case Input -> createInnovationId(node2, node1);

                default -> Optional.ofNullable(createInnovationId(node1, node2))
                        .orElseGet(() -> createInnovationId(node2, node1));
            };
        };
    }

    private boolean addConnectionMutation() {
        InnovationId<T> innovationId = createRandomInnovationId();

        if (innovationId != null) {
            ConnectionGene<T> connection = connections.getByIdFromAll(innovationId);

            if (connection == null) {
                addConnection(new ConnectionGene<>(innovationId, context.connections().nextWeight()));

                return true;
            }

            if (context.connections().allowRecurrentConnections()) {
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

    public GenomeDefault<T> createCopy() {
        return new GenomeDefault<>(this, context.general().createGenomeId());
    }

    public GenomeDefault<T> createClone() {
        return new GenomeDefault<>(this, id);
    }
}