package com.dipasquale.ai.rl.neat.population;

interface SpeciesEvolutionStrategy {
    void process(SpeciesEvolutionContext evolutionContext, Species species, boolean speciesSurvives);

    void postProcess(SpeciesEvolutionContext evolutionContext);
}
