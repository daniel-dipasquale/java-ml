package com.dipasquale.ai.rl.neat.core;

import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.ai.rl.neat.settings.EvaluatorLoadSettings;
import com.dipasquale.ai.rl.neat.speciation.core.Population;
import com.dipasquale.ai.rl.neat.speciation.organism.DefaultOrganismActivator;
import com.dipasquale.ai.rl.neat.speciation.organism.OrganismActivator;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

final class ConcurrentNeatEvaluator implements NeatEvaluator {
    private final Context context;
    private final Population population;
    private final AtomicBoolean populationInitialized;
    private volatile boolean populationFinalized;
    private final OrganismActivator mostFitOrganismActivator;
    private final ReadWriteLock lock;

    ConcurrentNeatEvaluator(final Context context, final OrganismActivator mostFitOrganismActivator, final ReadWriteLock lock) {
        this.context = context;
        this.population = new Population(mostFitOrganismActivator);
        this.mostFitOrganismActivator = mostFitOrganismActivator;
        this.populationInitialized = new AtomicBoolean(false);
        this.populationFinalized = false;
        this.lock = lock;
    }

    ConcurrentNeatEvaluator(final Context context, final ReadWriteLock lock) {
        this(context, new DefaultOrganismActivator(), lock);
    }

    ConcurrentNeatEvaluator(final Context context) {
        this(context, new DefaultOrganismActivator(), new ReentrantReadWriteLock());
    }

    private void ensureIsInitialized() {
        if (!populationInitialized.compareAndSet(false, true)) {
            while (!populationFinalized) {
                Thread.onSpinWait();
            }
        } else {
            population.initialize(context);
            populationFinalized = true;
        }
    }

    @Override
    public int getGeneration() {
        lock.readLock().lock();

        try {
            ensureIsInitialized();

            return population.getGeneration();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public int getSpeciesCount() {
        lock.readLock().lock();

        try {
            ensureIsInitialized();

            return population.getSpeciesCount();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public int getCurrentComplexity() {
        lock.readLock().lock();

        try {
            ensureIsInitialized();

            return mostFitOrganismActivator.getComplexity();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public float getMaximumFitness() {
        lock.readLock().lock();

        try {
            ensureIsInitialized();

            return mostFitOrganismActivator.getFitness();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void evaluateFitness() {
        lock.writeLock().lock();

        try {
            ensureIsInitialized();
            population.updateFitness(context);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void evolve() {
        lock.writeLock().lock();

        try {
            ensureIsInitialized();
            population.evolve(context);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void restart() {
        lock.writeLock().lock();

        try {
            ensureIsInitialized();
            population.restart(context);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public float[] activate(final float[] input) {
        lock.readLock().lock();

        try {
            ensureIsInitialized();

            return mostFitOrganismActivator.activate(context, input);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void save(final OutputStream outputStream)
            throws IOException {
        lock.writeLock().lock();

        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream)) {
            mostFitOrganismActivator.save(objectOutputStream);
            population.save(objectOutputStream);
            context.save(objectOutputStream);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void load(final InputStream inputStream, final EvaluatorLoadSettings settings)
            throws IOException {
        lock.writeLock().lock();

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
                    context.load(objectInputStream, settings.createContext());
                } catch (ClassNotFoundException e) {
                    throw new IOException("unable to load the settings", e);
                }
            }

            if (settings.isMeantToOverrideTopology() || settings.isMeantToOverrideSettings()) {
                population.initialize(context);
                populationInitialized.set(true);
                populationFinalized = true;
            }
        } finally {
            lock.writeLock().unlock();
        }
    }
}
