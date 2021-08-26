package com.dipasquale.ai.rl.neat.speciation.strategy.selection;

import com.dipasquale.ai.rl.neat.context.Context;
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
        Context.SpeciationSupport speciation = context.getParent().speciation();
        List<Organism> organisms = species.removeUnfitToReproduce(speciation);

        organisms.forEach(o -> o.kill(speciation));
    }

    @Override
    public void prepareExtinction(final SpeciesSelectionContext context, final Species species) {
        Context.SpeciationSupport speciation = context.getParent().speciation();
        List<Organism> organisms = species.getOrganisms();

        organisms.forEach(o -> o.kill(speciation));
    }

    @Override
    public void finalizeSelection(final SpeciesSelectionContext context) {
    }
}
