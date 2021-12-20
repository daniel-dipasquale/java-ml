package com.dipasquale.search.mcts;

@FunctionalInterface
public interface UctCalculator<T> {
    float calculate(int simulations, Node<T> node);
}
