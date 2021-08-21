package com.dipasquale.ai.rl.neat.speciation.strategy.selection;

import com.dipasquale.ai.rl.neat.speciation.core.Species;

public interface SpeciesSelectionStrategy {
    void prepareSurvival(SpeciesSelectionContext context, Species species);

    void prepareExtinction(SpeciesSelectionContext context, Species species);

    void finalizeSelection(SpeciesSelectionContext context);
}
