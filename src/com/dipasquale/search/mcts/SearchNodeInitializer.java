package com.dipasquale.search.mcts;

@FunctionalInterface
public interface SearchNodeInitializer<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> {
    boolean apply(SearchNode<TAction, TEdge, TState> node);
}
