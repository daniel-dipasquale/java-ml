package com.dipasquale.ai.rl.neat.core;

import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.ai.rl.neat.phenotype.NeuronMemory;
import com.dipasquale.ai.rl.neat.speciation.core.PopulationExtinctionException;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

final class ConcurrentNeatTrainer implements NeatTrainer {
    private final ReadWriteLock lock;
    private final ConcurrentNeatEvaluator evaluator;
    private NeatTrainingPolicy trainingPolicy;

    private ConcurrentNeatTrainer(final ReadWriteLock lock, final ConcurrentNeatEvaluator evaluator, final NeatTrainingPolicy trainingPolicy) {
        this.lock = lock;
        this.evaluator = evaluator;
        this.trainingPolicy = trainingPolicy.createClone();
    }

    ConcurrentNeatTrainer(final Context context, final NeatTrainingPolicy trainingPolicy, final ReadWriteLock lock) {
        this(lock, new ConcurrentNeatEvaluator(context, lock), trainingPolicy);
    }

    ConcurrentNeatTrainer(final Context context, final NeatTrainingPolicy trainingPolicy) {
        this(context, trainingPolicy, new ReentrantReadWriteLock());
    }

    @Override
    public NeatState getState() {
        return evaluator.getState();
    }

    @Override
    public NeuronMemory createMemory() {
        return evaluator.createMemory();
    }

    @Override
    public float[] activate(final float[] input, final NeuronMemory neuronMemory) {
        return evaluator.activate(input, neuronMemory);
    }

    @Override
    public boolean train() {
        lock.writeLock().lock();

        try {
            while (true) {
                NeatTrainingResult result = trainingPolicy.test(evaluator);

                switch (result) {
                    case EVALUATE_FITNESS:
                        evaluator.evaluateFitness();

                        break;

                    case EVOLVE:
                        try {
                            evaluator.evolve();
                        } catch (PopulationExtinctionException e) {
                            evaluator.restart();
                        }

                        break;

                    case RESTART:
                        evaluator.restart();

                        break;

                    case STOP_TRAINING:
                        return false;

                    case WORKING_SOLUTION_FOUND:
                        return true;
                }
            }
        } finally {
            trainingPolicy.reset();
            lock.writeLock().unlock();
        }
    }

    @Override
    public NeatTrainingResult test() {
        lock.readLock().lock();

        try {
            return trainingPolicy.test(evaluator);
        } finally {
            trainingPolicy.reset();
            lock.readLock().unlock();
        }
    }

    @Override
    public void save(final OutputStream outputStream)
            throws IOException {
        lock.writeLock().lock();

        try {
            try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream)) {
                objectOutputStream.writeObject(trainingPolicy);
            }

            evaluator.save(outputStream);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void load(final InputStream inputStream, final EvaluatorLoadSettings settings)
            throws IOException {
        lock.writeLock().lock();

        try {
            try (ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)) {
                trainingPolicy = (NeatTrainingPolicy) objectInputStream.readObject();
            } catch (ClassNotFoundException e) {
                throw new IOException("unable to load the training policy", e);
            }

            evaluator.load(inputStream, settings);
        } finally {
            lock.writeLock().unlock();
        }
    }
}
