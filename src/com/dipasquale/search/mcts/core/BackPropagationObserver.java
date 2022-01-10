package com.dipasquale.search.mcts.core;

@FunctionalInterface
public interface BackPropagationObserver<TState extends State, TEdge extends Edge, TEnvironment extends Environment<TState, TEnvironment>> {
    void notify(SearchNode<TState, TEdge, TEnvironment> leafNode, int simulationStatusId);
}
