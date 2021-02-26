package com.dipasquale.ai.rl.neat;

import lombok.AccessLevel;
import lombok.Getter;

import java.util.Optional;

final class GenomeDefault<T extends Comparable<T>> implements Genome {
    private final Context<T> context;
    @Getter
    private final String id;
    @Getter(AccessLevel.PACKAGE)
    private final NodeGeneMap<T> nodes;
    @Getter(AccessLevel.PACKAGE)
    private final ConnectionGeneMap<T> connections;
    private final NeuralNetwork neuralNetwork;

    GenomeDefault(final Context<T> context) {
        this.context = context;
        this.id = context.general().createGenomeId();
        this.nodes = new NodeGeneMap<>(context);
        this.connections = new ConnectionGeneMap<>();
        this.neuralNetwork = context.neuralNetwork().create(this);
    }

    private GenomeDefault(final GenomeDefault<T> genome) {
        this(genome.context);

        genome.nodes.forEach(this::addNode);

        for (ConnectionGene<T> connection : genome.connections) {
            addConnection(connection.createCopy(connection.isExpressed()));
        }
    }

    @Override
    public int getComplexity() {
        return this.connections.sizeFromExpressed() + 1;
    }

    private void addNode(final NodeGene<T> node) {
        nodes.put(node);
    }

    private void addConnection(final ConnectionGene<T> connection) {
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
        NodeGene<T> newNode = context.nodes().create(NodeGene.Type.Hidden);
        ConnectionGene<T> inToNewConnection = new ConnectionGene<>(context.connections().getOrCreateInnovationId(inNode, newNode), 1f);
        ConnectionGene<T> newToOutConnection = new ConnectionGene<>(context.connections().getOrCreateInnovationId(newNode, outNode), connection.getWeight());

        addNode(newNode);
        addConnection(inToNewConnection);
        addConnection(newToOutConnection);

        return true;
    }

    private NodeGene<T> getRandomNodeToMatch(final NodeGene.Type type) {
        switch (type) {
            case Input -> {
                if (context.random().isLessThan(0.5f)) {
                    return Optional.ofNullable(nodes.getRandom(NodeGene.Type.Hidden))
                            .orElseGet(() -> nodes.getRandom(NodeGene.Type.Output));
                }

                return Optional.ofNullable(nodes.getRandom(NodeGene.Type.Output))
                        .orElseGet(() -> nodes.getRandom(NodeGene.Type.Hidden));
            }

            case Hidden -> {
                return nodes.getRandom();
            }

            default -> {
                if (context.random().isLessThan(0.5f)) {
                    return Optional.ofNullable(nodes.getRandom(NodeGene.Type.Hidden))
                            .orElseGet(() -> nodes.getRandom(NodeGene.Type.Input));
                }

                return Optional.ofNullable(nodes.getRandom(NodeGene.Type.Input))
                        .orElseGet(() -> nodes.getRandom(NodeGene.Type.Hidden));
            }
        }
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

        if (context.random().isLessThan(context.mutation().addConnectionMutationsRate())) {
            addConnectionMutation();
        }

        neuralNetwork.reset();
    }

    private static <T extends Comparable<T>> GenomeDefault<T> crossoverBySkippingUnfit(final GenomeDefault<T> fitParent, final GenomeDefault<T> unfitParent) {
        GenomeDefault<T> child = new GenomeDefault<>(fitParent.context);

        fitParent.nodes.forEach(child::addNode);

        for (ConnectionGene<T> fitConnection : fitParent.connections) {
            ConnectionGene<T> unfitConnection = unfitParent.connections.getByIdFromAll(fitConnection.getInnovationId());

            if (unfitConnection != null) {
                ConnectionGene<T> parentConnection = child.context.random().isLessThan(0.5f) ? fitConnection : unfitConnection;
                ConnectionGene<T> childConnection = parentConnection.createCopy(true);
                boolean expressed = fitConnection.isExpressed() && unfitConnection.isExpressed();

                if (!expressed && child.context.random().isLessThan(child.context.crossover().disableExpressedInheritanceRate())) {
                    childConnection.disable();
                }

                child.addConnection(childConnection);
            } else {
                ConnectionGene<T> childConnection = fitConnection.createCopy(fitConnection.isExpressed());

                child.addConnection(childConnection);
            }
        }

        return child;
    }

    public static <T extends Comparable<T>> GenomeDefault<T> crossover(final GenomeDefault<T> parent1, final GenomeDefault<T> parent2) { // TODO: revise this
        return crossoverBySkippingUnfit(parent1, parent2);
    }

    @Override
    public float[] activate(final float[] input) {
        return neuralNetwork.activate(input);
    }

    public GenomeDefault<T> createCopy() {
        return new GenomeDefault<>(this);
    }
}