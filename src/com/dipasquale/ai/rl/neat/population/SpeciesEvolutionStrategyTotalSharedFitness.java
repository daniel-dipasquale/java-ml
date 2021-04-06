package com.dipasquale.ai.rl.neat.population;

final class SpeciesEvolutionStrategyTotalSharedFitness implements SpeciesEvolutionStrategy {
    @Override
    public void process(final SpeciesEvolutionContext evolutionContext, final Species species, boolean speciesSurvives) {
        if (!speciesSurvives) {
            return;
        }

        evolutionContext.addTotalSharedFitness(species.getSharedFitness());
    }

    @Override
    public void postProcess(final SpeciesEvolutionContext evolutionContext) {
    }
}
