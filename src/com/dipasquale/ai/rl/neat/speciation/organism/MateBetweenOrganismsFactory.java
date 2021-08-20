package com.dipasquale.ai.rl.neat.speciation.organism;

import com.dipasquale.ai.rl.neat.context.Context;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
public final class MateBetweenOrganismsFactory implements OrganismFactory, Serializable {
    @Serial
    private static final long serialVersionUID = -7040095868129331447L;
    private final Organism parentOrganism1;
    private final Organism parentOrganism2;
    private final boolean shouldMutate;

    @Override
    public Organism create(final Context context) {
        Organism newOrganism = parentOrganism1.mate(context, parentOrganism2);

        newOrganism.initialize(context);

        if (shouldMutate) {
            newOrganism.mutate(context);
        }

        newOrganism.freeze();

        return newOrganism;
    }
}

