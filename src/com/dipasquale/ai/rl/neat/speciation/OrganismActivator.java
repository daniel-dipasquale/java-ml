package com.dipasquale.ai.rl.neat.speciation;

import com.dipasquale.ai.rl.neat.context.Context;

import java.io.Serializable;

public interface OrganismActivator extends Serializable {
    void setOrganism(Organism newOrganism);

    float getFitness();

    float[] activate(Context context, float[] inputs);
}
