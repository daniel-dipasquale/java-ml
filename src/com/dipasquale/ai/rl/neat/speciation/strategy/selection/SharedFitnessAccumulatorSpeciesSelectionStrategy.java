package com.dipasquale.ai.rl.neat.speciation.strategy.selection;

import com.dipasquale.ai.rl.neat.speciation.core.Species;

import java.io.Serial;
import java.io.Serializable;

public final class SharedFitnessAccumulatorSpeciesSelectionStrategy implements SpeciesSelectionStrategy, Serializable {
    @Serial
    private static final long serialVersionUID = 3821903702479787617L;

    @Override
    public void prepareSurvival(final SpeciesSelectionContext context, final Species species) {
        context.addTotalSharedFitness(species.getSharedFitness());
    }

    @Override
    public void prepareExtinction(final SpeciesSelectionContext context, final Species species) {
    }

    @Override
    public void finalizeSelection(final SpeciesSelectionContext context) {
    }
}
