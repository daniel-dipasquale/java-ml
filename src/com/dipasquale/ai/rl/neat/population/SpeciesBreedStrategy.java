package com.dipasquale.ai.rl.neat.population;

import com.dipasquale.ai.rl.neat.species.Species;

import java.util.List;

@FunctionalInterface
interface SpeciesBreedStrategy {
    void process(SpeciesBreedContext breedContext, List<Species> speciesList);
}
