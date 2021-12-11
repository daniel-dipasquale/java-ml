package com.dipasquale.ai.rl.neat.speciation.strategy.reproduction;

import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.ai.rl.neat.speciation.organism.Organism;
import com.dipasquale.ai.rl.neat.speciation.organism.OrganismFactory;
import com.dipasquale.data.structure.set.DequeSet;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Queue;

@Getter
public final class ReproductionContext implements Serializable {
    @Serial
    private static final long serialVersionUID = -368081389919247260L;
    private final Context parent;
    private final SpeciesState speciesState;
    private final DequeSet<Organism> organismsWithoutSpecies;
    private final Queue<OrganismFactory> organismsToBirth;

    public ReproductionContext(final Context context, final SpeciesState speciesState, final DequeSet<Organism> organismsWithoutSpecies, final Queue<OrganismFactory> organismsToBirth) {
        this.parent = context;
        this.speciesState = speciesState;
        this.organismsWithoutSpecies = organismsWithoutSpecies;
        this.organismsToBirth = organismsToBirth;
    }
}
