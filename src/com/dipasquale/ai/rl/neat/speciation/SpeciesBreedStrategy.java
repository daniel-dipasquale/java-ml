package com.dipasquale.ai.rl.neat.speciation;

import java.io.Serializable;
import java.util.List;

@FunctionalInterface
interface SpeciesBreedStrategy extends Serializable {
    void process(SpeciesBreedContext breedContext, List<Species> speciesList);
}
