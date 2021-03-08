package com.dipasquale.ai.rl.neat;

public interface NeatCollective {
    int generation();

    int species();

    void testFitness();

    void evolve();
}
