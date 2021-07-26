package com.dipasquale.ai.rl.neat.speciation;

import java.io.Serial;
import java.io.Serializable;

final class SpeciesEvolutionStrategyTotalSharedFitness implements SpeciesEvolutionStrategy, Serializable {
    @Serial
    private static final long serialVersionUID = 3821903702479787617L;

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
