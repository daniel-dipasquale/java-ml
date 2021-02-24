package com.experimental.ai.rl.neat;

import lombok.AccessLevel;
import lombok.Getter;

import java.util.Optional;

public final class Genome<T extends Comparable<T>> {
    private final Context<T> context;
    @Getter(AccessLevel.PACKAGE)
    private final NodeGeneMap<T> nodes;
    @Getter(AccessLevel.PACKAGE)
    private final ConnectionGeneMap<T> connections;
    private final NeuralNetwork neuralNetwork;

    public Genome(final Context<T> context) {
        this.context = context;
        this.nodes = new NodeGeneMap<>(context);
        this.connections = new ConnectionGeneMap<>();
        this.neuralNetwork = context.neuralNetwork().create(this);
    }

    private Genome(final Genome<T> genome) {
        this(genome.context);

        genome.nodes.forEach(this::addNode);

        for (ConnectionGene<T> connection : genome.connections) {
            addConnection(connection.createCopy(connection.isExpressed()));
        }
    }

    private void addNode(final NodeGene<T> node) {
        nodes.put(node);
        neuralNetwork.reset();
    }

    private void addConnection(final ConnectionGene<T> connection) {
        if (connections.put(connection)) {
            neuralNetwork.reset();
        }
    }

    private boolean addNodeMutation() {
        if (connections.isEmptyFromExpressed()) {
            return false;
        }

        int connectionIndex = context.random().nextIndex(connections.sizeFromExpressed());
        ConnectionGene<T> connection = connections.disableByIndex(connectionIndex);

        neuralNetwork.reset();

        NodeGene<T> inNode = nodes.getById(connection.getInnovationId().getInNodeId());
        NodeGene<T> outNode = nodes.getById(connection.getInnovationId().getOutNodeId());
        NodeGene<T> newNode = context.nodes().create(NodeGene.Type.Hidden);
        ConnectionGene<T> inToNewConnection = new ConnectionGene<>(context.connections().createInnovationId(inNode, newNode), 1f);
        ConnectionGene<T> newToOutConnection = new ConnectionGene<>(context.connections().createInnovationId(newNode, outNode), connection.getWeight());

        addNode(newNode);
        addConnection(inToNewConnection);
        addConnection(newToOutConnection);

        return true;
    }

    private NodeGene<T> getRandomNodeToMatch(final NodeGene.Type type) {
        switch (type) {
            case Input -> {
                if (context.random().isAtMost(0.5f)) {
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
                if (context.random().isAtMost(0.5f)) {
                    return Optional.ofNullable(nodes.getRandom(NodeGene.Type.Hidden))
                            .orElseGet(() -> nodes.getRandom(NodeGene.Type.Input));
                }

                return Optional.ofNullable(nodes.getRandom(NodeGene.Type.Input))
                        .orElseGet(() -> nodes.getRandom(NodeGene.Type.Hidden));
            }
        }
    }

    private DirectedEdgeNodePair<T> createRandomDirectedEdgeNodePair() {
        if (nodes.size() <= 1) {
            return null;
        }

        NodeGene<T> node1 = nodes.getByIndex(context.random().nextIndex(nodes.size()));
        NodeGene<T> node2 = getRandomNodeToMatch(node1.getType());

        if (node1 == node2) {
            return null;
        }

        if (node1.getType() != NodeGene.Type.Output) {
            return new DirectedEdgeNodePair<>(node1, node2);
        }

        return new DirectedEdgeNodePair<>(node2, node1);
    }

    private boolean addConnectionMutation() {
        DirectedEdgeNodePair<T> directedEdgeNodePair = createRandomDirectedEdgeNodePair();

        if (directedEdgeNodePair == null) {
            return false;
        }

        DirectedEdge<T> directedEdge = directedEdgeNodePair.createDirectedEdge();
        InnovationId<T> innovationId = context.connections().createInnovationId(directedEdge);

        if (innovationId == null && !context.connections().allowReInnovations()) {
            return false;
        }

        if (innovationId == null) {
            innovationId = context.connections().getInnovationId(directedEdge);
        }

        ConnectionGene<T> connection = connections.getByIdFromAll(innovationId);

        if (connection == null) {
            addConnection(new ConnectionGene<>(innovationId, context.connections().nextWeight()));
        } else if (context.connections().allowCyclicConnections()) {
            connection.increaseCyclesAllowed();
        } else {
            return false;
        }

        return true;
    }

    private void mutateSomeConnectionWeights() {
        for (ConnectionGene<T> connection : connections) {
            if (context.random().isAtMost(context.mutation().perturbConnectionWeightRate())) {
                connection.setWeight(connection.getWeight() * context.random().next());
            } else {
                connection.setWeight(context.connections().nextWeight());
            }
        }
    }

    private void mutateSomeConnectionExpressed() {
        for (ConnectionGene<T> connection : connections) {
            if (context.random().isAtMost(context.mutation().changeConnectionExpressedRate())) {
                connections.toggleExpressed(connection);
            }
        }
    }

    public void mutate() {
        if (context.random().isAtMost(context.mutation().addNodeMutationsRate())) {
            addNodeMutation();
        }

        if (context.random().isAtMost(context.mutation().addConnectionMutationsRate())) {
            addConnectionMutation();
        }

        mutateSomeConnectionWeights();
        mutateSomeConnectionExpressed();
    }

    private static <T extends Comparable<T>> Genome<T> crossoverBySkippingUnfit(final Genome<T> fitParent, final Genome<T> unfitParent) {
        Genome<T> child = new Genome<>(fitParent.context);

        fitParent.nodes.forEach(child::addNode);

        for (ConnectionGene<T> fitConnection : fitParent.connections) {
            ConnectionGene<T> unfitConnection = unfitParent.connections.getByIdFromAll(fitConnection.getInnovationId());

            if (unfitConnection != null) {
                ConnectionGene<T> parentConnection = child.context.random().isAtMost(0.5f) ? fitConnection : unfitConnection;
                ConnectionGene<T> childConnection = parentConnection.createCopy(true);
                boolean expressed = fitConnection.isExpressed() && unfitConnection.isExpressed();

                if (!expressed && child.context.random().isAtMost(child.context.crossover().disableExpressedInheritanceRate())) {
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

    public static <T extends Comparable<T>> Genome<T> crossover(final Genome<T> parent1, final Genome<T> parent2) { // TODO: revise this
        return crossoverBySkippingUnfit(parent1, parent2);
    }

    public float[] activate(final float[] input) {
        return neuralNetwork.activate(input);
    }

    public Genome<T> createCopy() {
        return new Genome<>(this);
    }
}