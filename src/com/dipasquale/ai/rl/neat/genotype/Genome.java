package com.dipasquale.ai.rl.neat.genotype;

import com.dipasquale.ai.common.output.OutputClassifier;
import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.common.Pair;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
public final class Genome implements Serializable {
    @Serial
    private static final long serialVersionUID = 1467592503532949541L;
    private final String id;
    private final NodeGeneGroup nodes = new NodeGeneGroup();
    private final ConnectionGeneGroup connections = new ConnectionGeneGroup();

    public int getComplexity() {
        return connections.getExpressed().size();
    }

    private boolean mutateWeights(final Context context) {
        boolean mutated = false;

        for (ConnectionGene connection : connections.getAll()) {
            switch (context.mutation().generateWeightMutationType()) {
                case PERTURB -> {
                    connection.setWeight(context.connections().perturbWeight(connection.getWeight()));
                    mutated = true;
                }

                case REPLACE -> {
                    connection.setWeight(context.connections().generateWeight());
                    mutated = true;
                }
            }
        }

        return mutated;
    }

    private boolean disableRandomConnection(final Context.RandomSupport randomSupport) {
        int size = connections.getExpressed().size();

        if (size == 0) {
            return false;
        }

        connections.getExpressed().disableByIndex(randomSupport.generateIndex(size));

        return true;
    }

    private boolean addRandomNodeMutation(final Context context) {
        int size = connections.getExpressed().size();

        if (size == 0) {
            return false;
        }

        int index = context.random().generateIndex(size);
        ConnectionGene connection = connections.getExpressed().disableByIndex(index);
        NodeGene inNode = nodes.getById(connection.getInnovationId().getSourceNodeId());
        NodeGene outNode = nodes.getById(connection.getInnovationId().getTargetNodeId());
        NodeGene newNode = context.nodes().createHidden();
        ConnectionGene inToNewConnection = new ConnectionGene(context.connections().getOrCreateInnovationId(inNode, newNode), 1f);
        ConnectionGene newToOutConnection = new ConnectionGene(context.connections().getOrCreateInnovationId(newNode, outNode), connection.getWeight());

        getNodes().put(newNode);
        getConnections().put(inToNewConnection);
        getConnections().put(newToOutConnection);

        return true;
    }

