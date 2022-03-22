package com.dipasquale.search.mcts;

@FunctionalInterface
public interface BackPropagationObserver<TAction extends Action, TState extends State<TAction, TState>> {
    void notify(int statusId, Iterable<TState> states);
}
