package com.dipasquale.ai.rl.neat.speciation;

import java.io.Serial;

final class SpeciesEvolutionStrategyTotalSharedFitness implements SpeciesEvolutionStrategy {
    @Serial
    private static final long serialVersionUID = -188364327859090092L;

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
