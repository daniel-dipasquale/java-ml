package com.dipasquale.search.mcts.heuristic.selection;

@FunctionalInterface
public interface CPuctAlgorithm { // PUCT = Predictor + Upper Confidence Bounds
    float calculate(int simulations, int visited);
}
