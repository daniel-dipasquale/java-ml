package com.dipasquale.ai.rl.neat.speciation.organism;

import com.dipasquale.ai.rl.neat.context.Context;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public interface OrganismActivator {
    void initialize(Organism organism, Context.ActivationSupport neuralNetwork);

    int getComplexity();

    float getFitness();

    float[] activate(float[] inputs);

    void save(ObjectOutputStream outputStream) throws IOException;

    void load(ObjectInputStream inputStream) throws IOException, ClassNotFoundException;
}
