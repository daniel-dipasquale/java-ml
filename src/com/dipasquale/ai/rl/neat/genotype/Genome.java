package com.dipasquale.ai.rl.neat.genotype;

import com.dipasquale.ai.rl.neat.Context;
import com.dipasquale.ai.rl.neat.WeightMutationType;
import com.dipasquale.common.Pair;
import com.dipasquale.common.random.ProbabilityClassifier;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
public final class Genome implements Serializable {
    @Serial
    private static final long serialVersionUID = 1467592503532949541L;
    private final int id;
    private final NodeGeneGroup nodeGenes = new NodeGeneGroup();
    private final ConnectionGeneGroup connectionGenes = new ConnectionGeneGroup();

    private boolean mutateWeights(final Context.MutationSupport mutationSupport, final Context.ConnectionGeneSupport connectionGeneSupport) {
        boolean mutated = false;

        for (ConnectionGene connectionGene : connectionGenes.getAll()) {
            WeightMutationType weightMutationType = mutationSupport.generateWeightMutationType();

            switch (weightMutationType) {
                case PERTURB -> {
                    float weight = connectionGeneSupport.perturbWeight(connectionGene.getWeight());

                    connectionGene.setWeight(weight);
                    mutated = true;
                }

                case REPLACE -> {
                    float weight = connectionGeneSupport.generateWeight();

                    connectionGene.setWeight(weight);
                    mutated = true;
                }
            }
        }

        return mutated;
    }

    private boolean disableRandomConnectionGene(final Context.RandomnessSupport randomnessSupport) {
        int size = connectionGenes.getExpressed().size();

        if (size == 0) {
            return false;
        }

        int index = randomnessSupport.generateIndex(size);

        connectionGenes.getExpressed().addCyclesAllowed(index, -1);

        return true;
    }

    private boolean addRandomNodeGene(final Context.RandomnessSupport randomnessSupport, final Context.NodeGeneSupport nodeGeneSupport, final Context.ConnectionGeneSupport connectionGeneSupport) {
        int size = connectionGenes.getExpressed().size();

        if (size == 0) {
            return false;
        }

        int index = randomnessSupport.generateIndex(size);
        ConnectionGene connectionGene = connectionGenes.getExpressed().disableByIndex(index);
        NodeGene sourceNodeGene = nodeGenes.getById(connectionGene.getInnovationId().getSourceNodeGeneId());
        NodeGene targetNodeGene = nodeGenes.getById(connectionGene.getInnovationId().getTargetNodeGeneId());
        NodeGene newNodeGene = nodeGeneSupport.createHidden();
        ConnectionGene sourceToNewConnectionGene = new ConnectionGene(connectionGeneSupport.provideInnovationId(sourceNodeGene, newNodeGene), 1f, connectionGeneSupport.generateRecurrentWeights());
        ConnectionGene newToTargetConnectionGene = new ConnectionGene(connectionGeneSupport.provideInnovationId(newNodeGene, targetNodeGene), connectionGene.getWeight(), connectionGeneSupport.cloneRecurrentWeights(connectionGene.getRecurrentWeights()));

        getNodeGenes().put(newNodeGene);
        getConnectionGenes().put(sourceToNewConnectionGene);
        getConnectionGenes().put(newToTargetConnectionGene);

        return true;
    }

    private NodeGene getRandomNodeGene(final Context.RandomnessSupport randomnessSupport, final NodeGeneType... types) {
        ProbabilityClassifier<NodeGeneType> nodeGeneTypeClassifier = new ProbabilityClassifier<>();

        for (NodeGeneType type : types) {
            int size = nodeGenes.size(type);

            nodeGeneTypeClassifier.add((float) size, type);
        }

        return nodeGenes.getRandom(randomnessSupport, randomnessSupport.generateElement(nodeGeneTypeClassifier));
    }

    private NodeGene getRandomMatchingNodeGene(final Context.RandomnessSupport randomnessSupport, final NodeGeneType type) {
        return switch (type) {
            case INPUT, BIAS -> getRandomNodeGene(randomnessSupport, NodeGeneType.OUTPUT, NodeGeneType.HIDDEN);

            case HIDDEN -> nodeGenes.getRandom(randomnessSupport);

            case OUTPUT -> getRandomNodeGene(randomnessSupport, NodeGeneType.INPUT, NodeGeneType.BIAS, NodeGeneType.HIDDEN);
        };
    }

