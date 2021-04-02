package com.dipasquale.ai.rl.neat.genotype;

import com.dipasquale.common.ObjectFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class OrganismFactoryMutation implements ObjectFactory<Organism> {
    private final Organism originalOrganism;

    @Override
    public Organism create() {
        Organism organism = originalOrganism.createCopy();

        organism.mutate();
        organism.freeze();

        return organism;
    }
}
