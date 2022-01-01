package com.dipasquale.search.mcts.alphazero;

@FunctionalInterface
public interface CPuctAlgorithm {
    float getValue(int simulations, int visited);
}
