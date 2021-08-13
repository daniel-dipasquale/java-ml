/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.ai.rl.neat.core;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class NeatActivatorEvaluator implements NeatActivator {
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
