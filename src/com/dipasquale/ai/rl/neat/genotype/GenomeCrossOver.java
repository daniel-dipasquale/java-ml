package com.dipasquale.ai.rl.neat.genotype;

import com.dipasquale.ai.common.JointItems;
import com.dipasquale.ai.rl.neat.context.Context;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class GenomeCrossOver {
    private <T> T getRandom(final Context context, final T item1, final T item2) {
        return context.random().isLessThan(0.5f) ? item1 : item2;
    }

    private ConnectionGene createChildConnection(final Context context, final ConnectionGene parent1Connection, final ConnectionGene parent2Connection) {
        ConnectionGene randomParentConnection = getRandom(context, parent1Connection, parent2Connection);
        boolean expressed = parent1Connection.isExpressed() && parent2Connection.isExpressed() || context.crossOver().shouldOverrideConnectionExpressed();

        if (context.crossOver().shouldUseRandomParentConnectionWeight()) {
            return randomParentConnection.createCopy(expressed);
        }

        InnovationId innovationId = randomParentConnection.getInnovationId();
        float weight = (parent1Connection.getWeight() + parent2Connection.getWeight()) / 2f;
        int cyclesAllowed = randomParentConnection.getRecurrentCyclesAllowed();

        return new ConnectionGene(innovationId, weight, cyclesAllowed, expressed);
    }

    public GenomeDefault crossOverBySkippingUnfitDisjointOrExcess(final Context context, final GenomeDefault fitParent, final GenomeDefault unfitParent) {
        GenomeDefault child = new GenomeDefault(context);

        for (JointItems<NodeGene> jointItems : fitParent.getNodes().fullJoin(unfitParent.getNodes())) {
            if (jointItems.getItem1() != null && jointItems.getItem2() != null) {
                child.addNode(getRandom(context, jointItems.getItem1(), jointItems.getItem2()));
            } else if (jointItems.getItem1() != null) {
                child.addNode(jointItems.getItem1());
            }
        }

        for (ConnectionGene fitConnection : fitParent.getConnections()) {
            ConnectionGene unfitConnection = unfitParent.getConnections().getByIdFromAll(fitConnection.getInnovationId());
            ConnectionGene childConnection;

            if (unfitConnection != null) {
                childConnection = createChildConnection(context, fitConnection, unfitConnection);
            } else {
                childConnection = fitConnection.createCopy(fitConnection.isExpressed() || context.crossOver().shouldOverrideConnectionExpressed());
            }

            child.addConnection(childConnection);
        }

        return child;
    }

    public GenomeDefault crossOverByEqualTreatment(final Context context, final GenomeDefault parent1, final GenomeDefault parent2) {
        GenomeDefault child = new GenomeDefault(context);

        for (JointItems<NodeGene> jointItems : parent1.getNodes().fullJoin(parent2.getNodes())) {
            if (jointItems.getItem1() != null && jointItems.getItem2() != null) {
                child.addNode(getRandom(context, jointItems.getItem1(), jointItems.getItem2()));
            } else if (jointItems.getItem1() != null) {
                child.addNode(jointItems.getItem1());
            } else {
                child.addNode(jointItems.getItem2());
            }
        }

        for (JointItems<ConnectionGene> jointItems : parent1.getConnections().fullJoinFromAll(parent2.getConnections())) {
            if (jointItems.getItem1() != null && jointItems.getItem2() != null) {
                ConnectionGene childConnection = createChildConnection(context, jointItems.getItem1(), jointItems.getItem2());

                child.addConnection(childConnection);
            } else if (jointItems.getItem1() != null) {
                ConnectionGene childConnection = jointItems.getItem1().createCopy(jointItems.getItem1().isExpressed() || context.crossOver().shouldOverrideConnectionExpressed());

                child.addConnection(childConnection);
            } else {
                ConnectionGene childConnection = jointItems.getItem2().createCopy(jointItems.getItem2().isExpressed() || context.crossOver().shouldOverrideConnectionExpressed());

                child.addConnection(childConnection);
            }
        }

        return child;
    }
}