    private static InnovationId createFeedForwardInnovationIdIfPossible(final Context.ConnectionGeneSupport connectionGeneSupport, final NodeGene nodeGene1, final NodeGene nodeGene2) {
        return switch (ConnectionGene.getType(nodeGene1.getId(), nodeGene2.getId())) {
            case REFLEXIVE -> null;

            case BACKWARD -> connectionGeneSupport.provideInnovationId(nodeGene2, nodeGene1);

            case FORWARD -> connectionGeneSupport.provideInnovationId(nodeGene1, nodeGene2);
        };
    }

    private InnovationId createRandomInnovationId(final Context.RandomnessSupport randomnessSupport, final Context.ConnectionGeneSupport connectionGeneSupport, final boolean shouldAllowRecurrent) {
        int size = nodeGenes.size();

        if (size == 0 || !shouldAllowRecurrent && size == 1) {
            return null;
        }

        NodeGene nodeGene1 = nodeGenes.getByIndex(randomnessSupport.generateIndex(size));

        if (shouldAllowRecurrent && connectionGeneSupport.shouldAllowUnrestrictedDirection()) {
            NodeGene nodeGene2 = nodeGenes.getByIndex(randomnessSupport.generateIndex(size));

            return connectionGeneSupport.provideInnovationId(nodeGene1, nodeGene2);
        }

        NodeGene nodeGene2 = getRandomMatchingNodeGene(randomnessSupport, nodeGene1.getType());

        if (shouldAllowRecurrent) {
            return switch (nodeGene1.getType()) {
                case BIAS -> connectionGeneSupport.provideInnovationId(nodeGene1, nodeGene2);

                default -> switch (nodeGene2.getType()) {
                    case BIAS -> connectionGeneSupport.provideInnovationId(nodeGene2, nodeGene1);

                    default -> connectionGeneSupport.provideInnovationId(nodeGene1, nodeGene2);
                };
            };
        }

        return switch (nodeGene1.getType()) {
            case INPUT, BIAS -> connectionGeneSupport.provideInnovationId(nodeGene1, nodeGene2);

            case OUTPUT -> connectionGeneSupport.provideInnovationId(nodeGene2, nodeGene1);

            case HIDDEN -> switch (nodeGene2.getType()) {
                case INPUT, BIAS -> createFeedForwardInnovationIdIfPossible(connectionGeneSupport, nodeGene2, nodeGene1);

                case OUTPUT, HIDDEN -> createFeedForwardInnovationIdIfPossible(connectionGeneSupport, nodeGene1, nodeGene2);
            };
        };
    }

    private boolean addRandomConnection(final Context.RandomnessSupport randomnessSupport, final Context.ConnectionGeneSupport connectionGeneSupport) {
        boolean shouldAllowRecurrent = connectionGeneSupport.shouldAllowRecurrent();
        InnovationId innovationId = createRandomInnovationId(randomnessSupport, connectionGeneSupport, shouldAllowRecurrent);

        if (innovationId != null) {
            ConnectionGene connectionGene = connectionGenes.getAll().getById(innovationId);

            if (connectionGene == null) {
                connectionGene = new ConnectionGene(innovationId, connectionGeneSupport.generateWeight(), connectionGeneSupport.generateRecurrentWeights());
                getConnectionGenes().put(connectionGene);

                return true;
            }

            if (!connectionGene.isExpressed() || shouldAllowRecurrent && connectionGeneSupport.shouldAllowMultiCycle()) {
                connectionGenes.getExpressed().addCyclesAllowed(connectionGene, 1);

                return true;
            }
        }

        return false;
    }

