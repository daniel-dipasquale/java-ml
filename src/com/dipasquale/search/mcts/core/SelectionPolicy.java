package com.dipasquale.search.mcts.core;

@FunctionalInterface
public interface SelectionPolicy<TState extends SearchState, TEdge extends SearchEdge, TEnvironment extends Environment<TState, TEnvironment>> {
    SearchNode<TState, TEdge, TEnvironment> next(int simulations, SearchNode<TState, TEdge, TEnvironment> node);
}
