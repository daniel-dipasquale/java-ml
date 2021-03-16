package com.dipasquale.ai.rl.neat;

final class SpeciesEvolutionStrategyTotalSharedFitness implements SpeciesEvolutionStrategy {
    @Override
    public void process(final SpeciesEvolutionContext context, final Species species) {
        if (!species.shouldSurvive()) {
            return;
        }

        context.addTotalSharedFitness(species.getSharedFitness());
    }

    @Override
    public void postProcess(final SpeciesEvolutionContext context) {
    }
}
