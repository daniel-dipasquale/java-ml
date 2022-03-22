package com.dipasquale.search.mcts;

@FunctionalInterface
public interface SelectionPolicy<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> {
    SearchNode<TAction, TEdge, TState> select(int simulations, SearchNode<TAction, TEdge, TState> rootSearchNode);
}
