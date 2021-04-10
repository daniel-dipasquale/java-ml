package com.dipasquale.ai.rl.neat.genotype;

import com.dipasquale.ai.common.SequentialId;
import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.ai.rl.neat.context.HistoricalMarkings;
import com.dipasquale.ai.rl.neat.phenotype.NeuralNetwork;
import com.dipasquale.common.Pair;
import lombok.AccessLevel;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Optional;

public final class GenomeDefault implements Genome, Serializable {
    @Serial
    private static final long serialVersionUID = 1467592503532949541L;
    @Getter
    private final String id;
    @Getter(AccessLevel.PACKAGE)
    private final NodeGeneMap nodes;
    @Getter(AccessLevel.PACKAGE)
    private final ConnectionGeneMap connections;
    private final HistoricalMarkings historicalMarkings;
    private NeuralNetwork neuralNetwork;
    private boolean frozen;

    public GenomeDefault(final String id, final HistoricalMarkings historicalMarkings) {
        this.id = id;
        this.nodes = new NodeGeneMap();
        this.connections = new ConnectionGeneMap();
        this.historicalMarkings = historicalMarkings;
        this.neuralNetwork = null;
        this.frozen = false;
    }

    public void initialize(final Context.NeuralNetworkSupport neuralNetwork) {
        this.neuralNetwork = neuralNetwork.create(this, nodes, connections);
    }

    @Override
    public int getComplexity() {
        return connections.sizeFromExpressed() + 1;
    }

    public Iterable<NodeGene> getNodes(final NodeGeneType type) {
        return () -> nodes.iterator(type);
    }

    private void ensureIsNotFrozen() {
        if (frozen) {
            throw new IllegalStateException("the genome is frozen");
        }
    }

    public void addNode(final NodeGene node) {
        ensureIsNotFrozen();
        nodes.put(node);
    }

    public void addConnection(final ConnectionGene connection) {
        ensureIsNotFrozen();
        connections.put(connection);
    }

