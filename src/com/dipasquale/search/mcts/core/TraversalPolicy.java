package com.dipasquale.search.mcts.core;

@FunctionalInterface
public interface TraversalPolicy<TState extends State, TEdge extends Edge, TEnvironment extends Environment<TState, TEnvironment>> {
    SearchNode<TState, TEdge, TEnvironment> next(int simulations, SearchNode<TState, TEdge, TEnvironment> node);
}
