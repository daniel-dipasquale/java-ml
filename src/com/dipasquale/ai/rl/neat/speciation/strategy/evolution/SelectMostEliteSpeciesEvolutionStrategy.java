/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.ai.rl.neat.speciation.strategy.evolution;

import com.dipasquale.ai.rl.neat.speciation.core.Species;
import com.dipasquale.ai.rl.neat.speciation.organism.Organism;
import com.dipasquale.ai.rl.neat.speciation.organism.OrganismActivator;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

@RequiredArgsConstructor
public final class SelectMostEliteSpeciesEvolutionStrategy implements SpeciesEvolutionStrategy, Serializable {
    @Serial
    private static final long serialVersionUID = 6142238994253254883L;
    private final Set<Organism> organismsWithoutSpecies;
    private final OrganismActivator mostFitOrganismActivator;

    @Override
    public void process(final SpeciesEvolutionContext evolutionContext, final Species species, boolean speciesSurvives) {
        if (!speciesSurvives) {
            return;
        }

        evolutionContext.replaceOrganismIfMoreFit(species.selectMostElite());
    }

    @Override
    public void postProcess(final SpeciesEvolutionContext evolutionContext) {
        Organism organismMostFit = evolutionContext.getOrganismMostFit();

        organismsWithoutSpecies.add(organismMostFit);
        mostFitOrganismActivator.setOrganism(organismMostFit);
    }
}
