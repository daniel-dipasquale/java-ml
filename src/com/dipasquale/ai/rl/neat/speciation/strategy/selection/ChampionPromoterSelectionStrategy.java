package com.dipasquale.ai.rl.neat.speciation.strategy.selection;

import com.dipasquale.ai.rl.neat.speciation.Species;
import com.dipasquale.ai.rl.neat.speciation.organism.Organism;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ChampionPromoterSelectionStrategy implements SelectionStrategy {
    private static final ChampionPromoterSelectionStrategy INSTANCE = new ChampionPromoterSelectionStrategy();

    public static ChampionPromoterSelectionStrategy getInstance() {
        return INSTANCE;
    }

    @Override
    public void prepareSurvival(final SelectionContext context, final Species species) {
        Organism championOrganism = species.getChampionOrganism();
        Organism previousChampionOrganism = context.getChampionOrganism();

        if (previousChampionOrganism == null || previousChampionOrganism.compareTo(championOrganism) < 0) {
            context.setChampionOrganism(championOrganism);
        }
    }

    @Override
    public void prepareExtinction(final SelectionContext context, final Species species) {
    }

    @Override
    public void finalizeSelection(final SelectionContext context) {
        context.initializeChampionOrganism();
    }
}
