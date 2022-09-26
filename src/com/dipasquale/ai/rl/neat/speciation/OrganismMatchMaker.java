package com.dipasquale.ai.rl.neat.speciation;

import com.dipasquale.ai.rl.neat.NeatContext;
import com.dipasquale.ai.rl.neat.speciation.organism.Organism;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class OrganismMatchMaker {
    private static final float MINIMUM_COMPATIBILITY = Float.POSITIVE_INFINITY;
    private final NeatContext.SpeciationSupport speciationSupport;
    private float bestMatchCompatibility = MINIMUM_COMPATIBILITY;
    private Species bestMatchSpecies = null;

    public Species getBestMatch() {
        return bestMatchSpecies;
    }

    public boolean replaceIfBetterMatch(final Organism organism, final Species species) {
        float compatibility = organism.calculateCompatibility(speciationSupport, species);

        if (Float.compare(bestMatchCompatibility, compatibility) <= 0) {
            return false;
        }

        bestMatchCompatibility = compatibility;
        bestMatchSpecies = species;

        return true;
    }

    public boolean isBestMatchCompatible(final int generation) {
        float compatibilityThreshold = speciationSupport.calculateCompatibilityThreshold(generation);

        return Float.compare(bestMatchCompatibility, compatibilityThreshold) < 0;
    }

    public void clear() {
        bestMatchCompatibility = MINIMUM_COMPATIBILITY;
        bestMatchSpecies = null;
    }
}
