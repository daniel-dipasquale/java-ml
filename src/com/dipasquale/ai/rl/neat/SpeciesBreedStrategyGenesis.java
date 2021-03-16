package com.dipasquale.ai.rl.neat;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class SpeciesBreedStrategyGenesis implements SpeciesBreedStrategy {
    private final Set<Organism> organismsWithoutSpecies;

    @Override
    public void process(final SpeciesBreedContext context, final List<Species> speciesList) {
        for (Species species : speciesList) {
            species.restart();
            organismsWithoutSpecies.remove(species.getRepresentative());
        }
    }
}
