package com.dipasquale.search.mcts;

@FunctionalInterface
public interface LeafNodeObserver<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> {
    void notify(SearchNode<TAction, TEdge, TState> leafNode, int simulationStatusId);
}
