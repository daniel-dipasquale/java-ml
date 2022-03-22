package com.dipasquale.search.mcts.common;

@FunctionalInterface
public interface CPuctCalculator { // PUCT = Predictor + Upper Confidence Bounds
    float calculate(int simulations, int visited);
}
