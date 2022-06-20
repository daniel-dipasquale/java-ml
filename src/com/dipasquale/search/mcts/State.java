package com.dipasquale.search.mcts;

public interface State<TAction extends Action, TState extends State<TAction, TState>> {
    int getStatusId();

    int getParticipantId();

    int getNextParticipantId();

    boolean isIntentional();

    boolean isNextIntentional();

    TAction createRootAction();

    Iterable<TAction> createAllPossibleActions();

    TState accept(TAction action);
}
