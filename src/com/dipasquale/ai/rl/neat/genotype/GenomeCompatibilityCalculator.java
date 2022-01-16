package com.dipasquale.ai.rl.neat.genotype;

import com.dipasquale.common.Pair;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
public final class GenomeCompatibilityCalculator implements Serializable {
    @Serial
    private static final long serialVersionUID = 8186925797297865215L;
    private final float excessCoefficient; // c1;
    private final float disjointCoefficient; // c2;
    private final float weightDifferenceCoefficient; // c3

    private static boolean isMatching(final Pair<ConnectionGene> connections) {
        return connections.getLeft() != null && connections.getRight() != null;
    }

    private static boolean isExcess(final ConnectionGene connection, final ConnectionGene excessConnection) {
        return connection.getInnovationId().compareTo(excessConnection.getInnovationId()) > 0;
    }

    private static boolean isDisjoint(final Pair<ConnectionGene> connections, final ConnectionGene excessConnection) {
        return excessConnection == null || connections.getLeft() != null && !isExcess(connections.getLeft(), excessConnection) || connections.getRight() != null && !isExcess(connections.getRight(), excessConnection);
    }

    private static ConnectionGene getExcessConnection(final Genome genome1, final Genome genome2) {
        ConnectionGene lastConnection1 = genome1.getConnections().getAll().getLast();
        ConnectionGene lastConnection2 = genome2.getConnections().getAll().getLast();

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

    public double calculateCompatibility(final Genome genome1, final Genome genome2) {
        ConnectionGene excessFromConnection = getExcessConnection(genome1, genome2);
        int matchingCount = 0;
        double weightDifference = 0D;
        int disjointCount = 0;
        int excessCount = 0;

        for (Pair<ConnectionGene> connectionPair : (Iterable<Pair<ConnectionGene>>) () -> genome1.getConnections().getAll().fullJoin(genome2.getConnections())) {
            if (isMatching(connectionPair)) {
                matchingCount++;
                weightDifference += Math.abs(connectionPair.getLeft().getWeight() - connectionPair.getRight().getWeight());
            } else if (isDisjoint(connectionPair, excessFromConnection)) {
                disjointCount++;
            } else {
                excessCount++;
            }
        }

        int maximumNodes = Math.max(genome1.getNodes().size(), genome2.getNodes().size());
        double n = maximumNodes >= 20 ? (double) maximumNodes : 1D;
        double averageWeightDifference = weightDifference / (double) (1 + matchingCount);
        double compatibility = excessCoefficient * (double) excessCount / n + disjointCoefficient * (double) disjointCount / n + weightDifferenceCoefficient * averageWeightDifference;

        if (compatibility == Double.POSITIVE_INFINITY) {
            return Double.MAX_VALUE;
        }

        if (compatibility == Double.NEGATIVE_INFINITY) {
            return -Double.MAX_VALUE;
        }

        return compatibility;
    }
}
