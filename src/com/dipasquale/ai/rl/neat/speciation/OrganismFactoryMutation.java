package com.dipasquale.ai.rl.neat.speciation;

import com.dipasquale.ai.rl.neat.context.Context;
import lombok.RequiredArgsConstructor;

import java.io.Serial;

@RequiredArgsConstructor
public final class OrganismFactoryMutation implements OrganismFactory {
    @Serial
    private static final long serialVersionUID = 3542098615345722645L;
    private final Organism originalOrganism;

    @Override
    public Organism create(final Context context) {
        Organism organism = originalOrganism.createCopy();

        organism.initialize(context);
        organism.mutate(context);
        organism.freeze();

        return organism;
    }
}
