package com.dipasquale.ai.rl.neat.genotype;

import com.dipasquale.ai.common.sequence.SequentialId;
import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.ai.rl.neat.phenotype.NeuralNetwork;
import com.dipasquale.common.Pair;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Optional;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public final class DefaultGenome implements Genome, Serializable {
    @Serial
    private static final long serialVersionUID = 1467592503532949541L;
    @Getter
    @EqualsAndHashCode.Include
    private final String id;
    @Getter(AccessLevel.PACKAGE)
    @EqualsAndHashCode.Include
    private final NodeGeneGroup nodes;
    @Getter(AccessLevel.PACKAGE)
    @EqualsAndHashCode.Include
    private final ConnectionGeneGroup connections;
    private final GenomeHistoricalMarkings historicalMarkings;
    private transient NeuralNetwork neuralNetwork;
    private boolean frozen;

    public DefaultGenome(final String id, final GenomeHistoricalMarkings historicalMarkings) {
        this.id = id;
        this.nodes = new NodeGeneGroup();
        this.connections = new ConnectionGeneGroup();
        this.historicalMarkings = historicalMarkings;
        this.neuralNetwork = null;
        this.frozen = false;
    }

    public void initialize(final Context.NeuralNetworkSupport neuralNetwork) {
        this.neuralNetwork = neuralNetwork.create(nodes, connections);
    }

    @Override
    public int getComplexity() {
        return connections.getExpressed().size() + 1;
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

    private boolean mutateWeights(final Context context) {
        boolean mutated = false;

        for (ConnectionGene connection : connections.getAll()) {
            switch (context.mutation().nextWeightMutationType()) {
                case PERTURB -> {
                    connection.setWeight(context.connections().perturbWeight(connection.getWeight()));
                    mutated = true;
                }

                case REPLACE -> {
                    connection.setWeight(context.connections().nextWeight());
                    mutated = true;
                }
            }
        }

        return mutated;
    }

    private boolean disableRandomConnection(final Context.RandomSupport random) {
        int size = connections.getExpressed().size();

        if (size == 0) {
            return false;
        }

        connections.getExpressed().disableByIndex(random.nextIndex(size));

        return true;
    }

    private SequentialId createHiddenNodeId() {
        return historicalMarkings.createNodeId(NodeGeneType.HIDDEN);
    }

    private InnovationId getOrCreateInnovationId(final NodeGene inNode, final NodeGene outNode) {
        return historicalMarkings.getOrCreateInnovationId(inNode, outNode);
    }

    private boolean addRandomNodeMutation(final Context context) {
        int size = connections.getExpressed().size();

        if (size == 0) {
            return false;
        }

        int index = context.random().nextIndex(size);
        ConnectionGene connection = connections.getExpressed().disableByIndex(index);
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
            ConnectionGene connection = connections.getAll().getById(innovationId);

            if (connection == null) {
                addConnection(new ConnectionGene(innovationId, context.connections().nextWeight()));

                return true;
            }

            if (!connection.isExpressed()) {
                connection.toggleExpressed();

                return true;
            }

            if (context.connections().params().multipleRecurrentCyclesAllowed()) {
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

        boolean mutated = mutateWeights(context);

        if (context.mutation().shouldDisableExpressed()) {
            mutated |= disableRandomConnection(context.random());
        }

        if (context.mutation().shouldAddNodeMutation()) {
            mutated |= addRandomNodeMutation(context);
        }

        if (connections.getExpressed().isEmpty() || context.mutation().shouldAddConnectionMutation()) {
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
        boolean expressed = parent1Connection.isExpressed() && parent2Connection.isExpressed() || context.crossOver().shouldOverrideExpressed();

        if (context.crossOver().shouldUseRandomParentWeight()) {
            return randomParentConnection.createCopy(expressed);
        }

        InnovationId innovationId = randomParentConnection.getInnovationId();
        float weight = (parent1Connection.getWeight() + parent2Connection.getWeight()) / 2f;
        int recurrentCyclesAllowed = randomParentConnection.getRecurrentCyclesAllowed();

        return new ConnectionGene(innovationId, weight, recurrentCyclesAllowed, expressed);
    }

    public static DefaultGenome crossOverBySkippingUnfitDisjointOrExcess(final Context context, final DefaultGenome fitParent, final DefaultGenome unfitParent) {
        DefaultGenome child = new DefaultGenome(fitParent.createGenomeId(), fitParent.historicalMarkings);

        for (Pair<NodeGene> nodePair : (Iterable<Pair<NodeGene>>) () -> fitParent.nodes.fullJoin(unfitParent.nodes)) {
            if (nodePair.getLeft() != null && nodePair.getRight() != null) {
                child.nodes.put(getRandom(context.random(), nodePair.getLeft(), nodePair.getRight()));
            } else if (nodePair.getLeft() != null) {
                child.nodes.put(nodePair.getLeft());
            }
        }

        for (ConnectionGene fitConnection : fitParent.connections.getAll()) {
            ConnectionGene unfitConnection = unfitParent.connections.getAll().getById(fitConnection.getInnovationId());
            ConnectionGene childConnection;

            if (unfitConnection != null) {
                childConnection = createChildConnection(context, fitConnection, unfitConnection);
            } else {
                childConnection = fitConnection.createCopy(fitConnection.isExpressed() || context.crossOver().shouldOverrideExpressed());
            }

            child.connections.put(childConnection);
        }

        return child;
    }

    public static DefaultGenome crossOverByEqualTreatment(final Context context, final DefaultGenome parent1, final DefaultGenome parent2) {
        DefaultGenome child = new DefaultGenome(parent1.createGenomeId(), parent1.historicalMarkings);

        for (Pair<NodeGene> nodePair : (Iterable<Pair<NodeGene>>) () -> parent1.nodes.fullJoin(parent2.nodes)) {
            if (nodePair.getLeft() != null && nodePair.getRight() != null) {
                child.nodes.put(getRandom(context.random(), nodePair.getLeft(), nodePair.getRight()));
            } else if (nodePair.getLeft() != null) {
                child.nodes.put(nodePair.getLeft());
            } else {
                child.nodes.put(nodePair.getRight());
            }
        }

        for (Pair<ConnectionGene> connectionPair : (Iterable<Pair<ConnectionGene>>) () -> parent1.connections.getAll().fullJoin(parent2.connections)) {
            if (connectionPair.getLeft() != null && connectionPair.getRight() != null) {
                ConnectionGene childConnection = createChildConnection(context, connectionPair.getLeft(), connectionPair.getRight());

                child.connections.put(childConnection);
            } else if (connectionPair.getLeft() != null) {
                boolean expressed = connectionPair.getLeft().isExpressed() || context.crossOver().shouldOverrideExpressed();
                ConnectionGene childConnection = connectionPair.getLeft().createCopy(expressed);

                child.connections.put(childConnection);
            } else {
                boolean expressed = connectionPair.getRight().isExpressed() || context.crossOver().shouldOverrideExpressed();
                ConnectionGene childConnection = connectionPair.getRight().createCopy(expressed);

                child.connections.put(childConnection);
            }
        }

        return child;
    }

    private DefaultGenome createCopy(final String id, final GenomeHistoricalMarkings historicalMarkings) {
        DefaultGenome genome = new DefaultGenome(id, historicalMarkings);

        nodes.forEach(genome.nodes::put);
        connections.getAll().forEach(c -> genome.connections.put(c.createClone()));

        return genome;
    }

    public DefaultGenome createCopy(final GenomeHistoricalMarkings historicalMarkings) {
        String id = historicalMarkings.createGenomeId();

        return createCopy(id, historicalMarkings);
    }

    public DefaultGenome createClone(final GenomeHistoricalMarkings historicalMarkings) {
        return createCopy(id, historicalMarkings);
    }

    private static boolean isMatching(final Pair<ConnectionGene> connections) {
        return connections.getLeft() != null && connections.getRight() != null;
    }

    private static boolean isExcess(final ConnectionGene connection, final ConnectionGene excessConnection) {
        return connection.getInnovationId().compareTo(excessConnection.getInnovationId()) > 0;
    }

    private static boolean isDisjoint(final Pair<ConnectionGene> connections, final ConnectionGene excessConnection) {
        return excessConnection == null || connections.getLeft() != null && !isExcess(connections.getLeft(), excessConnection) || connections.getRight() != null && !isExcess(connections.getRight(), excessConnection);
    }

    private static ConnectionGene getExcessConnection(final DefaultGenome genome1, final DefaultGenome genome2) {
        ConnectionGene lastConnection1 = genome1.connections.getAll().getLast();
        ConnectionGene lastConnection2 = genome2.connections.getAll().getLast();

        if (lastConnection1 == null || lastConnection2 == null) {
            return null;
        }

        int comparison = lastConnection1.getInnovationId().compareTo(lastConnection2.getInnovationId());

        if (comparison == 0) {
            return null;
        }

        if (comparison < 0) {
            return lastConnection1;
        }

        return lastConnection2;
    }

    @RequiredArgsConstructor
    public static final class CompatibilityCalculator implements GenomeCompatibilityCalculator, Serializable {
        @Serial
        private static final long serialVersionUID = 8186925797297865215L;
        private final float excessCoefficient; // c1;
        private final float disjointCoefficient; // c2;
        private final float weightDifferenceCoefficient; // c3

        @Override
        public double calculateCompatibility(final DefaultGenome genome1, final DefaultGenome genome2) {
            ConnectionGene excessFromConnection = getExcessConnection(genome1, genome2);
            int matchingCount = 0;
            double weightDifference = 0D;
            int disjointCount = 0;
            int excessCount = 0;

            for (Pair<ConnectionGene> connectionPair : (Iterable<Pair<ConnectionGene>>) () -> genome1.connections.getAll().fullJoin(genome2.connections)) {
                if (isMatching(connectionPair)) {
                    matchingCount++;
                    weightDifference += Math.abs(connectionPair.getLeft().getWeight() - connectionPair.getRight().getWeight());
                } else if (isDisjoint(connectionPair, excessFromConnection)) {
                    disjointCount++;
                } else {
                    excessCount++;
                }
            }

            int maximumNodes = Math.max(genome1.nodes.size(), genome2.nodes.size());
            double n = maximumNodes < 20 ? 1D : (double) maximumNodes;
            double averageWeightDifference = matchingCount == 0 ? 0D : weightDifference / (double) matchingCount;

            return excessCoefficient * (double) excessCount / n + disjointCoefficient * (double) disjointCount / n + weightDifferenceCoefficient * averageWeightDifference;
        }
    }
}