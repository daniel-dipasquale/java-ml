package com.dipasquale.ai.rl.neat;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class ContextDefaultSpeciation implements Context.Speciation {
    private final int maximumGenomes;
    private final float weightDifferenceCoefficient;
    private final float disjointCoefficient;
    private final float excessCoefficient;
    private final float compatibilityThreshold;
    private final float compatibilityThresholdModifier;
    private final GenomeCompatibilityCalculator genomeCompatibilityCalculator;
    private final float survivalThreshold;
    private final float elitistThreshold;
    private final int elitistThresholdMinimum;
    private final int stagnationDropOffAge;
    private final float interspeciesMatingRate;

    @Override
    public int maximumGenomes() {
        return maximumGenomes;
    }

    @Override
    public float weightDifferenceCoefficient() {
        return weightDifferenceCoefficient;
    }

    @Override
    public float disjointCoefficient() {
        return disjointCoefficient;
    }

    @Override
    public float excessCoefficient() {
        return excessCoefficient;
    }

    @Override
    public float compatibilityThreshold(final int generation) {
        return compatibilityThreshold * (float) Math.pow(compatibilityThresholdModifier, generation);
    }

    @Override
    public float calculateCompatibility(final GenomeDefault genome1, final GenomeDefault genome2) {
        return genomeCompatibilityCalculator.calculateCompatibility(genome1, genome2);
    }

    @Override
    public float eugenicsThreshold() {
        return survivalThreshold;
    }

    @Override
    public float elitistThreshold() {
        return elitistThreshold;
    }

    @Override
    public int elitistThresholdMinimum() {
        return elitistThresholdMinimum;
    }

    @Override
    public int stagnationDropOffAge() {
        return stagnationDropOffAge;
    }

    @Override
    public float interspeciesMatingRate() {
        return interspeciesMatingRate;
    }
}