    private boolean mutateConnectionWeights(final Context context) {
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

    private boolean disableRandomConnection(final Context.RandomSupport random) {
        int size = connections.sizeFromExpressed();

        if (size == 0) {
            return false;
        }

        connections.disableByIndex(random.nextIndex(size));

        return true;
    }

    private SequentialId createHiddenNodeId() {
        return historicalMarkings.createNodeId(NodeGeneType.HIDDEN);
    }

    private InnovationId getOrCreateInnovationId(final NodeGene inNode, final NodeGene outNode) {
        return historicalMarkings.getOrCreateInnovationId(inNode, outNode);
    }

    private boolean addRandomNodeMutation(final Context context) {
        int size = connections.sizeFromExpressed();

        if (size == 0) {
            return false;
        }

        int index = context.random().nextIndex(size);
        ConnectionGene connection = connections.disableByIndex(index);
        NodeGene inNode = nodes.getById(connection.getInnovationId().getSourceNodeId());
        NodeGene outNode = nodes.getById(connection.getInnovationId().getTargetNodeId());
        NodeGene newNode = context.nodes().create(createHiddenNodeId(), NodeGeneType.HIDDEN);
        ConnectionGene inToNewConnection = new ConnectionGene(getOrCreateInnovationId(inNode, newNode), 1f);
        ConnectionGene newToOutConnection = new ConnectionGene(getOrCreateInnovationId(newNode, outNode), connection.getWeight());

        addNode(newNode);
        addConnection(inToNewConnection);
        addConnection(newToOutConnection);

        return true;
    }

    private boolean addRandomConnectionMutation(final Context context) {
        InnovationId innovationId = createRandomInnovationId(context.random());

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

    private NodeGene getRandomNode(final Context.RandomSupport random, final NodeGeneType type1, final NodeGeneType type2) {
        float size1 = (float) nodes.size(type1);
        float size2 = (float) nodes.size(type2);
        float size = size1 + size2;

        if (random.isLessThan(size1 / size)) {
            return Optional.ofNullable(nodes.getRandom(random, type1))
                    .orElseGet(() -> nodes.getRandom(random, type2));
        }

        return Optional.ofNullable(nodes.getRandom(random, type2))
                .orElseGet(() -> nodes.getRandom(random, type1));
    }

    private NodeGene getRandomNode(final Context.RandomSupport random, final NodeGeneType type1, final NodeGeneType type2, final NodeGeneType type3) {
        float size1 = (float) nodes.size(type1);
        float size2 = (float) nodes.size(type2);
        float size3 = (float) nodes.size(type3);
        float size = size1 + size2 + size3;

        if (random.isLessThan(size1 / size)) {
            return Optional.ofNullable(nodes.getRandom(random, type1))
                    .orElseGet(() -> getRandomNode(random, type2, type3));
        }

        if (random.isLessThan(size2 / (size2 + size3))) {
            return Optional.ofNullable(nodes.getRandom(random, type2))
                    .orElseGet(() -> getRandomNode(random, type1, type3));
        }

        return Optional.ofNullable(nodes.getRandom(random, type3))
                .orElseGet(() -> getRandomNode(random, type1, type2));
    }

    private NodeGene getRandomNodeToMatch(final Context.RandomSupport random, final NodeGeneType type) {
        return switch (type) {
            case INPUT, BIAS -> getRandomNode(random, NodeGeneType.OUTPUT, NodeGeneType.HIDDEN);

            case HIDDEN -> nodes.getRandom(random);

            case OUTPUT -> getRandomNode(random, NodeGeneType.INPUT, NodeGeneType.BIAS, NodeGeneType.HIDDEN);
        };
    }

    private InnovationId createRandomInnovationId(final Context.RandomSupport random) {
        if (nodes.size() <= 1) {
            return null;
        }

        NodeGene node1 = nodes.getByIndex(random.nextIndex(nodes.size()));
        NodeGene node2 = getRandomNodeToMatch(random, node1.getType());

        return switch (node1.getType()) {
            case INPUT, BIAS -> getOrCreateInnovationId(node1, node2);

            case OUTPUT -> getOrCreateInnovationId(node2, node1);

            case HIDDEN -> switch (node2.getType()) {
                case INPUT, BIAS -> getOrCreateInnovationId(node2, node1);

                case OUTPUT, HIDDEN -> getOrCreateInnovationId(node1, node2);
            };
        };
    }

    public void mutate(final Context context) {
        ensureIsNotFrozen();

        boolean mutated = mutateConnectionWeights(context);

        if (context.mutation().shouldDisableConnectionExpressed()) {
            mutated |= disableRandomConnection(context.random());
        }

        if (context.mutation().shouldAddNodeMutation()) {
            mutated |= addRandomNodeMutation(context);
        }

        if (connections.sizeFromExpressed() == 0 || context.mutation().shouldAddConnectionMutation()) {
            mutated |= addRandomConnectionMutation(context); // TODO: determine if this algorithm is consistent enough when converging
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

    private String createGenomeId() {
        return historicalMarkings.createGenomeId();
    }

    private static <T> T getRandom(final Context.RandomSupport random, final T item1, final T item2) {
        return random.isLessThan(0.5f) ? item1 : item2;
    }

    private static ConnectionGene createChildConnection(final Context context, final ConnectionGene parent1Connection, final ConnectionGene parent2Connection) {
        ConnectionGene randomParentConnection = getRandom(context.random(), parent1Connection, parent2Connection);
        boolean expressed = parent1Connection.isExpressed() && parent2Connection.isExpressed() || context.crossOver().shouldOverrideConnectionExpressed();

        if (context.crossOver().shouldUseRandomParentConnectionWeight()) {
            return randomParentConnection.createCopy(expressed);
        }

        InnovationId innovationId = randomParentConnection.getInnovationId();
        float weight = (parent1Connection.getWeight() + parent2Connection.getWeight()) / 2f;
        int cyclesAllowed = randomParentConnection.getRecurrentCyclesAllowed();

        return new ConnectionGene(innovationId, weight, cyclesAllowed, expressed);
    }

    public static GenomeDefault crossOverBySkippingUnfitDisjointOrExcess(final Context context, final GenomeDefault fitParent, final GenomeDefault unfitParent) {
        GenomeDefault child = new GenomeDefault(fitParent.createGenomeId(), fitParent.historicalMarkings);

        for (Pair<NodeGene> nodes : fitParent.nodes.fullJoin(unfitParent.nodes)) {
            if (nodes.getItem1() != null && nodes.getItem2() != null) {
                child.nodes.put(getRandom(context.random(), nodes.getItem1(), nodes.getItem2()));
            } else if (nodes.getItem1() != null) {
                child.nodes.put(nodes.getItem1());
            }
        }

        for (ConnectionGene fitConnection : fitParent.connections) {
            ConnectionGene unfitConnection = unfitParent.connections.getByIdFromAll(fitConnection.getInnovationId());
            ConnectionGene childConnection;

            if (unfitConnection != null) {
                childConnection = createChildConnection(context, fitConnection, unfitConnection);
            } else {
                childConnection = fitConnection.createCopy(fitConnection.isExpressed() || context.crossOver().shouldOverrideConnectionExpressed());
            }

            child.connections.put(childConnection);
        }

        return child;
    }

    public static GenomeDefault crossOverByEqualTreatment(final Context context, final GenomeDefault parent1, final GenomeDefault parent2) {
        GenomeDefault child = new GenomeDefault(parent1.createGenomeId(), parent1.historicalMarkings);

        for (Pair<NodeGene> nodes : parent1.nodes.fullJoin(parent2.nodes)) {
            if (nodes.getItem1() != null && nodes.getItem2() != null) {
                child.nodes.put(getRandom(context.random(), nodes.getItem1(), nodes.getItem2()));
            } else if (nodes.getItem1() != null) {
                child.nodes.put(nodes.getItem1());
            } else {
                child.nodes.put(nodes.getItem2());
            }
        }

        for (Pair<ConnectionGene> connections : parent1.connections.fullJoinFromAll(parent2.connections)) {
            if (connections.getItem1() != null && connections.getItem2() != null) {
                ConnectionGene childConnection = createChildConnection(context, connections.getItem1(), connections.getItem2());

                child.connections.put(childConnection);
            } else if (connections.getItem1() != null) {
                ConnectionGene childConnection = connections.getItem1().createCopy(connections.getItem1().isExpressed() || context.crossOver().shouldOverrideConnectionExpressed());

                child.connections.put(childConnection);
            } else {
                ConnectionGene childConnection = connections.getItem2().createCopy(connections.getItem2().isExpressed() || context.crossOver().shouldOverrideConnectionExpressed());

                child.connections.put(childConnection);
            }
        }

        return child;
    }

    private GenomeDefault createCopy(final String id, final HistoricalMarkings historicalMarkings) {
        GenomeDefault genome = new GenomeDefault(id, historicalMarkings);

        nodes.forEach(genome.nodes::put);
        connections.forEach(c -> genome.connections.put(c.createClone()));

        return genome;
    }

    public GenomeDefault createCopy(final HistoricalMarkings historicalMarkings) {
        String id = historicalMarkings.createGenomeId();

        return createCopy(id, historicalMarkings);
    }

    public GenomeDefault createClone(final HistoricalMarkings historicalMarkings) {
        return createCopy(id, historicalMarkings);
    }
}