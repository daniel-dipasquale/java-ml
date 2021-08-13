/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.ai.rl.neat.speciation.strategy.evolution;

import com.dipasquale.ai.rl.neat.speciation.organism.Organism;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;

@Getter
public final class SpeciesEvolutionContext implements Serializable {
    @Serial
    private static final long serialVersionUID = 2714591384142252796L;
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
