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
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

final class ConcurrentNeatEvaluator implements NeatEvaluator {
    private final Context context;
    private final OrganismActivator championOrganismActivator;
    private final Population population;
    private final ReadWriteLock lock;

    ConcurrentNeatEvaluator(final Context context, final OrganismActivator championOrganismActivator, final ReadWriteLock lock) {
        this.context = context;
        this.championOrganismActivator = championOrganismActivator;
        this.population = createPopulation(context, championOrganismActivator);
        this.lock = lock;
    }

    ConcurrentNeatEvaluator(final Context context, final ReadWriteLock lock) {
        this(context, new DefaultOrganismActivator(), lock);
    }

    ConcurrentNeatEvaluator(final Context context) {
        this(context, new DefaultOrganismActivator(), new ReentrantReadWriteLock());
    }

    private static Population createPopulation(final Context context, final OrganismActivator championOrganismActivator) {
        Population population = new Population();

        population.initialize(context, championOrganismActivator);

        return population;
    }

    @Override
    public int getGeneration() {
        lock.readLock().lock();

        try {
            return population.getGeneration();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public int getSpeciesCount() {
        lock.readLock().lock();

        try {
            return population.getSpeciesCount();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public int getCurrentConnections() {
        lock.readLock().lock();

        try {
            return championOrganismActivator.getConnections();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public float getMaximumFitness() {
        lock.readLock().lock();

        try {
            return championOrganismActivator.getFitness();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void evaluateFitness() {
        lock.writeLock().lock();

        try {
            population.updateFitness(context);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void evolve() {
        lock.writeLock().lock();

        try {
            population.evolve(context, championOrganismActivator);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void restart() {
        lock.writeLock().lock();

        try {
            population.restart(context, championOrganismActivator);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public float[] activate(final float[] input) {
        lock.readLock().lock();

        try {
            return championOrganismActivator.activate(input);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void save(final OutputStream outputStream)
            throws IOException {
        lock.writeLock().lock();

        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream)) {
            context.save(objectOutputStream);
            championOrganismActivator.save(objectOutputStream);
            population.save(objectOutputStream);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void load(final InputStream inputStream, final EvaluatorLoadSettings settings)
            throws IOException {
        lock.writeLock().lock();

        try (ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)) {
            try {
                context.load(objectInputStream, settings.createContext());
            } catch (ClassNotFoundException e) {
                throw new IOException("unable to load the settings", e);
            }

            try {
                championOrganismActivator.load(objectInputStream);
                population.load(objectInputStream);
            } catch (ClassNotFoundException e) {
                throw new IOException("unable to load the topology", e);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }
}
