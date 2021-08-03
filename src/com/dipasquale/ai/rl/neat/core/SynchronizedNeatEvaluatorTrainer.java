package com.dipasquale.ai.rl.neat.core;

import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.ai.rl.neat.settings.EvaluatorStateSettings;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

final class SynchronizedNeatEvaluatorTrainer implements NeatEvaluatorTrainer {
    private final NeatEvaluator evaluator;
    private final NeatActivator activator;

    SynchronizedNeatEvaluatorTrainer(final Context context) {
        NeatEvaluator evaluator = new SynchronizedNeatEvaluator(context);

        this.evaluator = evaluator;
        this.activator = new NeatActivatorEvaluator(evaluator);
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
    public float getMaximumFitness() {
        return evaluator.getMaximumFitness();
    }

    @Override
    public boolean train(final NeatEvaluatorTrainingPolicy trainingPolicy) {
        synchronized (evaluator) { // NOTE: all other methods can still be invoked while this is in the loop, however, because all actions within the loop are synchronized, it still not a problem to keep it working like this
            while (true) {
                NeatEvaluatorTrainingResult result = trainingPolicy.test(activator);

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

                    case STOP:
                        return false;

                    case WORKING_SOLUTION_FOUND:
                        return true;
                }
            }
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
    public void load(final InputStream inputStream, final EvaluatorStateSettings settings)
            throws IOException {
        evaluator.load(inputStream, settings);
    }
}
