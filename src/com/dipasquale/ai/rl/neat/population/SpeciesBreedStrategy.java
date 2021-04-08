package com.dipasquale.ai.rl.neat.population;

import com.dipasquale.ai.rl.neat.genotype.Species;

import java.io.Serializable;
import java.util.List;

@FunctionalInterface
interface SpeciesBreedStrategy extends Serializable {
    void process(SpeciesBreedContext breedContext, List<Species> speciesList);
}
