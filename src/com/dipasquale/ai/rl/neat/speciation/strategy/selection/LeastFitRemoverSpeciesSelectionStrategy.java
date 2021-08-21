package com.dipasquale.ai.rl.neat.speciation.strategy.selection;

import com.dipasquale.ai.rl.neat.speciation.core.Species;
import com.dipasquale.ai.rl.neat.speciation.organism.Organism;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@RequiredArgsConstructor
public final class LeastFitRemoverSpeciesSelectionStrategy implements SpeciesSelectionStrategy, Serializable {
    @Serial
    private static final long serialVersionUID = -8972350994440640462L;

    @Override
    public void prepareSurvival(final SpeciesSelectionContext context, final Species species) {
        List<Organism> organisms = species.removeUnfitToReproduce(context.getParent().speciation());

        organisms.forEach(Organism::kill);
    }

    @Override
    public void prepareExtinction(final SpeciesSelectionContext context, final Species species) {
        List<Organism> organisms = species.getOrganisms();

        organisms.forEach(Organism::kill);
    }

    @Override
    public void finalizeSelection(final SpeciesSelectionContext context) {
    }
}
