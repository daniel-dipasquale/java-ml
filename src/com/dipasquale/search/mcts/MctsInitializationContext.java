package com.dipasquale.search.mcts;

import com.dipasquale.common.factory.data.structure.map.MapFactory;
import com.dipasquale.common.random.float1.RandomSupport;

public interface MctsInitializationContext<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>, TBackPropagationContext> {
    RandomSupport createRandomSupport();

    MapFactory getMapFactory();

    EdgeFactory<TEdge> getEdgeFactory();

    SearchNodeFactory<TAction, TEdge, TState, TSearchNode> getSearchNodeFactory();

    SearchStrategy<TAction, TEdge, TState, TSearchNode> createSearchStrategy(SearchPolicy searchPolicy, SelectionPolicy<TAction, TEdge, TState, TSearchNode> selectionPolicy, SimulationRolloutPolicy<TAction, TEdge, TState, TSearchNode> simulationRolloutPolicy, BackPropagationPolicy<TAction, TEdge, TState, TSearchNode, TBackPropagationContext> backPropagationPolicy);
}
