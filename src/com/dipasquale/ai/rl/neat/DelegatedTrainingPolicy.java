package com.dipasquale.ai.rl.neat;

import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
public final class DelegatedTrainingPolicy implements NeatTrainingPolicy, Serializable {
    @Serial
    private static final long serialVersionUID = -8584467764466409799L;
    private int lastIterationTested = 0;
    private int lastGenerationTested = 0;
    private NeatTrainingResult previousTestResult = null;
    private final NeatTrainingAssessor trainingAssessor;

    @Override
    public NeatTrainingResult test(final NeatActivator activator) {
        NeatState state = activator.getState();
        int iteration = state.getIteration();
        int generation = state.getGeneration();

        if (lastIterationTested >= iteration && lastGenerationTested >= generation) {
            return previousTestResult;
        }

        lastIterationTested = iteration;
        lastGenerationTested = generation;

        if (trainingAssessor.test(activator)) {
            return previousTestResult = NeatTrainingResult.WORKING_SOLUTION_FOUND;
        }

        return previousTestResult = NeatTrainingResult.CONTINUE_TRAINING;
    }

    @Override
    public void reset() {
        lastIterationTested = 0;
        lastGenerationTested = 0;
        previousTestResult = null;
    }

    @Override
    public NeatTrainingPolicy createClone() {
        return new DelegatedTrainingPolicy(trainingAssessor);
    }
}
