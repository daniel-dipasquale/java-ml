package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.ai.rl.neat.population.Population;

final class NeatCollectiveSynchronized implements NeatCollective {
    private final Population population;

    public NeatCollectiveSynchronized(final Context context) {
        this.population = new Population(context);
    }

    @Override
    public int generation() {
        synchronized (population) {
            return population.getGeneration();
        }
    }

    @Override
    public int species() {
        synchronized (population) {
            return population.getSpeciesCount();
        }
    }

    @Override
    public void testFitness() {
        synchronized (population) {
            population.testFitness();
        }
    }

    @Override
    public void evolve() {
        synchronized (population) {
            population.evolve();
        }
    }

    @Override
    public float[] activate(final float[] input) {
        synchronized (population) {
            return population.activate(input);
        }
    }
}
