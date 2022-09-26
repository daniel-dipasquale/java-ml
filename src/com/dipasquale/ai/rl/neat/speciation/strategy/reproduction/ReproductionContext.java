package com.dipasquale.ai.rl.neat.speciation.strategy.reproduction;

import com.dipasquale.ai.rl.neat.NeatContext;
import com.dipasquale.ai.rl.neat.speciation.organism.Organism;
import com.dipasquale.ai.rl.neat.speciation.organism.OrganismFactory;
import com.dipasquale.data.structure.set.DequeSet;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Queue;

@RequiredArgsConstructor
@Getter
public final class ReproductionContext implements Serializable {
    @Serial
    private static final long serialVersionUID = -368081389919247260L;
    private final NeatContext parent;
    private final SpeciesState speciesState;
    private final DequeSet<Organism> undeterminedOrganisms;
    private final Queue<OrganismFactory> organismsToBirth;
}
