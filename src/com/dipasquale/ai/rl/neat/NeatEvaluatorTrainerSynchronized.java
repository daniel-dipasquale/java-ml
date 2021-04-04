package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.rl.neat.context.Context;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

final class NeatEvaluatorTrainerSynchronized implements NeatEvaluatorTrainer {
    private final NeatEvaluator evaluator;
    private final NeatActivator activator;

    NeatEvaluatorTrainerSynchronized(final Context context) {
        NeatEvaluator evaluator = new NeatEvaluatorSynchronized(context);

        this.evaluator = evaluator;
        this.activator = new NeatActivatorDefault(evaluator);
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
    public boolean train(final NeatEvaluatorTrainingPolicy trainingPolicy) {
        synchronized (evaluator) {
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
    public float getMaximumFitness() {
        return evaluator.getMaximumFitness();
    }

    @Override
    public float[] activate(final float[] input) {
        return evaluator.activate(input);
    }

    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
    private static final class NeatActivatorDefault implements NeatActivator {
        private final NeatEvaluator evaluator;

        @Override
        public int getGeneration() {
            return evaluator.getGeneration();
        }

        @Override
        public int getSpeciesCount() {
            return evaluator.getSpeciesCount();
        }

        @Override
        public float getFitness() {
            return evaluator.getMaximumFitness();
        }

        @Override
        public float[] activate(final float[] inputs) {
            return evaluator.activate(inputs);
        }
    }
}
