package com.dipasquale.ai.rl.neat;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicBoolean;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class ParallelTrainingPolicy implements NeatTrainingPolicy, Serializable {
    @Serial
    private static final long serialVersionUID = 188585934977792857L;
    private final AtomicBoolean solutionFound;

    @Override
    public NeatTrainingResult test(final NeatActivator activator) {
        if (solutionFound.get()) {
            return NeatTrainingResult.STOP_TRAINING;
        }

        return NeatTrainingResult.CONTINUE_TRAINING;
    }

    @Override
    public void reset() {
    }

    @Override
    public NeatTrainingPolicy createClone() {
        return this;
    }
}
