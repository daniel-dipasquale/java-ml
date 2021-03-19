package com.dipasquale.ai.rl.neat.population;

import com.dipasquale.ai.rl.neat.species.Species;

interface SpeciesEvolutionStrategy {
    void process(SpeciesEvolutionContext context, Species species);

    void postProcess(SpeciesEvolutionContext context);
}