    private boolean addRandomConnectionMutation(final Context context) {
        InnovationId innovationId = createRandomInnovationId(context);

        if (innovationId != null) {
            ConnectionGene connection = connections.getAll().getById(innovationId);

            if (connection == null) {
                getConnections().put(new ConnectionGene(innovationId, context.connections().generateWeight()));

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

    private NodeGene getRandomNode(final Context.RandomSupport randomSupport, final NodeGeneType... types) {
        float totalSize = (float) Arrays.stream(types)
                .map(nodes::size)
                .reduce(0, Integer::sum);

        OutputClassifier<NodeGeneType> typeOutputClassifier = new OutputClassifier<>();

        for (int i = 0, c = types.length - 1; i < c; i++) {
            NodeGeneType type = types[i];

            typeOutputClassifier.addRangeFor(nodes.size(type) / totalSize, type);
        }

        typeOutputClassifier.addRemainingRangeFor(types[types.length - 1]);

        return nodes.getRandom(randomSupport, randomSupport.generateItem(typeOutputClassifier));
    }

    private NodeGene getRandomNodeToMatch(final Context.RandomSupport randomSupport, final NodeGeneType type) {
        return switch (type) {
            case INPUT, BIAS -> getRandomNode(randomSupport, NodeGeneType.OUTPUT, NodeGeneType.HIDDEN);

            case HIDDEN -> nodes.getRandom(randomSupport);

            case OUTPUT -> getRandomNode(randomSupport, NodeGeneType.INPUT, NodeGeneType.BIAS, NodeGeneType.HIDDEN);
        };
    }

    private InnovationId createRandomInnovationId(final Context context) {
        if (nodes.size() <= 1) {
            return null;
        }

        NodeGene node1 = nodes.getByIndex(context.random().generateIndex(nodes.size()));
        NodeGene node2 = getRandomNodeToMatch(context.random(), node1.getType());

        return switch (node1.getType()) {
            case INPUT, BIAS -> context.connections().getOrCreateInnovationId(node1, node2);

            case OUTPUT -> context.connections().getOrCreateInnovationId(node2, node1);

            case HIDDEN -> switch (node2.getType()) {
                case INPUT, BIAS -> context.connections().getOrCreateInnovationId(node2, node1);

                case OUTPUT, HIDDEN -> context.connections().getOrCreateInnovationId(node1, node2);
            };
        };
    }

    public boolean mutate(final Context context) {
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

        return mutated;
    }

    private static <T> T getRandom(final Context.RandomSupport randomSupport, final T item1, final T item2) {
        return randomSupport.isLessThan(0.5f) ? item1 : item2;
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

    private static Iterable<Pair<NodeGene>> fullJoinBetweenNodes(final Genome genome1, final Genome genome2) {
        return () -> genome1.nodes.fullJoin(genome2.nodes);
    }

    private static boolean isInnovationIdValid(final Context.ConnectionGeneSupport connectionGeneSupport, final ConnectionGene connection1, final ConnectionGene connection2) {
        if (connection1 != null) {
            return connectionGeneSupport.containsInnovationId(connection1.getInnovationId().getDirectedEdge());
        }

        return connectionGeneSupport.containsInnovationId(connection2.getInnovationId().getDirectedEdge());
    }

    private static boolean isInnovationIdValid(final Context.ConnectionGeneSupport connectionGeneSupport, final ConnectionGene connection) {
        return isInnovationIdValid(connectionGeneSupport, connection, null);
    }

    public static Genome crossOverBySkippingUnfitDisjointOrExcess(final Context context, final Genome fitParent, final Genome unfitParent) {
        Genome child = new Genome(context.speciation().createGenomeId());

        for (Pair<NodeGene> nodePair : fullJoinBetweenNodes(fitParent, unfitParent)) {
            if (nodePair.getLeft() != null && nodePair.getRight() != null) {
                child.nodes.put(getRandom(context.random(), nodePair.getLeft(), nodePair.getRight()));
            } else if (nodePair.getLeft() != null) {
                child.nodes.put(nodePair.getLeft());
            }
        }

        for (ConnectionGene fitConnection : fitParent.connections.getAll()) {
            if (isInnovationIdValid(context.connections(), fitConnection)) {
                ConnectionGene unfitConnection = unfitParent.connections.getAll().getById(fitConnection.getInnovationId());
                ConnectionGene childConnection;

                if (unfitConnection != null) {
                    childConnection = createChildConnection(context, fitConnection, unfitConnection);
                } else {
                    childConnection = fitConnection.createCopy(fitConnection.isExpressed() || context.crossOver().shouldOverrideExpressed());
                }

                child.connections.put(childConnection);
            }
        }

        return child;
    }

    private static Iterable<Pair<ConnectionGene>> fullJoinBetweenConnections(final Genome genome1, final Genome genome2) {
        return () -> genome1.connections.getAll().fullJoin(genome2.connections);
    }

    public static Genome crossOverByEqualTreatment(final Context context, final Genome parent1, final Genome parent2) {
        Genome child = new Genome(context.speciation().createGenomeId());

        for (Pair<NodeGene> nodePair : fullJoinBetweenNodes(parent1, parent2)) {
            if (nodePair.getLeft() != null && nodePair.getRight() != null) {
                child.nodes.put(getRandom(context.random(), nodePair.getLeft(), nodePair.getRight()));
            } else if (nodePair.getLeft() != null) {
                child.nodes.put(nodePair.getLeft());
            } else {
                child.nodes.put(nodePair.getRight());
            }
        }

        for (Pair<ConnectionGene> connectionPair : fullJoinBetweenConnections(parent1, parent2)) {
            if (isInnovationIdValid(context.connections(), connectionPair.getLeft(), connectionPair.getRight())) {
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
        }

        return child;
    }

    private static void addIfValid(final Context.ConnectionGeneSupport connectionGeneSupport, final ConnectionGene connection, final Genome genome) {
        if (isInnovationIdValid(connectionGeneSupport, connection)) {
            genome.connections.put(connection.createClone());
        }
    }

    private Genome createCopy(final String id, final Context.ConnectionGeneSupport connectionGeneSupport) {
        Genome genome = new Genome(id);

        nodes.forEach(genome.nodes::put);
        connections.getAll().forEach(c -> addIfValid(connectionGeneSupport, c, genome));

        return genome;
    }

    public Genome createCopy(final Context context) {
        String id = context.speciation().createGenomeId();

        return createCopy(id, context.connections());
    }

    public Genome createClone(final Context.ConnectionGeneSupport connectionGeneSupport) {
        return createCopy(id, connectionGeneSupport);
    }
}