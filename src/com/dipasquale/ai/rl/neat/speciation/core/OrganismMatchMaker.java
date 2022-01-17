package com.dipasquale.ai.rl.neat.speciation.core;

import com.dipasquale.ai.rl.neat.core.Context;
import com.dipasquale.ai.rl.neat.speciation.organism.Organism;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class OrganismMatchMaker {
    private static final double MINIMUM_COMPATIBILITY = Double.POSITIVE_INFINITY;
    private final Context.SpeciationSupport speciationSupport;
    private double bestMatchCompatibility = MINIMUM_COMPATIBILITY;
    private Species bestMatchSpecies = null;

    public Species getBestMatch() {
        return bestMatchSpecies;
    }

    public boolean replaceIfBetterMatch(final Organism organism, final Species species) {
        double compatibility = organism.calculateCompatibility(speciationSupport, species);

        if (Double.compare(bestMatchCompatibility, compatibility) <= 0) {
            return false;
        }

        bestMatchCompatibility = compatibility;
        bestMatchSpecies = species;

        return true;
    }

    public boolean isBestMatchCompatible(final int generation) {
        double compatibilityThreshold = speciationSupport.params().compatibilityThreshold(generation);

        return Double.compare(bestMatchCompatibility, compatibilityThreshold) < 0;
    }

    public void clear() {
        bestMatchCompatibility = MINIMUM_COMPATIBILITY;
        bestMatchSpecies = null;
    }
}
