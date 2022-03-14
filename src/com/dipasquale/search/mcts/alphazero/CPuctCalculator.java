package com.dipasquale.search.mcts.alphazero;

@FunctionalInterface
public interface CPuctCalculator { // PUCT = Predictor + Upper Confidence Bounds
    float calculate(int simulations, int visited);
}
