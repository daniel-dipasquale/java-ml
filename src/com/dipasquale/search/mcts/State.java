package com.dipasquale.search.mcts;

public interface State<TAction, TState extends State<TAction, TState>> {
    int getStatusId();

    int getParticipantId();

    int getNextParticipantId();

    boolean isActionIntentional();

    boolean isNextActionIntentional();

    Iterable<TAction> createAllPossibleActions();

    TState accept(TAction action);
}
