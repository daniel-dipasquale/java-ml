package com.dipasquale.ai.rl.neat.population;

import com.dipasquale.ai.rl.neat.genotype.Organism;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Setter;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Setter(AccessLevel.PACKAGE)
final class OrganismActivator {
    private Organism organism;

    public float[] activate(final float[] inputs) {
        return organism.activate(inputs);
    }
}