    public boolean mutate(final Context context) {
        Context.RandomnessSupport randomnessSupport = context.randomness();
        Context.MutationSupport mutationSupport = context.mutation();
        Context.ConnectionGeneSupport connectionGeneSupport = context.connectionGenes();
        boolean mutated = mutateWeights(mutationSupport, connectionGeneSupport);

        if (mutationSupport.shouldDisableExpressedConnection()) {
            mutated |= disableRandomConnectionGene(randomnessSupport);
        }

        if (mutationSupport.shouldAddNode()) {
            mutated |= addRandomNodeGene(randomnessSupport, context.nodeGenes(), connectionGeneSupport);
        }

        if (connectionGenes.getExpressed().isEmpty() || mutationSupport.shouldAddConnection()) {
            mutated |= addRandomConnection(randomnessSupport, connectionGeneSupport);
        }

        return mutated;
    }

    private static int determineCyclesAllowed(final ConnectionGene connectionGene, final boolean expressed) {
        if (!expressed) {
            return 0;
        }

        return Math.max(connectionGene.getCyclesAllowed(), 1);
    }

    private static ConnectionGene createChildConnection(final Context context, final ConnectionGene parent1ConnectionGene, final ConnectionGene parent2ConnectionGene) {
        ConnectionGene randomParentConnectionGene = context.randomness().generateElement(parent1ConnectionGene, parent2ConnectionGene);
        Context.CrossOverSupport crossOverSupport = context.crossOver();
        boolean expressed = parent1ConnectionGene.isExpressed() && parent2ConnectionGene.isExpressed() || crossOverSupport.shouldOverrideExpressedConnection();
        int cyclesAllowed = determineCyclesAllowed(randomParentConnectionGene, expressed);

        if (crossOverSupport.shouldUseWeightFromRandomParent()) {
            return randomParentConnectionGene.createCopy(context.connectionGenes(), cyclesAllowed);
        }

        InnovationId innovationId = randomParentConnectionGene.getInnovationId();
        float weight = (parent1ConnectionGene.getWeight() + parent2ConnectionGene.getWeight()) / 2f;
        List<Float> recurrentWeights = context.connectionGenes().createAverageRecurrentWeights(parent1ConnectionGene.getRecurrentWeights(), parent2ConnectionGene.getRecurrentWeights());

        return new ConnectionGene(innovationId, weight, recurrentWeights, cyclesAllowed);
    }

    private static Iterable<Pair<NodeGene>> fullJoinBetweenNodes(final Genome genome1, final Genome genome2) {
        return () -> genome1.nodeGenes.fullJoin(genome2.nodeGenes);
    }

    private static boolean isInnovationIdValid(final Context.ConnectionGeneSupport connectionGeneSupport, final ConnectionGene connectionGene1, final ConnectionGene connectionGene2) {
        if (connectionGene1 != null) {
            return connectionGeneSupport.containsInnovationId(connectionGene1.getInnovationId());
        }

        return connectionGeneSupport.containsInnovationId(connectionGene2.getInnovationId());
    }

    private static boolean isInnovationIdValid(final Context.ConnectionGeneSupport connectionGeneSupport, final ConnectionGene connectionGene) {
        return isInnovationIdValid(connectionGeneSupport, connectionGene, null);
    }

    public static Genome crossOverBySkippingUnfitDisjointOrExcess(final Context context, final Genome fitParent, final Genome unfitParent) {
        Genome child = new Genome(context.speciation().createGenomeId());
        Context.RandomnessSupport randomnessSupport = context.randomness();

        for (Pair<NodeGene> nodeGenePair : fullJoinBetweenNodes(fitParent, unfitParent)) {
            if (nodeGenePair.getLeft() != null && nodeGenePair.getRight() != null) {
                child.nodeGenes.put(randomnessSupport.generateElement(nodeGenePair.getLeft(), nodeGenePair.getRight()));
            } else if (nodeGenePair.getLeft() != null) {
                child.nodeGenes.put(nodeGenePair.getLeft());
            }
        }

        Context.ConnectionGeneSupport connectionGeneSupport = context.connectionGenes();
        Context.CrossOverSupport crossOverSupport = context.crossOver();

        for (ConnectionGene fitConnectionGene : fitParent.connectionGenes.getAll()) {
            if (isInnovationIdValid(connectionGeneSupport, fitConnectionGene)) {
                ConnectionGene unfitConnectionGene = unfitParent.connectionGenes.getAll().getById(fitConnectionGene.getInnovationId());
                ConnectionGene childConnectionGene;

                if (unfitConnectionGene == null) {
                    boolean expressed = fitConnectionGene.isExpressed() || crossOverSupport.shouldOverrideExpressedConnection();
                    int cyclesAllowed = determineCyclesAllowed(fitConnectionGene, expressed);

                    childConnectionGene = fitConnectionGene.createCopy(connectionGeneSupport, cyclesAllowed);
                } else {
                    childConnectionGene = createChildConnection(context, fitConnectionGene, unfitConnectionGene);
                }

                child.connectionGenes.put(childConnectionGene);
            }
        }

        return child;
    }

