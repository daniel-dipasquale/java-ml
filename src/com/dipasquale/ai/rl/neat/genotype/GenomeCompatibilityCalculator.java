package com.dipasquale.ai.rl.neat.genotype;

import com.dipasquale.common.LimitSupport;
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

    private static boolean isMatching(final Pair<ConnectionGene> connectionGenePair) {
        return connectionGenePair.getLeft() != null && connectionGenePair.getRight() != null;
    }

    private static boolean isExcess(final ConnectionGene connectionGene, final ConnectionGene excessConnectionGene) {
        return connectionGene.getInnovationId().compareTo(excessConnectionGene.getInnovationId()) > 0;
    }

    private static boolean isDisjoint(final Pair<ConnectionGene> connectionGenePair, final ConnectionGene excessConnectionGene) {
        return excessConnectionGene == null
                || connectionGenePair.getLeft() != null && !isExcess(connectionGenePair.getLeft(), excessConnectionGene)
                || connectionGenePair.getRight() != null && !isExcess(connectionGenePair.getRight(), excessConnectionGene);
    }

    private static ConnectionGene getExcessConnectionGene(final Genome genome1, final Genome genome2) {
        ConnectionGene lastConnectionGene1 = genome1.getConnectionGenes().getAll().getLast();
        ConnectionGene lastConnectionGene2 = genome2.getConnectionGenes().getAll().getLast();

        if (lastConnectionGene1 == null || lastConnectionGene2 == null) {
            return null;
        }

        int comparison = lastConnectionGene1.getInnovationId().compareTo(lastConnectionGene2.getInnovationId());

        if (comparison == 0) {
            return null;
        }

        if (comparison < 0) {
            return lastConnectionGene1;
        }

        return lastConnectionGene2;
    }

    public float calculateCompatibility(final Genome genome1, final Genome genome2) {
        ConnectionGene excessFromConnectionGene = getExcessConnectionGene(genome1, genome2);
        int matchingCount = 0;
        float weightDifference = 0f;
        int disjointCount = 0;
        int excessCount = 0;

        for (Pair<ConnectionGene> connectionGenePair : (Iterable<Pair<ConnectionGene>>) () -> genome1.getConnectionGenes().getAll().fullJoin(genome2.getConnectionGenes())) {
            if (isMatching(connectionGenePair)) {
                matchingCount++;
                weightDifference += Math.abs(connectionGenePair.getLeft().getWeight() - connectionGenePair.getRight().getWeight());
            } else if (isDisjoint(connectionGenePair, excessFromConnectionGene)) {
                disjointCount++;
            } else {
                excessCount++;
            }
        }

        int maximumNodes = Math.max(genome1.getNodeGenes().size(), genome2.getNodeGenes().size());
        float n = Math.max(maximumNodes - 20, 1f);
        float averageWeightDifference = weightDifference / (float) (1 + matchingCount);
        float compatibility = excessCoefficient * (float) excessCount / n + disjointCoefficient * (float) disjointCount / n + weightDifferenceCoefficient * averageWeightDifference;

        return LimitSupport.getFiniteValue(compatibility);
    }
}
