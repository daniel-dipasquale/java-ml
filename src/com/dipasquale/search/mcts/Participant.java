package com.dipasquale.search.mcts;

public interface Participant<TAction extends Action, TState extends State<TAction, TState>> {
    TAction createNextAction(TState state);

    void accept(TState state);
}
