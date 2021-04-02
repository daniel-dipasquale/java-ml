package com.dipasquale.ai.rl.neat.population;

import com.dipasquale.ai.rl.neat.genotype.Organism;
import com.dipasquale.ai.rl.neat.genotype.OrganismFactoryMutation;
import com.dipasquale.ai.rl.neat.species.Species;
import com.dipasquale.common.ObjectFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Queue;
import java.util.Set;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class SpeciesBreedStrategyGenesis implements SpeciesBreedStrategy {
    private final Set<Organism> organismsWithoutSpecies;
    private final Queue<ObjectFactory<Organism>> organismsToBirth;

    @Override
    public void process(final SpeciesBreedContext breedContext, final List<Species> speciesList) {
        for (Species species : speciesList) {
            species.restart().stream()
                    .filter(o -> !organismsWithoutSpecies.contains(o))
                    .forEach(Organism::kill);

            if (organismsWithoutSpecies.remove(species.getRepresentative())) { // TODO: figure out a better way of doing this
                organismsToBirth.add(new OrganismFactoryMutation(species.getRepresentative()));
            }
        }
    }
}
