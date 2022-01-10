package com.dipasquale.search.mcts.core;

public interface Environment<TState extends State, TEnvironment extends Environment<TState, TEnvironment>> {
    TState getCurrentState();

    int getNextParticipantId();

    int getStatusId();

    Iterable<TState> createAllPossibleStates();

    TEnvironment accept(TState state);
}
