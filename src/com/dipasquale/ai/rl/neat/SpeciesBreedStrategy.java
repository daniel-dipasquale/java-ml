package com.dipasquale.ai.rl.neat;

import java.util.List;

@FunctionalInterface
interface SpeciesBreedStrategy {
    void process(SpeciesBreedContext context, List<Species> speciesList);
}
