package com.dipasquale.ai.rl.neat.speciation.strategy.selection;

import com.dipasquale.ai.rl.neat.speciation.Species;
import com.dipasquale.ai.rl.neat.speciation.organism.Organism;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
public final class ChampionPromoterSelectionStrategy implements SelectionStrategy, Serializable {
    @Serial
    private static final long serialVersionUID = 6142238994253254883L;

    @Override
    public void prepareSurvival(final SelectionContext context, final Species species) {
        Organism championOrganism = species.getChampionOrganism();

        if (context.getChampionOrganism() == null || context.getChampionOrganism().compareTo(championOrganism) < 0) {
            context.setChampionOrganism(championOrganism);
        }
    }

    @Override
    public void prepareExtinction(final SelectionContext context, final Species species) {
    }

    @Override
    public void finalizeSelection(final SelectionContext context) {
        context.setChampionOrganism();
    }
}