    private static Iterable<Pair<ConnectionGene>> fullJoinBetweenConnections(final Genome genome1, final Genome genome2) {
        return () -> genome1.connectionGenes.getAll().fullJoin(genome2.connectionGenes);
    }

    public static Genome crossOverByEqualTreatment(final Context context, final Genome parent1, final Genome parent2) {
        Genome child = new Genome(context.speciation().createGenomeId());
        Context.RandomnessSupport randomnessSupport = context.randomness();

        for (Pair<NodeGene> nodeGenePair : fullJoinBetweenNodes(parent1, parent2)) {
            if (nodeGenePair.getLeft() != null && nodeGenePair.getRight() != null) {
                child.nodeGenes.put(randomnessSupport.generateElement(nodeGenePair.getLeft(), nodeGenePair.getRight()));
            } else if (nodeGenePair.getLeft() != null) {
                child.nodeGenes.put(nodeGenePair.getLeft());
            } else {
                child.nodeGenes.put(nodeGenePair.getRight());
            }
        }

        Context.ConnectionGeneSupport connectionGeneSupport = context.connectionGenes();
        Context.CrossOverSupport crossOverSupport = context.crossOver();

        for (Pair<ConnectionGene> connectionGenePair : fullJoinBetweenConnections(parent1, parent2)) {
            if (isInnovationIdValid(connectionGeneSupport, connectionGenePair.getLeft(), connectionGenePair.getRight())) {
                if (connectionGenePair.getLeft() != null && connectionGenePair.getRight() != null) {
                    ConnectionGene childConnectionGene = createChildConnection(context, connectionGenePair.getLeft(), connectionGenePair.getRight());

                    child.connectionGenes.put(childConnectionGene);
                } else if (connectionGenePair.getLeft() != null) {
                    boolean expressed = connectionGenePair.getLeft().isExpressed() || crossOverSupport.shouldOverrideExpressedConnection();
                    int cyclesAllowed = determineCyclesAllowed(connectionGenePair.getLeft(), expressed);
                    ConnectionGene childConnectionGene = connectionGenePair.getLeft().createCopy(connectionGeneSupport, cyclesAllowed);

                    child.connectionGenes.put(childConnectionGene);
                } else {
                    boolean expressed = connectionGenePair.getRight().isExpressed() || crossOverSupport.shouldOverrideExpressedConnection();
                    int cyclesAllowed = determineCyclesAllowed(connectionGenePair.getRight(), expressed);
                    ConnectionGene childConnectionGene = connectionGenePair.getRight().createCopy(connectionGeneSupport, cyclesAllowed);

                    child.connectionGenes.put(childConnectionGene);
                }
            }
        }

        return child;
    }

    private static void addConnectionIfValid(final Context.ConnectionGeneSupport connectionGeneSupport, final ConnectionGene connectionGene, final Genome genome) {
        if (connectionGeneSupport == null || isInnovationIdValid(connectionGeneSupport, connectionGene)) {
            genome.connectionGenes.put(connectionGene.createClone(connectionGeneSupport));
        }
    }

    private Genome createCopy(final int id, final Context.ConnectionGeneSupport connectionGeneSupport) {
        Genome genome = new Genome(id);

        nodeGenes.forEach(genome.nodeGenes::put);
        connectionGenes.getAll().forEach(connectionGene -> addConnectionIfValid(connectionGeneSupport, connectionGene, genome));

        return genome;
    }

    public Genome createCopy(final Context context) {
        int id = context.speciation().createGenomeId();

        return createCopy(id, context.connectionGenes());
    }

    public Genome createClone(final Context.ConnectionGeneSupport connectionGeneSupport) {
        return createCopy(id, connectionGeneSupport);
    }
}