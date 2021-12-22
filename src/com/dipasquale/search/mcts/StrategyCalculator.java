package com.dipasquale.search.mcts;

public interface StrategyCalculator<T extends State> {
    float calculateEfficiency(Node<T> node);
}
