package com.dipasquale.ai.rl.neat.core;

import com.dipasquale.ai.rl.neat.speciation.metric.IterationMetricData;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class NeatActivatorTrainer implements NeatActivator {
    private final NeatTrainer trainer;

    @Override
    public int getIteration() {
        return trainer.getIteration();
    }

    @Override
    public int getGeneration() {
        return trainer.getGeneration();
    }

    @Override
    public int getSpeciesCount() {
        return trainer.getSpeciesCount();
    }

    @Override
    public int getConnections() {
        return trainer.getCurrentConnections();
    }

    @Override
    public float getFitness() {
        return trainer.getMaximumFitness();
    }

    @Override
    public Map<Integer, IterationMetricData> getMetrics() {
        return trainer.getMetrics();
    }

    @Override
    public float[] activate(final float[] inputs) {
        return trainer.activate(inputs);
    }
}
