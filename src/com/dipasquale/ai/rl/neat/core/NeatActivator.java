package com.dipasquale.ai.rl.neat.core;

public interface NeatActivator {
    int getGeneration();

    int getSpeciesCount();

    int getConnections();

    float getFitness();

    float[] activate(float[] inputs);
}
