package com.dipasquale.search.mcts;

public interface State<TAction extends Action, TState extends State<TAction, TState>> {
    TAction getLastAction();

    int getStatusId();

    int getNextParticipantId();

    boolean isValid(int actionId);

    Iterable<TAction> createAllPossibleActions();

    TState accept(TAction action, boolean simulation);

    int hashCode();

    boolean equals(Object other);
}
