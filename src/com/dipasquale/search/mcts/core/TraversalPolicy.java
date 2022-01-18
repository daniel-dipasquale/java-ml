package com.dipasquale.search.mcts.core;

@FunctionalInterface
public interface TraversalPolicy<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> {
    SearchNode<TAction, TEdge, TState> next(int simulations, SearchNode<TAction, TEdge, TState> node);
}
