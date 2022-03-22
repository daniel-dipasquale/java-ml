package com.dipasquale.search.mcts;

public interface State<TAction extends Action, TState extends State<TAction, TState>> {
    int getDepth();

    int getStatusId();

    int getParticipantId();

    int getNextParticipantId();

    boolean isIntentional();

    boolean isNextIntentional();

    TAction getLastAction();

    Iterable<TAction> createAllPossibleActions();

    TState accept(TAction action);
}
