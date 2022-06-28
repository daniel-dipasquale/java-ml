package com.dipasquale.search.mcts;

@FunctionalInterface
public interface TraversalPolicy<TAction, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> {
    TSearchNode next(int simulations, TSearchNode searchNode);
}
