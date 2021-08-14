package com.dipasquale.ai.rl.neat.core;

public enum NeatEvaluatorTrainingResult {
    EVALUATE_FITNESS,
    EVOLVE,
    EVALUATE_FITNESS_AND_EVOLVE,
    RESTART,
    STOP,
    WORKING_SOLUTION_FOUND
}
