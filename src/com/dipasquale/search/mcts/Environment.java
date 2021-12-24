package com.dipasquale.search.mcts;

public interface Environment<T extends State> {
    T getCurrentState();

    int getStatusId();

    Iterable<T> createAllPossibleStates();

    Environment<T> accept(T state);
}
