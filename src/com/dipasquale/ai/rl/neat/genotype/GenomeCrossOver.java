package com.dipasquale.ai.rl.neat.genotype;

import com.dipasquale.ai.common.JointItems;
import com.dipasquale.ai.rl.neat.context.Context;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class GenomeCrossOver {
    private final Context context;

    private <T> T getRandom(final T item1, final T item2) {
        return context.random().isLessThan(0.5f) ? item1 : item2;
    }

    private boolean shouldOverrideExpressed() {
        return context.random().isLessThan(context.crossOver().overrideExpressedRate());
    }

    private ConnectionGene createChildConnection(final ConnectionGene parent1Connection, final ConnectionGene parent2Connection) {
        ConnectionGene randomParentConnection = getRandom(parent1Connection, parent2Connection);
        boolean expressed = parent1Connection.isExpressed() && parent2Connection.isExpressed() || shouldOverrideExpressed();

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
                childConnection = fitConnection.createCopy(fitConnection.isExpressed() || shouldOverrideExpressed());
            }

            child.addConnection(childConnection);
        }

        return child;
    }

    public GenomeDefault crossOverByEqualTreatment(final GenomeDefault parent1, final GenomeDefault parent2) {
        GenomeDefault child = new GenomeDefault(context);

        for (JointItems<NodeGene> jointItems : parent1.getNodes().fullJoin(parent2.getNodes())) {
            if (jointItems.getItem1() != null && jointItems.getItem2() != null) {
                child.addNode(getRandom(jointItems.getItem1(), jointItems.getItem2()));
            } else if (jointItems.getItem1() != null) {
                child.addNode(jointItems.getItem1());
            } else {
                child.addNode(jointItems.getItem2());
            }
        }

        for (JointItems<ConnectionGene> jointItems : parent1.getConnections().fullJoinFromAll(parent2.getConnections())) {
            if (jointItems.getItem1() != null && jointItems.getItem2() != null) {
                ConnectionGene childConnection = createChildConnection(jointItems.getItem1(), jointItems.getItem2());

                child.addConnection(childConnection);
            } else if (jointItems.getItem1() != null) {
                ConnectionGene childConnection = jointItems.getItem1().createCopy(jointItems.getItem1().isExpressed() || shouldOverrideExpressed());

                child.addConnection(childConnection);
            } else {
                ConnectionGene childConnection = jointItems.getItem2().createCopy(jointItems.getItem2().isExpressed() || shouldOverrideExpressed());

                child.addConnection(childConnection);
            }
        }

        return child;
    }
}
