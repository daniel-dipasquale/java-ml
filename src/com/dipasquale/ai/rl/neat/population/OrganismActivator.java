package com.dipasquale.ai.rl.neat.population;

import com.dipasquale.ai.rl.neat.genotype.Organism;
import lombok.Getter;

final class OrganismActivator {
    private Organism organism;
    @Getter
    private float fitness;

    OrganismActivator(final Organism organism) {
        this.organism = organism;
        this.fitness = 0f;
    }

    public void setOrganism(final Organism organism) {
        this.organism = organism;
        this.fitness = organism.getFitness();
    }

    public float[] activate(final float[] inputs) {
        return organism.activate(inputs);
    }
}
