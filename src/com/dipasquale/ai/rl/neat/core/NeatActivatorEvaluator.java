package com.dipasquale.ai.rl.neat.core;

import com.dipasquale.ai.rl.neat.speciation.metric.IterationMetrics;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class NeatActivatorEvaluator implements NeatActivator {
    private final NeatEvaluator evaluator;

    @Override
    public int getIteration() {
        return evaluator.getIteration();
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
    public int getCurrentHiddenNodes() {
        return evaluator.getCurrentHiddenNodes();
    }

    @Override
    public int getCurrentConnections() {
        return evaluator.getCurrentConnections();
    }

    @Override
    public float getMaximumFitness() {
        return evaluator.getMaximumFitness();
    }

    @Override
    public Map<Integer, IterationMetrics> getMetrics() {
        return evaluator.getMetrics();
    }

    @Override
    public float[] activate(final float[] inputs) {
        return evaluator.activate(inputs);
    }
}
