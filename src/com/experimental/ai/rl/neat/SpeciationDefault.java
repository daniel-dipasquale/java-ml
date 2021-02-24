package com.experimental.ai.rl.neat;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class SpeciationDefault<T extends Comparable<T>> implements Context.Speciation<T> {
    private final int maximumSize;
    private final float weightDifferenceCoefficient;
    private final float disjointCoefficient;
    private final float excessCoefficient;
    private final float compatibilityThreshold;
    private final float compatibilityThresholdModifier;
    private final GenomeCompatibilityCalculator<T> genomeCompatibilityCalculator;
    private final float survivalThreshold;
    private final float elitistThreshold;
    private final int dropOffAge;

    @Override
    public int maximumSize() {
        return maximumSize;
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
    public float compatibilityThreshold(final int generationNumber) {
        return compatibilityThreshold * (float) Math.pow(compatibilityThresholdModifier, generationNumber);
    }

    @Override
    public float calculateCompatibility(final GenomeDefault<T> genome1, final GenomeDefault<T> genome2) {
        return genomeCompatibilityCalculator.calculateCompatibility(genome1, genome2);
    }

    @Override
    public float survivalThreshold() {
        return survivalThreshold;
    }

    @Override
    public float elitistThreshold() {
        return elitistThreshold;
    }

    @Override
    public int dropOffAge() {
        return dropOffAge;
    }
}
