package com.dipasquale.ai.rl.neat.population;

import com.dipasquale.ai.rl.neat.genotype.Organism;
import lombok.Getter;

@Getter
final class SpeciesEvolutionContext {
    private float totalSharedFitness = 0f;
    private Organism organismMostFit = null;

    public void addTotalSharedFitness(final float delta) {
        totalSharedFitness += delta;
    }

    public boolean replaceOrganismIfMoreFit(final Organism organism) {
        if (organismMostFit == null) {
            organismMostFit = organism;

            return false;
        }

        if (organismMostFit.compareTo(organism) >= 0) {
            return false;
        }

        organismMostFit = organism;

        return true;
    }
}
