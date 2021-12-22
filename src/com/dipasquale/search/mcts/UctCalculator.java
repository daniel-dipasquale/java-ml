package com.dipasquale.search.mcts;

@FunctionalInterface
public interface UctCalculator<T extends State> {
    float calculate(int simulations, Node<T> node);
}
