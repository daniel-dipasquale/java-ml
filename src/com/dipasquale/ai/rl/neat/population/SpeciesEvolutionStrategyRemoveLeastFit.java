package com.dipasquale.ai.rl.neat.population;

import com.dipasquale.ai.rl.neat.species.Species;

final class SpeciesEvolutionStrategyRemoveLeastFit implements SpeciesEvolutionStrategy {
    @Override
    public void process(final SpeciesEvolutionContext context, final Species species) {
        if (!species.shouldSurvive()) {
            return;
        }

        species.removeUnfitToReproduce();
    }

    @Override
    public void postProcess(final SpeciesEvolutionContext context) {
    }
}
