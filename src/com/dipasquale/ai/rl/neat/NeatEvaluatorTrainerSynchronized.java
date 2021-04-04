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
    public void restart() {
        evaluator.restart();
    }

    @Override
    public void train(final NeatEvaluatorTrainingPolicy trainingPolicy) {
        synchronized (evaluator) {
            boolean shouldTrain = true;

            while (shouldTrain) {
                NeatEvaluatorTrainingResult result = trainingPolicy.test(activator);

                shouldTrain = switch (result) {
                    case EVALUATE_FITNESS -> {
                        evaluator.evaluateFitness();

                        yield true;
                    }

                    case EVOLVE -> {
                        evaluator.evolve();

                        yield true;
                    }

                    case EVALUATE_FITNESS_AND_EVOLVE -> {
                        evaluator.evaluateFitness();
                        evaluator.evolve();

                        yield true;
                    }

                    case RESTART -> {
                        evaluator.restart();

                        yield true;
                    }

                    case STOP -> false;
                };
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
