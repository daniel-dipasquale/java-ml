package com.dipasquale.search.mcts.simulation;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.State;

@FunctionalInterface
public interface SimulationPolicy<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> {
    TSearchNode simulate(int simulations, TSearchNode selectedSearchNode);
}
