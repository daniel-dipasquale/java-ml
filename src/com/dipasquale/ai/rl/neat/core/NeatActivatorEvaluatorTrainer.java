package com.dipasquale.ai.rl.neat.core;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class NeatActivatorEvaluatorTrainer implements NeatActivator {
    private final NeatEvaluatorTrainer evaluatorTrainer;

    @Override
    public int getGeneration() {
        return evaluatorTrainer.getGeneration();
    }

    @Override
    public int getSpeciesCount() {
        return evaluatorTrainer.getSpeciesCount();
    }

    @Override
    public float getFitness() {
        return evaluatorTrainer.getMaximumFitness();
    }

    @Override
    public float[] activate(final float[] inputs) {
        return evaluatorTrainer.activate(inputs);
    }
}
