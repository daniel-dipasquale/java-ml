package com.dipasquale.ai.rl.neat.speciation;

import com.dipasquale.ai.rl.neat.context.Context;

@FunctionalInterface
public interface OrganismFactory {
    Organism create(Context context);
}
