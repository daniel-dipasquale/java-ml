package com.dipasquale.search.mcts;

@FunctionalInterface
public interface SimulationResultObserver<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> {
    void notify(SearchNode<TAction, TEdge, TState> leafNode);
}
