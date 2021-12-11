package com.dipasquale.ai.rl.neat.speciation.strategy.selection;

import com.dipasquale.ai.rl.neat.speciation.core.Species;

public interface SelectionStrategy {
    void prepareSurvival(SelectionContext context, Species species);

    void prepareExtinction(SelectionContext context, Species species);

    void finalizeSelection(SelectionContext context);
}
