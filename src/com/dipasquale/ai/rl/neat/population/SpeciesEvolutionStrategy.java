package com.dipasquale.ai.rl.neat.population;

import com.dipasquale.ai.rl.neat.genotype.Species;

import java.io.Serializable;

interface SpeciesEvolutionStrategy extends Serializable {
    void process(SpeciesEvolutionContext evolutionContext, Species species, boolean speciesSurvives);

    void postProcess(SpeciesEvolutionContext evolutionContext);
}
