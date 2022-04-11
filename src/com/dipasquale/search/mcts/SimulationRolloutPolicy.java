package com.dipasquale.search.mcts;

@FunctionalInterface
public interface SimulationRolloutPolicy<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> {
    TSearchNode simulate(int simulations, TSearchNode selectedSearchNode);
}
