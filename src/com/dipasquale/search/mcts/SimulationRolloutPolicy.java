package com.dipasquale.search.mcts;

@FunctionalInterface
public interface SimulationRolloutPolicy<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> {
    SearchNode<TAction, TEdge, TState> simulate(int simulations, SearchNode<TAction, TEdge, TState> selectedSearchNode);
}
