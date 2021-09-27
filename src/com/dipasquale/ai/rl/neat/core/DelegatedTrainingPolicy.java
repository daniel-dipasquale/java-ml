package com.dipasquale.ai.rl.neat.core;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class DelegatedTrainingPolicy implements NeatTrainingPolicy {
    private int iterationTested = 0;
    private int generationTested = 0;
    private NeatTrainingResult previousTestResult = null;
    private final Handler handler;

    @Override
    public NeatTrainingResult test(final NeatActivator activator) {
        if (iterationTested >= activator.getIteration() && generationTested >= activator.getGeneration()) {
            return previousTestResult;
        }

        iterationTested = activator.getIteration();
        generationTested = activator.getGeneration();

        if (handler.test(activator)) {
            return previousTestResult = NeatTrainingResult.WORKING_SOLUTION_FOUND;
        }

        return previousTestResult = NeatTrainingResult.CONTINUE_TRAINING;
    }

    @Override
    public void reset() {
        iterationTested = 0;
        generationTested = 0;
        previousTestResult = null;
    }

    @FunctionalInterface
    public interface Handler {
        boolean test(NeatActivator activator);
    }
}
