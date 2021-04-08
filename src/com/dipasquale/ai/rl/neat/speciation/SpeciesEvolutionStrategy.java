package com.dipasquale.ai.rl.neat.speciation;

import java.io.Serializable;

interface SpeciesEvolutionStrategy extends Serializable {
    void process(SpeciesEvolutionContext evolutionContext, Species species, boolean speciesSurvives);

    void postProcess(SpeciesEvolutionContext evolutionContext);
}
