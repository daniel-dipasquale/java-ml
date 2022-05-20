package com.dipasquale.search.mcts;

import com.dipasquale.common.factory.data.structure.map.MapFactory;
import com.dipasquale.common.random.float1.RandomSupport;
import com.dipasquale.search.mcts.common.ExplorationHeuristic;

public interface InitializationContext<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>, TBackPropagationContext> {
    RandomSupport createRandomSupport();

    MapFactory getMapFactory();

    EdgeFactory<TEdge> getEdgeFactory();

    SearchNodeFactory<TAction, TEdge, TState, TSearchNode> getSearchNodeFactory();

    SearchNodeGroupProvider<TAction, TEdge, TState, TSearchNode> getSearchNodeGroupProvider();

    TraversalPolicy<TAction, TEdge, TState, TSearchNode> createIntentionalTraversalPolicy(SelectionConfidenceCalculator<TEdge> selectionConfidenceCalculator);

    ExpansionPolicy<TAction, TEdge, TState, TSearchNode> createIntentionalExpansionPolicy(Iterable<ExpansionPolicy<TAction, TEdge, TState, TSearchNode>> preOrderExpansionPolicies, Iterable<ExpansionPolicy<TAction, TEdge, TState, TSearchNode>> postOrderExpansionPolicies);

    ExpansionPolicy<TAction, TEdge, TState, TSearchNode> createUnintentionalExpansionPolicy(Iterable<ExpansionPolicy<TAction, TEdge, TState, TSearchNode>> preOrderExpansionPolicies, ExplorationHeuristic<TAction> explorationHeuristic, Iterable<ExpansionPolicy<TAction, TEdge, TState, TSearchNode>> postOrderExpansionPolicies);

    SearchStrategy<TAction, TEdge, TState, TSearchNode> createSearchStrategy(SearchPolicy searchPolicy, SelectionPolicy<TAction, TEdge, TState, TSearchNode> selectionPolicy, SimulationRolloutPolicy<TAction, TEdge, TState, TSearchNode> simulationRolloutPolicy, BackPropagationPolicy<TAction, TEdge, TState, TSearchNode, TBackPropagationContext> backPropagationPolicy);
}
