package com.dipasquale.ai.rl.neat.core;

public enum NeatTrainingResult {
    EVALUATE_FITNESS,
    EVOLVE,
    EVALUATE_FITNESS_AND_EVOLVE,
    RESTART,
    SOLUTION_NOT_FOUND,
    WORKING_SOLUTION_FOUND
}
