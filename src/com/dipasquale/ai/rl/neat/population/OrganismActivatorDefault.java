package com.dipasquale.ai.rl.neat.population;

import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.ai.rl.neat.genotype.Organism;
import lombok.Getter;

public final class OrganismActivatorDefault implements OrganismActivator {
    private Organism organism = null;
    @Getter
    private float fitness = 0f;

    @Override
    public void setOrganism(final Organism newOrganism) {
        organism = newOrganism;
        fitness = newOrganism.getFitness();
    }

    @Override
    public float[] activate(final Context context, final float[] inputs) {
        return organism.activate(inputs);
    }
}
