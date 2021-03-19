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
    public void process(final SpeciesEvolutionContext context, final Species species) {
        if (!species.shouldSurvive()) {
            return;
        }

        context.replaceOrganismIfMoreFit(species.selectMostElite());
    }

    @Override
    public void postProcess(final SpeciesEvolutionContext context) {
        Organism organismMostFit = context.getOrganismMostFit();

        if (organismsWithoutSpecies.add(organismMostFit)) {
            context.addOrganismsNeeded(-1);
        }

        mostFitOrganismActivator.setOrganism(organismMostFit);
    }
}
