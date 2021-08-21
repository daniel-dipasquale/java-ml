package com.dipasquale.ai.rl.neat.speciation.strategy.reproduction;

import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.ai.rl.neat.speciation.core.Species;
import com.dipasquale.ai.rl.neat.speciation.organism.Organism;
import com.dipasquale.ai.rl.neat.speciation.organism.OrganismFactory;
import com.dipasquale.ai.rl.neat.speciation.strategy.selection.SpeciesSelectionContext;
import com.dipasquale.data.structure.set.DequeSet;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Queue;

@Getter
public final class SpeciesReproductionContext implements Serializable {
    @Serial
    private static final long serialVersionUID = -368081389919247260L;
    private final Context parent;
    private final List<Species> rankedSpecies;
    private final DequeSet<Organism> organismsWithoutSpecies;
    private final Queue<OrganismFactory> organismsToBirth;
    private final float totalSharedFitness;

    public SpeciesReproductionContext(final SpeciesSelectionContext context, final List<Species> rankedSpecies, final DequeSet<Organism> organismsWithoutSpecies, final Queue<OrganismFactory> organismsToBirth) {
        this.parent = context.getParent();
        this.rankedSpecies = rankedSpecies;
        this.organismsWithoutSpecies = organismsWithoutSpecies;
        this.organismsToBirth = organismsToBirth;
        this.totalSharedFitness = context.getTotalSharedFitness();
    }
}
