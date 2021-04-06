package com.dipasquale.ai.rl.neat.population;

import java.util.List;

@FunctionalInterface
interface SpeciesBreedStrategy {
    void process(SpeciesBreedContext breedContext, List<Species> speciesList);
}
