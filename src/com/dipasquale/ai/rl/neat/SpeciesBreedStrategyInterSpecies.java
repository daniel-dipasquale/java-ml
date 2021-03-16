package com.dipasquale.ai.rl.neat;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class SpeciesBreedStrategyInterSpecies implements SpeciesBreedStrategy {
    private final Context neatContext;
    private final Set<Organism> organismsWithoutSpecies;

    @Override
    public void process(final SpeciesBreedContext context, final List<Species> speciesList) {
        if (speciesList.size() <= 1) {
            return;
        }

        float organismsToReproduce = (float) context.getOrganismsNeeded() * neatContext.speciation().interspeciesMatingRate() + context.getInterSpeciesBreedingLeftOverRatio();
        int organismsToReproduceFixed = (int) Math.floor(organismsToReproduce);

        context.setInterSpeciesBreedingLeftOverRatio(organismsToReproduce - (float) organismsToReproduceFixed); // TODO: revisit this algorithm

        for (int i = 0; i < organismsToReproduceFixed; i++) {
            Species species1 = neatContext.random().nextItem(speciesList);
            Species species2 = neatContext.random().nextItem(speciesList);

            organismsWithoutSpecies.add(species1.reproduceOutcast(species2));
        }

        context.addOrganismsNeeded(-organismsToReproduceFixed);
    }
}
