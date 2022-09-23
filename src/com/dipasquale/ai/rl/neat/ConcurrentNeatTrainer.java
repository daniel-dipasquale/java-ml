package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.rl.neat.phenotype.NeatNeuronMemory;
import com.dipasquale.ai.rl.neat.speciation.PopulationExtinctionException;
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

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
final class ConcurrentNeatTrainer implements NeatTrainer {
    private final ReadWriteLock lock;
    private final ConcurrentNeatEvaluator evaluator;
    private final NeatTrainingPolicy trainingPolicy;

    ConcurrentNeatTrainer(final ReadWriteLock lock, final Context context, final NeatTrainingPolicy trainingPolicy) {
        this(lock, new ConcurrentNeatEvaluator(lock, context), trainingPolicy.createClone());
    }

    ConcurrentNeatTrainer(final Context context, final NeatTrainingPolicy trainingPolicy) {
        this(new ReentrantReadWriteLock(), context, trainingPolicy);
    }

    @Override
    public NeatState getState() {
        return evaluator.getState();
    }

    @Override
    public NeatNeuronMemory createMemory() {
        return evaluator.createMemory();
    }

    @Override
    public float[] activate(final float[] input, final NeatNeuronMemory neuronMemory) {
        return evaluator.activate(input, neuronMemory);
    }

    private boolean train(final Lock lock) {
        lock.lock();

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
            lock.unlock();
        }
    }

    @Override
    public boolean train() {
        return train(lock.writeLock());
    }

    private NeatTrainingResult test(final Lock lock) {
        lock.lock();

        try {
            return trainingPolicy.test(evaluator);
        } finally {
            trainingPolicy.reset();
            lock.unlock();
        }
    }

    @Override
    public NeatTrainingResult test() {
        return test(lock.readLock());
    }

    private void save(final Lock lock, final OutputStream outputStream)
            throws IOException {
        lock.lock();

        try {
            try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream)) {
                objectOutputStream.writeObject(trainingPolicy);
            }

            evaluator.save(outputStream);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void save(final OutputStream outputStream)
            throws IOException {
        save(lock.writeLock(), outputStream);
    }

    static ConcurrentNeatTrainer create(final InputStream inputStream, final NeatLoadSettings loadSettings)
            throws IOException {
        try (ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)) {
            NeatTrainingPolicy trainingPolicy;
            ConcurrentNeatEvaluator evaluator;

            try {
                trainingPolicy = (NeatTrainingPolicy) objectInputStream.readObject();
            } catch (IOException | ClassNotFoundException e) {
                throw new IOException("unable to load the trainer: failed to load the training policy", e);
            }

            try {
                evaluator = ConcurrentNeatEvaluator.create(inputStream, loadSettings);
            } catch (IOException e) {
                throw new IOException("unable to load the trainer: failed to load the evaluator", e);
            }

            return new ConcurrentNeatTrainer(new ReentrantReadWriteLock(), evaluator, trainingPolicy);
        }
    }
}
