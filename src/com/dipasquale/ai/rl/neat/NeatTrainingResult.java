package com.dipasquale.ai.rl.neat;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum NeatTrainingResult {
    CONTINUE_TRAINING,
    STOP_TRAINING,
    EVALUATE_FITNESS,
    EVOLVE,
    RESTART,
    WORKING_SOLUTION_FOUND
}
