package com.dipasquale.ai.rl.neat.phenotype;

public interface GenomeActivator {
    String getId();

    int getGeneration();

    int getHiddenNodes();

    int getConnections();

    float[] activate(float[] input);
}
