package com.dipasquale.search.mcts.core;

@FunctionalInterface
public interface SelectionPolicy<TState extends SearchState, TEdge extends SearchEdge> {
    SearchNode<TState, TEdge> next(int simulations, SearchNode<TState, TEdge> node);
}
