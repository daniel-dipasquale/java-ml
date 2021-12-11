package com.dipasquale.ai.rl.neat.genotype;

import com.dipasquale.ai.common.output.OutputClassifier;
import com.dipasquale.ai.rl.neat.common.WeightMutationType;
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

    private boolean mutateWeights(final Context context) {
        boolean mutated = false;

        for (ConnectionGene connection : connections.getAll()) {
            WeightMutationType weightMutationType = context.mutation().generateWeightMutationType();

            switch (weightMutationType) {
                case PERTURB -> {
                    float weight = context.connections().perturbWeight(connection.getWeight());

                    connection.setWeight(weight);
                    mutated = true;
                }

                case REPLACE -> {
                    float weight = context.connections().generateWeight();

                    connection.setWeight(weight);
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

        int index = randomSupport.generateIndex(size);

        connections.getExpressed().addCyclesAllowed(index, -1);

        return true;
    }

    private boolean addRandomNode(final Context context) {
        int size = connections.getExpressed().size();

        if (size == 0) {
            return false;
        }

        int index = context.random().generateIndex(size);
        ConnectionGene connection = connections.getExpressed().disableByIndex(index);
        NodeGene inNode = nodes.getById(connection.getInnovationId().getSourceNodeId());
        NodeGene outNode = nodes.getById(connection.getInnovationId().getTargetNodeId());
        NodeGene newNode = context.nodes().createHidden();
        ConnectionGene inToNewConnection = new ConnectionGene(context.connections().provideInnovationId(inNode, newNode), 1f);
        ConnectionGene newToOutConnection = new ConnectionGene(context.connections().provideInnovationId(newNode, outNode), connection.getWeight());

        getNodes().put(newNode);
        getConnections().put(inToNewConnection);
        getConnections().put(newToOutConnection);

        return true;
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

    private static InnovationId createFeedForwardInnovationIdIfPossible(final Context.ConnectionGeneSupport connectionGeneSupport, final NodeGene node1, final NodeGene node2) {
        return switch (ConnectionGene.getType(node1.getId(), node2.getId())) {
            case REFLEXIVE -> null;

            case BACKWARD -> connectionGeneSupport.provideInnovationId(node2, node1);

            case FORWARD -> connectionGeneSupport.provideInnovationId(node1, node2);
        };
    }

    private InnovationId createRandomInnovationId(final Context context, final boolean shouldAllowRecurrent) {
        if (nodes.size() <= 1) {
            return null;
        }

        NodeGene node1 = nodes.getByIndex(context.random().generateIndex(nodes.size()));
        NodeGene node2 = getRandomNodeToMatch(context.random(), node1.getType());

        if (shouldAllowRecurrent) {
            return switch (node1.getType()) {
                case BIAS -> context.connections().provideInnovationId(node1, node2);

                default -> switch (node2.getType()) {
                    case BIAS -> context.connections().provideInnovationId(node2, node1);

                    default -> context.connections().provideInnovationId(node1, node2);
                };
            };
        }

        return switch (node1.getType()) {
            case INPUT, BIAS -> context.connections().provideInnovationId(node1, node2);

            case OUTPUT -> context.connections().provideInnovationId(node2, node1);

            case HIDDEN -> switch (node2.getType()) {
                case INPUT, BIAS -> createFeedForwardInnovationIdIfPossible(context.connections(), node2, node1);

                case OUTPUT, HIDDEN -> createFeedForwardInnovationIdIfPossible(context.connections(), node1, node2);
            };
        };
    }

    private boolean addRandomConnection(final Context context) {
        boolean shouldAllowRecurrent = context.connections().shouldAllowRecurrent();
        InnovationId innovationId = createRandomInnovationId(context, shouldAllowRecurrent);

        if (innovationId != null) {
            ConnectionGene connection = connections.getAll().getById(innovationId);

            if (connection == null) {
                getConnections().put(new ConnectionGene(innovationId, context.connections().generateWeight()));

                return true;
            }

            if (!connection.isExpressed() || shouldAllowRecurrent && context.connections().shouldAllowMultiCycle()) {
                connections.getExpressed().addCyclesAllowed(connection, 1); // TODO: consider balancing out the cycles (meaning if a cycle between two nodes is larger than the reverse cycle, then reverse it

                return true;
            }
        }

        return false;
    }

    public boolean mutate(final Context context) {
        boolean mutated = mutateWeights(context);

        if (context.mutation().shouldDisableExpressedConnection()) {
            mutated |= disableRandomConnection(context.random());
        }

        if (context.mutation().shouldAddNode()) {
            mutated |= addRandomNode(context);
        }

        if (connections.getExpressed().isEmpty() || context.mutation().shouldAddConnection()) {
            mutated |= addRandomConnection(context); // TODO: determine if this algorithm is consistent enough when converging
        }

        return mutated;
    }

    private static <T> T getRandom(final Context.RandomSupport randomSupport, final T item1, final T item2) {
        return randomSupport.isLessThan(0.5f) ? item1 : item2;
    }

    private static int determineCyclesAllowed(final ConnectionGene connection, final boolean expressed) {
        if (!expressed) {
            return 0;
        }

        return Math.max(connection.getCyclesAllowed(), 1);
    }

    private static ConnectionGene createChildConnection(final Context context, final ConnectionGene parent1Connection, final ConnectionGene parent2Connection) {
        ConnectionGene randomParentConnection = getRandom(context.random(), parent1Connection, parent2Connection);
        boolean expressed = parent1Connection.isExpressed() && parent2Connection.isExpressed() || context.crossOver().shouldOverrideExpressedConnection();
        int cyclesAllowed = determineCyclesAllowed(randomParentConnection, expressed);

        if (context.crossOver().shouldUseWeightFromRandomParent()) {
            return randomParentConnection.createCopy(cyclesAllowed);
        }

        InnovationId innovationId = randomParentConnection.getInnovationId();
        float weight = (parent1Connection.getWeight() + parent2Connection.getWeight()) / 2f;

        return new ConnectionGene(innovationId, weight, cyclesAllowed);
    }

    private static Iterable<Pair<NodeGene>> fullJoinBetweenNodes(final Genome genome1, final Genome genome2) {
        return () -> genome1.nodes.fullJoin(genome2.nodes);
    }

    private static boolean isInnovationIdValid(final Context.ConnectionGeneSupport connectionGeneSupport, final ConnectionGene connection1, final ConnectionGene connection2) {
        if (connection1 != null) {
            return connectionGeneSupport.containsInnovationId(connection1.getInnovationId());
        }

        return connectionGeneSupport.containsInnovationId(connection2.getInnovationId());
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

                if (unfitConnection == null) {
                    boolean expressed = fitConnection.isExpressed() || context.crossOver().shouldOverrideExpressedConnection();
                    int cyclesAllowed = determineCyclesAllowed(fitConnection, expressed);

                    childConnection = fitConnection.createCopy(cyclesAllowed);
                } else {
                    childConnection = createChildConnection(context, fitConnection, unfitConnection);
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
                    boolean expressed = connectionPair.getLeft().isExpressed() || context.crossOver().shouldOverrideExpressedConnection();
                    int cyclesAllowed = determineCyclesAllowed(connectionPair.getLeft(), expressed);
                    ConnectionGene childConnection = connectionPair.getLeft().createCopy(cyclesAllowed);

                    child.connections.put(childConnection);
                } else {
                    boolean expressed = connectionPair.getRight().isExpressed() || context.crossOver().shouldOverrideExpressedConnection();
                    int cyclesAllowed = determineCyclesAllowed(connectionPair.getRight(), expressed);
                    ConnectionGene childConnection = connectionPair.getRight().createCopy(cyclesAllowed);

                    child.connections.put(childConnection);
                }
            }
        }

        return child;
    }

    private static void addConnectionIfValid(final Context.ConnectionGeneSupport connectionGeneSupport, final ConnectionGene connection, final Genome genome) {
        if (connectionGeneSupport == null || isInnovationIdValid(connectionGeneSupport, connection)) {
            genome.connections.put(connection.createClone());
        }
    }

    private Genome createCopy(final String id, final Context.ConnectionGeneSupport connectionGeneSupport) {
        Genome genome = new Genome(id);

        nodes.forEach(genome.nodes::put);
        connections.getAll().forEach(c -> addConnectionIfValid(connectionGeneSupport, c, genome));

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