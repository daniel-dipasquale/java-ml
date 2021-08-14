package com.dipasquale.ai.rl.neat.speciation.organism;

import com.dipasquale.ai.rl.neat.context.Context;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
public final class MatingOrganismFactory implements OrganismFactory, Serializable {
    @Serial
    private static final long serialVersionUID = -7040095868129331447L;
    private final Organism parentOrganism1;
    private final Organism parentOrganism2;
    private final boolean shouldMutate;

    @Override
    public Organism create(final Context context) {
        Organism organism = parentOrganism1.mate(context, parentOrganism2);

        organism.prepare(context);

        if (shouldMutate) {
            organism.mutate(context);
        }

        organism.freeze();

        return organism;
    }
}

