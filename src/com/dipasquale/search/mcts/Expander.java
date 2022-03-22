package com.dipasquale.search.mcts;

@FunctionalInterface
public interface Expander<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> {
    void expand(SearchNode<TAction, TEdge, TState> searchNode);
}
