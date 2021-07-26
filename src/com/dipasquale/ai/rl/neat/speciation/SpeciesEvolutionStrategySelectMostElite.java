package com.dipasquale.ai.rl.neat.speciation;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class SpeciesEvolutionStrategySelectMostElite implements SpeciesEvolutionStrategy, Serializable {
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
