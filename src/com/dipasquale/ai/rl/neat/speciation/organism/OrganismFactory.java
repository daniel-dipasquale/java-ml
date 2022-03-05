package com.dipasquale.ai.rl.neat.speciation.organism;

import com.dipasquale.ai.rl.neat.Context;

@FunctionalInterface
public interface OrganismFactory {
    Organism create(Context context);
}
