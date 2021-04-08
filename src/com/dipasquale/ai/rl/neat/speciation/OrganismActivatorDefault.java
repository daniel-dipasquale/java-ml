package com.dipasquale.ai.rl.neat.speciation;

import com.dipasquale.ai.rl.neat.context.Context;
import lombok.Getter;

import java.io.Serial;

public final class OrganismActivatorDefault implements OrganismActivator {
    @Serial
    private static final long serialVersionUID = -960213656782630391L;
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
