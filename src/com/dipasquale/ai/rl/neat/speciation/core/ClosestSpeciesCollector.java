package com.dipasquale.ai.rl.neat.speciation.core;

import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.ai.rl.neat.speciation.organism.Organism;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class ClosestSpeciesCollector {
    private static final double MINIMUM_COMPATIBILITY = Double.POSITIVE_INFINITY;
    private final Context.SpeciationSupport speciation;
    private double minimumCompatibility = MINIMUM_COMPATIBILITY;
    private Species closestSpecies = null;

    public boolean collectIfCloser(final Organism organism, final Species species) {
        double compatibility = organism.calculateCompatibility(speciation, species);

        if (Double.compare(minimumCompatibility, compatibility) <= 0) {
            return false;
        }

        minimumCompatibility = compatibility;
        closestSpecies = species;

        return true;
    }

    public boolean isClosestCompatible(final int generation) {
        double compatibilityThreshold = speciation.params().compatibilityThreshold(generation);

        return Double.compare(minimumCompatibility, compatibilityThreshold) < 0;
    }

    public void clear() {
        minimumCompatibility = MINIMUM_COMPATIBILITY;
        closestSpecies = null;
    }

    public Species getAndClear() {
        try {
            return closestSpecies;
        } finally {
            clear();
        }
    }
}