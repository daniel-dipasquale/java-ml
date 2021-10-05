package com.dipasquale.ai.rl.neat.core;

import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.ai.rl.neat.phenotype.NeuronMemory;
import com.dipasquale.ai.rl.neat.settings.EvaluatorLoadSettings;
import com.dipasquale.ai.rl.neat.speciation.core.PopulationExtinctionException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

final class ConcurrentNeatTrainer implements NeatTrainer {
    private final ConcurrentNeatEvaluator evaluator;
    private final ReadWriteLock lock;

    private ConcurrentNeatTrainer(final ConcurrentNeatEvaluator evaluator, final ReadWriteLock lock) {
        this.evaluator = evaluator;
        this.lock = lock;
    }

    private ConcurrentNeatTrainer(final Context context, final ReadWriteLock lock) {
        this(new ConcurrentNeatEvaluator(context, lock), lock);
    }

    ConcurrentNeatTrainer(final Context context) {
        this(context, new ReentrantReadWriteLock());
    }

    private boolean executeTrainingPolicy(final NeatTrainingPolicy trainingPolicy) {
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
        }
    }

    @Override
    public NeatState getState() {
        return evaluator.getState();
    }

    @Override
    public boolean train(final NeatTrainingPolicy trainingPolicy) {
        lock.writeLock().lock();

        try {
            return executeTrainingPolicy(trainingPolicy);
        } finally {
            lock.writeLock().unlock();
        }
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
    public void save(final OutputStream outputStream)
            throws IOException {
        evaluator.save(outputStream);
    }

    @Override
    public void load(final InputStream inputStream, final EvaluatorLoadSettings settings)
            throws IOException {
        evaluator.load(inputStream, settings);
    }
}
