package com.dipasquale.ai.rl.neat.speciation.strategy.selection;

import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.ai.rl.neat.speciation.core.Species;
import com.dipasquale.ai.rl.neat.speciation.organism.Organism;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@RequiredArgsConstructor
public final class LeastFitRemoverSelectionStrategy implements SelectionStrategy, Serializable {
    @Serial
    private static final long serialVersionUID = -8972350994440640462L;

    private static void killOrganism(final Organism organism, final Context context) {
        organism.kill(context.speciation());
        organism.deregisterNodes(context.connections());
    }

    @Override
    public void prepareSurvival(final SelectionContext context, final Species species) {
        Context.SpeciationSupport speciationSupport = context.getParent().speciation();
        List<Organism> organisms = species.removeUnfitToReproduce(speciationSupport);

        organisms.forEach(organism -> killOrganism(organism, context.getParent()));
        context.getParent().metrics().collectKilled(species, organisms);
    }

    @Override
    public void prepareExtinction(final SelectionContext context, final Species species) {
        List<Organism> organisms = species.getOrganisms();

        organisms.forEach(organism -> killOrganism(organism, context.getParent()));
        context.getParent().metrics().collectKilled(species, organisms);
    }

    @Override
    public void finalizeSelection(final SelectionContext context) {
    }
}
