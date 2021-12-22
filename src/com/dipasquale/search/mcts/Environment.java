package com.dipasquale.search.mcts;

public interface Environment<T extends State> {
    int getStatusId();

    float[] getScoreEstimates();

    Node<T> getCurrentNode();

    Iterable<T> createAllPossibleStates();

    Environment<T> accept(Node<T> node);
}
