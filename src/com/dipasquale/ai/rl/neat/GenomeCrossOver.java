package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.common.SequentialId;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class GenomeCrossOver {
    private final Context context;

    private <T> T getRandom(final T item1, final T item2) {
        return context.random().isLessThan(0.5f) ? item1 : item2;
    }

    private boolean shouldEnforceExpressed() {
        return context.random().isLessThan(context.crossOver().enforceExpressedRate());
    }

    private ConnectionGene createChildConnection(final ConnectionGene parent1Connection, final ConnectionGene parent2Connection) {
        ConnectionGene randomParentConnection = getRandom(parent1Connection, parent2Connection);
        boolean expressed = parent1Connection.isExpressed() && parent2Connection.isExpressed() || shouldEnforceExpressed();

        if (context.random().isLessThan(context.crossOver().useRandomParentWeightRate())) {
            return randomParentConnection.createCopy(expressed);
        }

        float weight = (parent1Connection.getWeight() + parent2Connection.getWeight()) / 2f;
        int cyclesAllowed = randomParentConnection.getRecurrentCyclesAllowed();

        return new ConnectionGene(parent1Connection.getInnovationId(), weight, cyclesAllowed, expressed);
    }

    public GenomeDefault crossOverBySkippingUnfitDisjointOrExcess(final GenomeDefault fitParent, final GenomeDefault unfitParent) {
        GenomeDefault child = new GenomeDefault(context);

        fitParent.getNodes().forEach(child::addNode);

        for (ConnectionGene fitConnection : fitParent.getConnections()) {
            ConnectionGene unfitConnection = unfitParent.getConnections().getByIdFromAll(fitConnection.getInnovationId());
            ConnectionGene childConnection;

            if (unfitConnection != null) {
                childConnection = createChildConnection(fitConnection, unfitConnection);
            } else {
                childConnection = fitConnection.createCopy(fitConnection.isExpressed() || shouldEnforceExpressed());
            }

            child.addConnection(childConnection);
        }

        return child;
    }

    public GenomeDefault crossOverByEqualTreatment(final GenomeDefault parent1, final GenomeDefault parent2) {
        GenomeDefault child = new GenomeDefault(context);

        for (SequentialMap<SequentialId, NodeGene>.JoinEntry joinEntry : parent1.getNodes().fullJoinFromAll(parent2.getNodes())) {
            if (joinEntry.getItem1() != null && joinEntry.getItem2() != null) {
                child.addNode(getRandom(joinEntry.getItem1(), joinEntry.getItem2()));
            } else if (joinEntry.getItem1() != null) {
                child.addNode(joinEntry.getItem1());
            } else {
                child.addNode(joinEntry.getItem2());
            }
        }

        for (SequentialMap<InnovationId, ConnectionGene>.JoinEntry joinEntry : parent1.getConnections().fullJoinFromAll(parent2.getConnections())) {
            if (joinEntry.getItem1() != null && joinEntry.getItem2() != null) {
                ConnectionGene childConnection = createChildConnection(joinEntry.getItem1(), joinEntry.getItem2());

                child.addConnection(childConnection);
            } else if (joinEntry.getItem1() != null) {
                ConnectionGene childConnection = joinEntry.getItem1().createCopy(joinEntry.getItem1().isExpressed() || shouldEnforceExpressed());

                child.addConnection(childConnection);
            } else {
                ConnectionGene childConnection = joinEntry.getItem2().createCopy(joinEntry.getItem2().isExpressed() || shouldEnforceExpressed());

                child.addConnection(childConnection);
            }
        }

        return child;
    }
}
