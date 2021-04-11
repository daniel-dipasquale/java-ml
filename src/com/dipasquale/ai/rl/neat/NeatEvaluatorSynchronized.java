package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.ai.rl.neat.speciation.OrganismActivator;
import com.dipasquale.ai.rl.neat.speciation.OrganismActivatorSynchronized;
import com.dipasquale.ai.rl.neat.speciation.Population;
import com.dipasquale.threading.event.loop.EventLoopIterable;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

final class NeatEvaluatorSynchronized implements NeatEvaluator {
    private final Context context;
    private final Population population;
    private final OrganismActivator mostFitOrganismActivator;
    private boolean initialized;

    NeatEvaluatorSynchronized(final Context context) {
        OrganismActivator mostFitOrganismActivator = new OrganismActivatorSynchronized();

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
                context.state().save(objectOutputStream);
            }
        }
    }

    private static Context.StateOverrideSupport createStateOverride(final SettingsEvaluatorState settings) {
        return new Context.StateOverrideSupport() {
            @Override
            public NeatEnvironment environment() {
                return settings.getEnvironment();
            }

            @Override
            public EventLoopIterable eventLoop() {
                return settings.getEventLoop();
            }
        };
    }

    @Override
    public void load(final InputStream inputStream, final SettingsEvaluatorState settings)
            throws IOException {
        synchronized (population) {
            try (ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)) {
                if (settings.isMeantToLoadTopology()) {
                    try {
                        mostFitOrganismActivator.load(objectInputStream);
                        population.load(objectInputStream, mostFitOrganismActivator);
                    } catch (ClassNotFoundException e) {
                        throw new IOException("unable to load the topology", e);
                    }
                }

                if (settings.isMeantToLoadSettings()) {
                    try {
                        Context.StateOverrideSupport override = createStateOverride(settings);

                        context.state().load(objectInputStream, override);
                    } catch (ClassNotFoundException e) {
                        throw new IOException("unable to load the settings", e);
                    }
                }

                if (settings.isMeantToLoadTopology() || settings.isMeantToLoadSettings()) {
                    population.initialize(context);
                    initialized = true;
                }
            }
        }
    }
}
