package com.dipasquale.ai.rl.neat.population;

import com.dipasquale.ai.rl.neat.genotype.Organism;
import com.dipasquale.ai.rl.neat.species.Species;
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

            if (organismsWithoutSpecies.remove(species.getRepresentative())) { // TODO: figure out how this should work
                Organism organismNew = species.getRepresentative().createCopy();

                organismNew.mutate();
                organismsWithoutSpecies.add(organismNew);
            }
        }
    }
}
