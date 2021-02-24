package com.experimental.ai.rl.neat;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class GenomeCompatibilityCalculator<T extends Comparable<T>> {
    private final Context.Speciation<T> speciation;

    private static <T extends Comparable<T>> boolean isMatching(final SequentialMap<InnovationId<T>, ConnectionGene<T>>.JoinEntry joinEntry) {
        return joinEntry.getItem1() != null && joinEntry.getItem2() != null;
    }

    private static <T extends Comparable<T>> boolean isExcess(final ConnectionGene<T> connection, final ConnectionGene<T> excessFromConnection) {
        return connection.getInnovationId().compareTo(excessFromConnection.getInnovationId()) > 0;
    }

    private static <T extends Comparable<T>> boolean isDisjoint(final SequentialMap<InnovationId<T>, ConnectionGene<T>>.JoinEntry joinEntry, final ConnectionGene<T> excessFromConnection) {
        return excessFromConnection == null || joinEntry.getItem1() != null && !isExcess(joinEntry.getItem1(), excessFromConnection) || joinEntry.getItem2() != null && !isExcess(joinEntry.getItem2(), excessFromConnection);
    }

    public float calculateCompatibility(final Genome<T> genome1, final Genome<T> genome2) {
        int genome1MaximumConnections = genome1.getConnections().sizeFromAll();
        int genome2MaximumConnections = genome2.getConnections().sizeFromAll();
        int maximumConnections = Math.max(genome1MaximumConnections, genome2MaximumConnections);

        ConnectionGene<T> excessFromConnection = genome1MaximumConnections == maximumConnections && genome2MaximumConnections == maximumConnections
                ? null
                : genome2MaximumConnections == maximumConnections
                ? genome1.getConnections().getLastFromAll()
                : genome2.getConnections().getLastFromAll();

        int matchingCount = 0;
        float weightDifference = 0f;
        int disjointCount = 0;
        int excessCount = 0;

        for (SequentialMap<InnovationId<T>, ConnectionGene<T>>.JoinEntry joinEntry : genome1.getConnections().fullJoinFromAll(genome2.getConnections())) {
            if (isMatching(joinEntry)) {
                matchingCount++;
                weightDifference += Math.abs(joinEntry.getItem1().getWeight() - joinEntry.getItem2().getWeight());
            } else if (isDisjoint(joinEntry, excessFromConnection)) {
                disjointCount++;
            } else {
                excessCount++;
            }
        }

        float c1 = speciation.excessCoefficient();
        float c2 = speciation.disjointCoefficient();
        float c3 = speciation.weightDifferenceCoefficient();
        int n = maximumConnections < 20 ? 1 : maximumConnections;
        float averageWeightDifference = matchingCount == 0 ? 0 : weightDifference / (float) matchingCount;

        return c1 * excessCount / n + c2 * disjointCount / n + c3 * averageWeightDifference;
    }
}
