package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.rl.neat.genotype.Genome;
import com.dipasquale.ai.rl.neat.phenotype.NeatNeuronMemory;
import com.dipasquale.ai.rl.neat.speciation.Population;
import com.dipasquale.ai.rl.neat.speciation.metric.MetricsViewer;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

final class ConcurrentNeatEvaluator implements NeatEvaluator {
    private final ConcurrentNeatState state;
    private final ReadWriteLock lock;
    private final Context context;
    private final Population population;

    private ConcurrentNeatEvaluator(final ReadWriteLock lock, final Context context, final Population population) {
        this.state = new ConcurrentNeatState();
        this.lock = lock;
        this.context = context;
        this.population = population;
    }

    private static Population createPopulation(final Context context) {
        Population population = new Population();

        population.initialize(context);

        return population;
    }

    ConcurrentNeatEvaluator(final ReadWriteLock lock, final Context context) {
        this(lock, context, createPopulation(context));
    }

    ConcurrentNeatEvaluator(final Context context) {
        this(new ReentrantReadWriteLock(), context);
    }

    private ConcurrentNeatEvaluator(final Context context, final Population population) {
        this(new ReentrantReadWriteLock(), context, population);
    }

    @Override
    public NeatState getState() {
        return state;
    }

    private void evaluateFitness(final Lock lock) {
        lock.lock();

        try {
            population.updateFitness(context);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void evaluateFitness() {
        evaluateFitness(lock.writeLock());
    }

    private void evolve(final Lock lock) {
        lock.lock();

        try {
            population.evolve(context);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void evolve() {
        evolve(lock.writeLock());
    }

    private void restart(final Lock lock) {
        lock.lock();

        try {
            population.restart(context);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void restart() {
        restart(lock.writeLock());
    }

    private NeatNeuronMemory createMemory(final Lock lock) {
        lock.lock();

        try {
            return population.getChampionOrganismActivator().createMemory();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public NeatNeuronMemory createMemory() {
        return createMemory(lock.readLock());
    }

    private float[] activate(final Lock lock, final float[] input, final NeatNeuronMemory neuronMemory) {
        lock.lock();

        try {
            return population.getChampionOrganismActivator().activate(input, neuronMemory);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public float[] activate(final float[] input, final NeatNeuronMemory neuronMemory) {
        return activate(lock.readLock(), input, neuronMemory);
    }

    private void save(final Lock lock, final OutputStream outputStream)
            throws IOException {
        lock.lock();

        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream)) {
            context.save(objectOutputStream);
            population.save(objectOutputStream);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void save(final OutputStream outputStream)
            throws IOException {
        save(lock.writeLock(), outputStream);
    }

    static ConcurrentNeatEvaluator create(final InputStream inputStream, final NeatLoadSettings loadSettings)
            throws IOException {
        try (ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)) {
            Context context;
            Population population;

            try {
                context = ContextObject.create(objectInputStream, loadSettings.createContext());
            } catch (IOException | ClassNotFoundException e) {
                throw new IOException("unable to load the evaluator: failed at loading the context", e);
            }

            try {
                population = Population.create(objectInputStream);
            } catch (IOException | ClassNotFoundException e) {
                throw new IOException("unable to load the evaluator: failed at loading the population", e);
            }

            return new ConcurrentNeatEvaluator(context, population);
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private final class ConcurrentNeatState implements NeatState {
        private int getIteration(final Lock lock) {
            lock.lock();

            try {
                return population.getIteration();
            } finally {
                lock.unlock();
            }
        }

        @Override
        public int getIteration() {
            return getIteration(lock.readLock());
        }

        private int getGeneration(final Lock lock) {
            lock.lock();

            try {
                return population.getGeneration();
            } finally {
                lock.unlock();
            }
        }

        @Override
        public int getGeneration() {
            return getGeneration(lock.readLock());
        }

        private int getSpeciesCount(final Lock lock) {
            lock.lock();

            try {
                return population.getSpeciesCount();
            } finally {
                lock.unlock();
            }
        }

        @Override
        public int getSpeciesCount() {
            return getSpeciesCount(lock.readLock());
        }

        private Genome getChampionGenome(final Lock lock) {
            lock.lock();

            try {
                return population.getChampionOrganismActivator().getGenome();
            } finally {
                lock.unlock();
            }
        }

        @Override
        public Genome getChampionGenome() {
            return getChampionGenome(lock.readLock());
        }

        private float getMaximumFitness(final Lock lock) {
            lock.lock();

            try {
                return population.getChampionOrganismActivator().getFitness();
            } finally {
                lock.unlock();
            }
        }

        @Override
        public float getMaximumFitness() {
            return getMaximumFitness(lock.readLock());
        }

        private MetricsViewer createMetricsViewer(final Lock lock) {
            lock.lock();

            try {
                return context.metrics().createMetricsViewer();
            } finally {
                lock.unlock();
            }
        }

        @Override
        public MetricsViewer createMetricsViewer() {
            return createMetricsViewer(lock.readLock());
        }
    }
}
