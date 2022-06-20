package com.dipasquale.search.mcts;

public interface Participant<TAction extends Action, TState extends State<TAction, TState>> {
    SearchNodeResult<TAction, TState> produceNext(SearchNodeResult<TAction, TState> searchNodeResult);

    void accept(SearchNodeResult<TAction, TState> searchNodeResult);
}
