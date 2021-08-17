package com.dipasquale.ai.rl.neat.speciation.strategy.breeding;

import com.dipasquale.ai.rl.neat.speciation.core.Species;

import java.util.List;

@FunctionalInterface
public interface SpeciesBreedingStrategy {
    void process(SpeciesBreedingContext breedingContext, List<Species> speciesList);
}
