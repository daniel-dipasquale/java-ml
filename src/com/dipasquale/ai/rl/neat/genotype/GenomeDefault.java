package com.dipasquale.ai.rl.neat.genotype;

import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.ai.rl.neat.phenotype.NeuralNetwork;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.Optional;

public final class GenomeDefault implements Genome {
    private final Context context;
    @Getter
    private final String id;
    @Getter(AccessLevel.PACKAGE)
    private final NodeGeneMap nodes;
    @Getter(AccessLevel.PACKAGE)
    private final ConnectionGeneMap connections;
    private final NeuralNetwork neuralNetwork;
    private boolean frozen;

    private GenomeDefault(final Context context, final String id) {
        NodeGeneMap nodes = new NodeGeneMap(context);
        ConnectionGeneMap connections = new ConnectionGeneMap();

        this.context = context;
        this.id = id;
        this.nodes = nodes;
        this.connections = connections;
        this.neuralNetwork = context.neuralNetwork().create(this, nodes, connections);
        this.frozen = false;
    }

    public GenomeDefault(final Context context) {
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

    public Iterable<NodeGene> getNodes(final NodeGeneType type) {
        return () -> nodes.iterator(type);
    }

    private void ensureNotFrozen() {
        if (frozen) {
            throw new IllegalStateException("the genome is frozen");
        }
    }

    public void addNode(final NodeGene node) {
        ensureNotFrozen();
        nodes.put(node);
    }

    public void addConnection(final ConnectionGene connection) {
        ensureNotFrozen();
        connections.put(connection);
    }

    private boolean mutateConnectionWeights() {
        boolean mutated = false;

        for (ConnectionGene connection : connections) {
            if (context.mutation().shouldPerturbConnectionWeight()) {
                connection.setWeight(context.connections().perturbWeight(connection.getWeight()));
                mutated = true;
            } else if (context.mutation().shouldReplaceConnectionWeight()) {
                connection.setWeight(context.connections().nextWeight());
                mutated = true;
            }
        }

        return mutated;
    }

    private boolean disableRandomConnection() {
        int size = connections.sizeFromExpressed();

        if (size == 0) {
            return false;
        }

        connections.disableByIndex(context.random().nextIndex(size));

        return true;
    }

    private boolean addRandomNodeMutation() {
        int size = connections.sizeFromExpressed();

        if (size == 0) {
            return false;
        }

        int index = context.random().nextIndex(size);
        ConnectionGene connection = connections.disableByIndex(index);
        NodeGene inNode = nodes.getById(connection.getInnovationId().getSourceNodeId());
        NodeGene outNode = nodes.getById(connection.getInnovationId().getTargetNodeId());
        NodeGene newNode = context.nodes().create(NodeGeneType.HIDDEN);
        ConnectionGene inToNewConnection = new ConnectionGene(context.connections().getOrCreateInnovationId(inNode, newNode), 1f);
        ConnectionGene newToOutConnection = new ConnectionGene(context.connections().getOrCreateInnovationId(newNode, outNode), connection.getWeight());

        addNode(newNode);
        addConnection(inToNewConnection);
        addConnection(newToOutConnection);

        return true;
    }

    private boolean addRandomConnectionMutation() {
        InnovationId innovationId = createRandomInnovationId();

        if (innovationId != null) {
            ConnectionGene connection = connections.getByIdFromAll(innovationId);

            if (connection == null) {
                addConnection(new ConnectionGene(innovationId, context.connections().nextWeight()));

                return true;
            }

            if (!connection.isExpressed()) {
                connection.toggleExpressed();

                return true;
            }

            if (context.connections().multipleRecurrentCyclesAllowed()) {
                connection.increaseCyclesAllowed();

                return true;
            }
        }

        return false;
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

        if (context.random().isLessThan(size2 / (size2 + size3))) {
            return Optional.ofNullable(nodes.getRandom(type2))
                    .orElseGet(() -> getRandomNode(type1, type3));
        }

        return Optional.ofNullable(nodes.getRandom(type3))
                .orElseGet(() -> getRandomNode(type1, type2));
    }

    private NodeGene getRandomNodeToMatch(final NodeGeneType type) {
        return switch (type) {
            case INPUT, BIAS -> getRandomNode(NodeGeneType.OUTPUT, NodeGeneType.HIDDEN);

            case HIDDEN -> nodes.getRandom();

            case OUTPUT -> getRandomNode(NodeGeneType.INPUT, NodeGeneType.BIAS, NodeGeneType.HIDDEN);
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
            case INPUT, BIAS -> createInnovationId(node1, node2);

            case OUTPUT -> createInnovationId(node2, node1);

            case HIDDEN -> switch (node2.getType()) {
                case INPUT, BIAS -> createInnovationId(node2, node1);

                case OUTPUT, HIDDEN -> createInnovationId(node1, node2);
            };
        };
    }

    public void mutate() {
        ensureNotFrozen();

        boolean mutated = mutateConnectionWeights();

        if (context.mutation().shouldDisableConnectionExpressed()) {
            mutated |= disableRandomConnection();
        }

        if (context.mutation().shouldAddNodeMutation()) {
            mutated |= addRandomNodeMutation();
        }

        if (connections.sizeFromExpressed() == 0 || context.mutation().shouldAddConnectionMutation()) {
            mutated |= addRandomConnectionMutation(); // TODO: find a better way to determine random connections
        }

        if (mutated) {
            neuralNetwork.reset();
        }
    }

    public void freeze() {
        frozen = true;
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