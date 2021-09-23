package com.dipasquale.ai.rl.neat.core;

public enum NeatTrainingResult {
    CONTINUE_TRAINING,
    STOP_TRAINING,
    EVALUATE_FITNESS,
    EVOLVE,
    RESTART,
    WORKING_SOLUTION_FOUND
}
