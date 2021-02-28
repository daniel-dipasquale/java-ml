package com.dipasquale.ai.rl.neat;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class GenomeCrossOver<T extends Comparable<T>> {
    private final Context<T> context;

    private <R> R getRandom(final R item1, final R item2) {
        return context.random().isLessThan(0.5f) ? item1 : item2;
    }

    private boolean shouldEnforceExpressed() {
        return context.random().isLessThan(context.crossOver().enforceExpressedRate());
    }

    private ConnectionGene<T> createChildConnection(final ConnectionGene<T> parent1Connection, final ConnectionGene<T> parent2Connection) {
        ConnectionGene<T> randomParentConnection = getRandom(parent1Connection, parent2Connection);
        boolean expressed = parent1Connection.isExpressed() && parent2Connection.isExpressed() || shouldEnforceExpressed();

        if (context.random().isLessThan(context.crossOver().useRandomParentWeightRate())) {
            return randomParentConnection.createCopy(expressed);
        }

        float weight = (parent1Connection.getWeight() + parent2Connection.getWeight()) / 2f;
        int cyclesAllowed = randomParentConnection.getCyclesAllowed();

        return new ConnectionGene<>(parent1Connection.getInnovationId(), weight, cyclesAllowed, expressed);
    }

    public GenomeDefault<T> crossOverBySkippingUnfitDisjointOrExcess(final GenomeDefault<T> fitParent, final GenomeDefault<T> unfitParent) {
        GenomeDefault<T> child = new GenomeDefault<>(context);

        fitParent.getNodes().forEach(child::addNode);

        for (ConnectionGene<T> fitConnection : fitParent.getConnections()) {
            ConnectionGene<T> unfitConnection = unfitParent.getConnections().getByIdFromAll(fitConnection.getInnovationId());

            if (unfitConnection != null) {
                ConnectionGene<T> childConnection = createChildConnection(fitConnection, unfitConnection);

                child.addConnection(childConnection);
            } else {
                ConnectionGene<T> childConnection = fitConnection.createCopy(fitConnection.isExpressed() || shouldEnforceExpressed());

                child.addConnection(childConnection);
            }
        }

        return child;
    }

    public GenomeDefault<T> crossOverByEqualTreatment(final GenomeDefault<T> parent1, final GenomeDefault<T> parent2) {
        GenomeDefault<T> child = new GenomeDefault<>(context);

        for (SequentialMap<T, NodeGene<T>>.JoinEntry joinEntry : parent1.getNodes().fullJoinFromAll(parent2.getNodes())) {
            if (joinEntry.getItem1() != null && joinEntry.getItem2() != null) {
                child.addNode(getRandom(joinEntry.getItem1(), joinEntry.getItem2()));
            } else if (joinEntry.getItem1() != null) {
                child.addNode(joinEntry.getItem1());
            } else {
                child.addNode(joinEntry.getItem2());
            }
        }

        for (SequentialMap<InnovationId<T>, ConnectionGene<T>>.JoinEntry joinEntry : parent1.getConnections().fullJoinFromAll(parent2.getConnections())) {
            if (joinEntry.getItem1() != null && joinEntry.getItem2() != null) {
                ConnectionGene<T> childConnection = createChildConnection(joinEntry.getItem1(), joinEntry.getItem2());

                child.addConnection(childConnection);
            } else if (joinEntry.getItem1() != null) {
                ConnectionGene<T> childConnection = joinEntry.getItem1().createCopy(joinEntry.getItem1().isExpressed() || shouldEnforceExpressed());

                child.addConnection(childConnection);
            } else {
                ConnectionGene<T> childConnection = joinEntry.getItem2().createCopy(joinEntry.getItem2().isExpressed() || shouldEnforceExpressed());

                child.addConnection(childConnection);
            }
        }

        return child;
    }
}
