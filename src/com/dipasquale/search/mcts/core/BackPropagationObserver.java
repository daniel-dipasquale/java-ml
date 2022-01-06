package com.dipasquale.search.mcts.core;

@FunctionalInterface
public interface BackPropagationObserver<TState extends SearchState, TEdge extends SearchEdge, TEnvironment extends Environment<TState, TEnvironment>> {
    void notify(SearchNode<TState, TEdge, TEnvironment> leafNode, int simulationStatusId);
}
