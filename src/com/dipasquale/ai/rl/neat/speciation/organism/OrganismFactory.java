package com.dipasquale.ai.rl.neat.speciation.organism;

import com.dipasquale.ai.rl.neat.NeatContext;

@FunctionalInterface
public interface OrganismFactory {
    Organism create(NeatContext context);
}
