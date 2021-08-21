package com.dipasquale.ai.rl.neat.speciation.strategy.selection;

import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.ai.rl.neat.speciation.organism.Organism;
import com.dipasquale.ai.rl.neat.speciation.organism.OrganismActivator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
public final class SpeciesSelectionContext {
    private final Context parent;
    private final OrganismActivator championOrganismActivator;
    private float totalSharedFitness = 0f;
    @Setter
    private Organism championOrganism = null;

    public void addTotalSharedFitness(final float delta) {
        totalSharedFitness += delta;
    }
}
