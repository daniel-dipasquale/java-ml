/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.ai.rl.neat.core;

import com.dipasquale.ai.common.fitness.FitnessFunction;
import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.ai.rl.neat.genotype.Genome;
import com.dipasquale.ai.rl.neat.settings.EvaluatorStateSettings;
import com.dipasquale.ai.rl.neat.speciation.core.Population;
import com.dipasquale.ai.rl.neat.speciation.organism.OrganismActivator;
import com.dipasquale.ai.rl.neat.speciation.organism.SynchronizedOrganismActivator;
import com.dipasquale.threading.event.loop.IterableEventLoop;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

final class SynchronizedNeatEvaluator implements NeatEvaluator {
    private final Context context;
    private final Population population;
    private final OrganismActivator mostFitOrganismActivator;
    private boolean initialized;

    SynchronizedNeatEvaluator(final Context context) {
        OrganismActivator mostFitOrganismActivator = new SynchronizedOrganismActivator();

        this.context = context;
        this.population = new Population(mostFitOrganismActivator);
        this.mostFitOrganismActivator = mostFitOrganismActivator;
        this.initialized = false;
    }

    private void ensureIsInitialized() {
        if (!initialized) {
            population.initialize(context);
            initialized = true;
        }
    }

    @Override
    public int getGeneration() {
        synchronized (population) {
            ensureIsInitialized();

            return population.getGeneration();
        }
    }

    @Override
    public int getSpeciesCount() {
        synchronized (population) {
            ensureIsInitialized();

            return population.getSpeciesCount();
        }
    }

    private void ensureIsInitializedSynchronized() {
        if (!initialized) {
            synchronized (population) {
                ensureIsInitialized();
            }
        }
    }

    @Override
    public float getMaximumFitness() {
        ensureIsInitializedSynchronized();

        return mostFitOrganismActivator.getFitness();
    }

    @Override
    public void evaluateFitness() {
        synchronized (population) {
            ensureIsInitialized();
            population.updateFitness(context);
        }
    }

    @Override
    public void evolve() {
        synchronized (population) {
            ensureIsInitialized();
            population.evolve(context);
        }
    }

    @Override
    public void restart() {
        synchronized (population) {
            ensureIsInitialized();
            population.restart(context);
        }
    }

    @Override
    public float[] activate(final float[] input) {
        ensureIsInitializedSynchronized();

        return mostFitOrganismActivator.activate(context, input);
    }

    @Override
    public void save(final OutputStream outputStream)
            throws IOException {
        synchronized (population) {
            try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream)) {
                mostFitOrganismActivator.save(objectOutputStream);
                population.save(objectOutputStream);
                context.save(objectOutputStream);
            }
        }
    }

    private static Context.StateOverrideSupport createStateOverride(final EvaluatorStateSettings settings) {
        return new Context.StateOverrideSupport() {
            @Override
            public FitnessFunction<Genome> environment() {
                return settings.getEnvironment();
            }

            @Override
            public IterableEventLoop eventLoop() {
                return settings.getEventLoop();
            }
        };
    }

    @Override
    public void load(final InputStream inputStream, final EvaluatorStateSettings settings)
            throws IOException {
        synchronized (population) {
            try (ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)) {
                if (settings.isMeantToOverrideTopology()) {
                    try {
                        mostFitOrganismActivator.load(objectInputStream);
                        population.load(objectInputStream, mostFitOrganismActivator);
                    } catch (ClassNotFoundException e) {
                        throw new IOException("unable to load the topology", e);
                    }
                }

                if (settings.isMeantToOverrideSettings()) {
                    try {
                        Context.StateOverrideSupport override = createStateOverride(settings);

                        context.load(objectInputStream, override);
                    } catch (ClassNotFoundException e) {
                        throw new IOException("unable to load the settings", e);
                    }
                }

                if (settings.isMeantToOverrideTopology() || settings.isMeantToOverrideSettings()) {
                    population.initialize(context);
                    initialized = true;
                }
            }
        }
    }
}
