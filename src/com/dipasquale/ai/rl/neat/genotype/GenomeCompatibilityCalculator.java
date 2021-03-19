package com.dipasquale.ai.rl.neat.genotype;

import com.dipasquale.ai.common.JointItems;
import com.dipasquale.ai.rl.neat.context.Context;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class GenomeCompatibilityCalculator {
    private final Context context;

    private static boolean isMatching(final JointItems<ConnectionGene> jointItems) {
        return jointItems.getItem1() != null && jointItems.getItem2() != null;
    }

    private static boolean isExcess(final ConnectionGene connection, final ConnectionGene excessFromConnection) {
        return connection.getInnovationId().compareTo(excessFromConnection.getInnovationId()) > 0;
    }

    private static boolean isDisjoint(final JointItems<ConnectionGene> jointItems, final ConnectionGene excessFromConnection) {
        return excessFromConnection == null || jointItems.getItem1() != null && !isExcess(jointItems.getItem1(), excessFromConnection) || jointItems.getItem2() != null && !isExcess(jointItems.getItem2(), excessFromConnection);
    }

    public float calculateCompatibility(final GenomeDefault genome1, final GenomeDefault genome2) {
        int genome1MaximumConnections = genome1.getConnections().sizeFromAll();
        int genome2MaximumConnections = genome2.getConnections().sizeFromAll();
        int maximumConnections = Math.max(genome1MaximumConnections, genome2MaximumConnections);

        ConnectionGene excessFromConnection = genome1MaximumConnections == maximumConnections && genome2MaximumConnections == maximumConnections
                ? null
                : genome2MaximumConnections == maximumConnections
                ? genome1.getConnections().getLastFromAll()
                : genome2.getConnections().getLastFromAll();

        int matchingCount = 0;
        float weightDifference = 0f;
        int disjointCount = 0;
        int excessCount = 0;

        for (JointItems<ConnectionGene> jointItems : genome1.getConnections().fullJoinFromAll(genome2.getConnections())) {
            if (isMatching(jointItems)) {
                matchingCount++;
                weightDifference += Math.abs(jointItems.getItem1().getWeight() - jointItems.getItem2().getWeight());
            } else if (isDisjoint(jointItems, excessFromConnection)) {
                disjointCount++;
            } else {
                excessCount++;
            }
        }

        float c1 = context.speciation().excessCoefficient();
        float c2 = context.speciation().disjointCoefficient();
        float c3 = context.speciation().weightDifferenceCoefficient();
        int n = maximumConnections < 20 ? 1 : maximumConnections;
        float averageWeightDifference = matchingCount == 0 ? 0 : weightDifference / (float) matchingCount;

        return c1 * excessCount / n + c2 * disjointCount / n + c3 * averageWeightDifference;
    }
}
