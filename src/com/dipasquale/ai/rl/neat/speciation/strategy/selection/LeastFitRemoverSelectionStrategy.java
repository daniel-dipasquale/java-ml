package com.dipasquale.ai.rl.neat.speciation.strategy.selection;

import com.dipasquale.ai.rl.neat.NeatContext;
import com.dipasquale.ai.rl.neat.speciation.Species;
import com.dipasquale.ai.rl.neat.speciation.organism.Organism;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class LeastFitRemoverSelectionStrategy implements SelectionStrategy {
    private static final LeastFitRemoverSelectionStrategy INSTANCE = new LeastFitRemoverSelectionStrategy();

    public static LeastFitRemoverSelectionStrategy getInstance() {
        return INSTANCE;
    }

    private static void killOrganism(final Organism organism, final NeatContext context) {
        organism.kill(context.getSpeciation());
        organism.deregisterNodeGenes(context.getNodeGenes());
    }

    @Override
    public void prepareSurvival(final SelectionContext context, final Species species) {
        NeatContext parentContext = context.getParent();
        NeatContext.SpeciationSupport speciationSupport = parentContext.getSpeciation();
        List<Organism> organisms = species.removeUnfitToReproduce(speciationSupport);

        organisms.forEach(organism -> killOrganism(organism, parentContext));
        context.getParent().getMetrics().collectKilled(species, organisms);
    }

    @Override
    public void prepareExtinction(final SelectionContext context, final Species species) {
        List<Organism> organisms = species.getOrganisms();
        NeatContext parentContext = context.getParent();

        organisms.forEach(organism -> killOrganism(organism, parentContext));
        parentContext.getMetrics().collectKilled(species, organisms);
    }

    @Override
    public void finalizeSelection(final SelectionContext context) {
    }
}
