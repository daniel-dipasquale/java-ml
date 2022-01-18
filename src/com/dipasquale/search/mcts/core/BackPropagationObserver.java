package com.dipasquale.search.mcts.core;

@FunctionalInterface
public interface BackPropagationObserver<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> {
    void notify(SearchNode<TAction, TEdge, TState> leafNode, int simulationStatusId);
}
