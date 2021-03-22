package com.dipasquale.ai.rl.neat.genotype;

import com.dipasquale.ai.common.JointItems;
import com.dipasquale.ai.rl.neat.context.Context;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class GenomeCompatibilityCalculatorDefault implements GenomeCompatibilityCalculator {
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

    private static ConnectionGene getExcessConnection(final GenomeDefault genome1, final GenomeDefault genome2) {
        ConnectionGene lastConnection1 = genome1.getConnections().getLastFromAll();
        ConnectionGene lastConnection2 = genome2.getConnections().getLastFromAll();

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

    @Override
    public float calculateCompatibility(final GenomeDefault genome1, final GenomeDefault genome2) {
        ConnectionGene excessFromConnection = getExcessConnection(genome1, genome2);
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
        int maximumNodes = Math.max(genome1.getNodes().size(), genome2.getNodes().size());
        int n = maximumNodes < 20 ? 1 : maximumNodes;
        float averageWeightDifference = matchingCount == 0 ? 0 : weightDifference / (float) matchingCount;

        return c1 * excessCount / n + c2 * disjointCount / n + c3 * averageWeightDifference;
    }
}
