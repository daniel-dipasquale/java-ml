package com.dipasquale.ai.rl.neat.core;

import com.dipasquale.ai.rl.neat.genotype.Genome;
import com.dipasquale.ai.rl.neat.phenotype.NeuronMemory;
import com.dipasquale.ai.rl.neat.speciation.core.Population;
import com.dipasquale.ai.rl.neat.speciation.metric.MetricsViewer;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

final class ConcurrentNeatEvaluator implements NeatEvaluator {
    private final ReadWriteLock lock;
    private final Context context;
    private final Population population;
    private final ConcurrentNeatState state;

    private static Population createPopulation(final Context context) {
        Population population = new Population();

        population.initialize(context);

        return population;
    }

    ConcurrentNeatEvaluator(final Context context, final ReadWriteLock lock) {
        this.lock = lock;
        this.context = context;
        this.population = createPopulation(context);
        this.state = new ConcurrentNeatState();
    }

    ConcurrentNeatEvaluator(final Context context) {
        this(context, new ReentrantReadWriteLock());
    }

    @Override
    public NeatState getState() {
        return state;
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
            population.evolve(context);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void restart() {
        lock.writeLock().lock();

        try {
            population.restart(context);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public NeuronMemory createMemory() {
        lock.readLock().lock();

        try {
            return population.getChampionOrganismActivator().createMemory();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public float[] activate(final float[] input, final NeuronMemory neuronMemory) {
        lock.readLock().lock();

        try {
            return population.getChampionOrganismActivator().activate(input, neuronMemory);
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
                throw new IOException("unable to load the context", e);
            }

            try {
                population.load(objectInputStream);
            } catch (ClassNotFoundException e) {
                throw new IOException("unable to load the population", e);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private final class ConcurrentNeatState implements NeatState {
        @Override
        public int getIteration() {
            lock.readLock().lock();

            try {
                return population.getIteration();
            } finally {
                lock.readLock().unlock();
            }
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
        public Genome getChampionGenome() {
            lock.readLock().lock();

            try {
                return population.getChampionOrganismActivator().getGenome();
            } finally {
                lock.readLock().unlock();
            }
        }

        @Override
        public float getMaximumFitness() {
            lock.readLock().lock();

            try {
                return population.getChampionOrganismActivator().getFitness();
            } finally {
                lock.readLock().unlock();
            }
        }

        @Override
        public MetricsViewer createMetricsViewer() {
            lock.readLock().lock();

            try {
                return context.metrics().createMetricsViewer();
            } finally {
                lock.readLock().unlock();
            }
        }
    }
}
