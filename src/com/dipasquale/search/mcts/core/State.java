package com.dipasquale.search.mcts.core;

public interface State<TAction extends Action, TState extends State<TAction, TState>> {
    TAction getLastAction();

    int getNextParticipantId();

    int getStatusId();

    boolean isValid(int actionId);

    Iterable<TAction> createAllPossibleActions();

    TState accept(TAction action);

    int hashCode();

    boolean equals(Object other);
}
