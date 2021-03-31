package com.dipasquale.ai.rl.neat.population;

import com.dipasquale.ai.rl.neat.genotype.Organism;

public interface OrganismActivator {
    void setOrganism(Organism newOrganism);

    float getFitness();

    float[] activate(float[] inputs);
}
