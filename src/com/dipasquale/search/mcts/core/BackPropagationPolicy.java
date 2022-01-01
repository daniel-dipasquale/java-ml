package com.dipasquale.search.mcts.core;

@FunctionalInterface
public interface BackPropagationPolicy<TState extends SearchState, TEdge extends SearchEdge> {
    void process(SearchNode<TState, TEdge> rootNode, SearchNode<TState, TEdge> leafNode, int simulationStatusId);
}
