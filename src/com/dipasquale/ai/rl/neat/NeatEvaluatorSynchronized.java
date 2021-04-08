package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.ai.rl.neat.speciation.OrganismActivator;
import com.dipasquale.ai.rl.neat.speciation.OrganismActivatorSynchronized;
import com.dipasquale.ai.rl.neat.speciation.Population;

final class NeatEvaluatorSynchronized implements NeatEvaluator {
    private final Context context;
    private final Population population;
    private final OrganismActivator mostFitOrganismActivator;

    NeatEvaluatorSynchronized(final Context context) {
        OrganismActivator mostFitOrganismActivator = new OrganismActivatorSynchronized();

        this.context = context;
        this.population = new Population(context, mostFitOrganismActivator);
        this.mostFitOrganismActivator = mostFitOrganismActivator;
    }

    @Override
    public int getGeneration() {
        synchronized (population) {
            return population.getGeneration();
        }
    }

    @Override
    public int getSpeciesCount() {
        synchronized (population) {
            return population.getSpeciesCount();
        }
    }

    @Override
    public void evaluateFitness() {
        synchronized (population) {
            population.updateFitness();
        }
    }

    @Override
    public void evolve() {
        synchronized (population) {
            population.evolve();
        }
    }

    @Override
    public void restart() {
        synchronized (population) {
            population.restart();
        }
    }

    @Override
    public float getMaximumFitness() {
        return mostFitOrganismActivator.getFitness();
    }

    @Override
    public float[] activate(final float[] input) {
        return mostFitOrganismActivator.activate(context, input);
    }
}
