package com.dipasquale.ai.rl.neat.genotype;

import com.dipasquale.common.ObjectFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class OrganismFactoryMating implements ObjectFactory<Organism> {
    private final Organism parentOrganism1;
    private final Organism parentOrganism2;
    private final boolean shouldMutate;

    @Override
    public Organism create() {
        Organism organism = parentOrganism1.mate(parentOrganism2);

        if (shouldMutate) {
            organism.mutate();
        }

        organism.freeze();

        return organism;
    }
}

