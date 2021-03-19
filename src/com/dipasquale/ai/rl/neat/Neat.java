package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.rl.neat.population.Population;

public interface Neat {
    static NeatCollective createCollective(final SettingsCollective settings) {
        Population population = new Population(settings.createContext());

        return new NeatCollective() {
            @Override
            public int generation() {
                synchronized (population) {
                    return population.getGeneration();
                }
            }

            @Override
            public int species() {
                synchronized (population) {
                    return population.species();
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
            public NeatCollectiveClient getMostFit() {
                synchronized (population) {
                    return population.getMostFitCollectiveStrategy();
                }
            }
        };
    }
}
