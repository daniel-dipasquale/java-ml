package com.dipasquale.ai.rl.neat.population;

import com.dipasquale.ai.rl.neat.species.Species;

final class SpeciesEvolutionStrategyTotalSharedFitness implements SpeciesEvolutionStrategy {
    @Override
    public void process(final SpeciesEvolutionContext context, final Species species, boolean speciesSurvives) {
        if (!speciesSurvives) {
            return;
        }

        context.addTotalSharedFitness(species.getSharedFitness());
    }

    @Override
    public void postProcess(final SpeciesEvolutionContext context) {
    }
}
