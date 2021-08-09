package com.dipasquale.ai.rl.neat.genotype;

import com.dipasquale.common.Pair;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
public final class DefaultGenomeCompatibilityCalculator implements GenomeCompatibilityCalculator, Serializable {
    @Serial
    private static final long serialVersionUID = -657873611756824223L;
    private final float excessCoefficient; // c1;
    private final float disjointCoefficient; // c2;
    private final float weightDifferenceCoefficient; // c3

    private static boolean isMatching(final Pair<ConnectionGene> connections) {
        return connections.getLeft() != null && connections.getRight() != null;
    }

    private static boolean isExcess(final ConnectionGene connection, final ConnectionGene excessFromConnection) {
        return connection.getInnovationId().compareTo(excessFromConnection.getInnovationId()) > 0;
    }

    private static boolean isDisjoint(final Pair<ConnectionGene> connections, final ConnectionGene excessFromConnection) {
        return excessFromConnection == null || connections.getLeft() != null && !isExcess(connections.getLeft(), excessFromConnection) || connections.getRight() != null && !isExcess(connections.getRight(), excessFromConnection);
    }

    private static ConnectionGene getExcessConnection(final DefaultGenome genome1, final DefaultGenome genome2) {
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
    public double calculateCompatibility(final DefaultGenome genome1, final DefaultGenome genome2) {
        ConnectionGene excessFromConnection = getExcessConnection(genome1, genome2);
        int matchingCount = 0;
        double weightDifference = 0D;
        int disjointCount = 0;
        int excessCount = 0;

        for (Pair<ConnectionGene> connections : genome1.getConnections().fullJoinFromAll(genome2.getConnections())) {
            if (isMatching(connections)) {
                matchingCount++;
                weightDifference += Math.abs(connections.getLeft().getWeight() - connections.getRight().getWeight());
            } else if (isDisjoint(connections, excessFromConnection)) {
                disjointCount++;
            } else {
                excessCount++;
            }
        }

        int maximumNodes = Math.max(genome1.getNodes().size(), genome2.getNodes().size());
        double n = maximumNodes < 20 ? 1D : (double) maximumNodes;
        double averageWeightDifference = matchingCount == 0 ? 0D : weightDifference / (double) matchingCount;

        return excessCoefficient * (double) excessCount / n + disjointCoefficient * (double) disjointCount / n + weightDifferenceCoefficient * averageWeightDifference;
    }
}