package com.dipasquale.ai.rl.neat.speciation;

interface SpeciesEvolutionStrategy {
    void process(SpeciesEvolutionContext evolutionContext, Species species, boolean speciesSurvives);

    void postProcess(SpeciesEvolutionContext evolutionContext);
}
