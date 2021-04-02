package com.dipasquale.ai.rl.neat.population;

import com.dipasquale.ai.rl.neat.genotype.Organism;
import com.dipasquale.ai.rl.neat.species.Species;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class SpeciesEvolutionStrategySelectMostElite implements SpeciesEvolutionStrategy {
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
