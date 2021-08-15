package com.dipasquale.ai.rl.neat.core;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class NeatActivatorTrainer implements NeatActivator {
    private final NeatTrainer trainer;

    @Override
    public int getGeneration() {
        return trainer.getGeneration();
    }

    @Override
    public int getSpeciesCount() {
        return trainer.getSpeciesCount();
    }

    @Override
    public float getFitness() {
        return trainer.getMaximumFitness();
    }

    @Override
    public float[] activate(final float[] inputs) {
        return trainer.activate(inputs);
    }
}
