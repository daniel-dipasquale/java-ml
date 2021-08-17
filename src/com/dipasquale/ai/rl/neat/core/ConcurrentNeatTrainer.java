package com.dipasquale.ai.rl.neat.core;

import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.ai.rl.neat.settings.EvaluatorLoadSettings;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

final class ConcurrentNeatTrainer implements NeatTrainer {
    private final ConcurrentNeatEvaluator evaluator;
    private final NeatActivatorEvaluator activator;
    private final ReadWriteLock lock;

    private ConcurrentNeatTrainer(final ConcurrentNeatEvaluator evaluator, final ReadWriteLock lock) {
        this.evaluator = evaluator;
        this.activator = new NeatActivatorEvaluator(evaluator);
        this.lock = lock;
    }

    private ConcurrentNeatTrainer(final Context context, final ReadWriteLock lock) {
        this(new ConcurrentNeatEvaluator(context, lock), lock);
    }

    ConcurrentNeatTrainer(final Context context) {
        this(context, new ReentrantReadWriteLock());
    }

    @Override
    public int getGeneration() {
        return evaluator.getGeneration();
    }

    @Override
    public int getSpeciesCount() {
        return evaluator.getSpeciesCount();
    }

    @Override
    public int getCurrentComplexity() {
        return evaluator.getCurrentComplexity();
    }

    @Override
    public float getMaximumFitness() {
        return evaluator.getMaximumFitness();
    }

    private static boolean train(final NeatTrainingPolicy trainingPolicy, final ConcurrentNeatEvaluator evaluator, final NeatActivatorEvaluator activator) {
        try {
            while (true) {
                NeatTrainingResult result = trainingPolicy.test(activator);

                switch (result) {
                    case EVALUATE_FITNESS:
                        evaluator.evaluateFitness();

                        break;

                    case EVOLVE:
                        evaluator.evolve();

                        break;

                    case EVALUATE_FITNESS_AND_EVOLVE:
                        evaluator.evaluateFitness();
                        evaluator.evolve();

                        break;

                    case RESTART:
                        evaluator.restart();

                        break;

                    case SOLUTION_NOT_FOUND:
                        return false;

                    case WORKING_SOLUTION_FOUND:
                        return true;
                }
            }
        } finally {
            trainingPolicy.complete();
        }
    }

    @Override
    public boolean train(final NeatTrainingPolicy trainingPolicy) {
        lock.writeLock().lock();

        try {
            return train(trainingPolicy, evaluator, activator);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public float[] activate(final float[] input) {
        return evaluator.activate(input);
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
