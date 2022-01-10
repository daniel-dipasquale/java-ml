package com.dipasquale.search.mcts.core;

@FunctionalInterface
public interface BackPropagationPolicy<TState extends State, TEdge extends Edge, TEnvironment extends Environment<TState, TEnvironment>> {
    void process(SearchNode<TState, TEdge, TEnvironment> leafNode, int simulationStatusId);

    default void process(final SimulationResult<TState, TEdge, TEnvironment> simulationResult) {
        process(simulationResult.getNode(), simulationResult.getStatusId());
    }
}
