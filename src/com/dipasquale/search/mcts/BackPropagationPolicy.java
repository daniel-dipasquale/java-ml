package com.dipasquale.search.mcts;

@FunctionalInterface
public interface BackPropagationPolicy<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> {
    void process(SearchNode<TAction, TEdge, TState> leafNode, int simulationStatusId);

    default void process(final SimulationResult<TAction, TEdge, TState> simulationResult) {
        process(simulationResult.getNode(), simulationResult.getStatusId());
    }
}
