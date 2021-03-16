package com.dipasquale.ai.rl.neat;

interface SpeciesEvolutionStrategy {
    void process(SpeciesEvolutionContext context, Species species);

    void postProcess(SpeciesEvolutionContext context);
}
