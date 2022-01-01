package com.dipasquale.search.mcts.core;

public interface Environment<T extends SearchState> {
    T getCurrentState();

    int getStatusId();

    Iterable<T> createAllPossibleStates();

    Environment<T> accept(T state);
}
