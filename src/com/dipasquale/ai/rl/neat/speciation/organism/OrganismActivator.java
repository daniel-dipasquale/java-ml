/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.ai.rl.neat.speciation.organism;

import com.dipasquale.ai.rl.neat.context.Context;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public interface OrganismActivator {
    void setOrganism(Organism newOrganism);

    float getFitness();

    float[] activate(Context context, float[] inputs);

    void save(ObjectOutputStream outputStream) throws IOException;

    void load(ObjectInputStream inputStream) throws IOException, ClassNotFoundException;
}
