package com.dipasquale.ai.rl.neat;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class SpeciesEvolutionStrategySelectChampion implements SpeciesEvolutionStrategy {
    private final Set<Organism> organismsWithoutSpecies;
    private final OrganismCollectiveStrategy mostFitCollectiveStrategy;

    @Override
    public void process(final SpeciesEvolutionContext context, final Species species) {
        if (!species.shouldSurvive()) {
            return;
        }

        context.replaceOrganismIfMoreFit(species.selectChampion());
    }

    @Override
    public void postProcess(final SpeciesEvolutionContext context) {
        Organism organismMostFit = context.getOrganismMostFit();

        if (organismsWithoutSpecies.add(organismMostFit)) {
            context.addOrganismsNeeded(-1);
        }

        mostFitCollectiveStrategy.setOrganism(organismMostFit); // TODO: this is assuming elitist are being saved
    }
}
