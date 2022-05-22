package com.dipasquale.search.mcts;

import com.dipasquale.common.factory.data.structure.map.MapFactory;
import com.dipasquale.common.random.float1.RandomSupport;

public interface InitializationContext<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>, TBackPropagationContext> {
    RandomSupport createRandomSupport();

    MapFactory getMapFactory();

    EdgeFactory<TEdge> getEdgeFactory();

    SearchNodeFactory<TAction, TEdge, TState, TSearchNode> getSearchNodeFactory();

    SearchNodeGroupProvider<TAction, TEdge, TState, TSearchNode> getSearchNodeGroupProvider();

    ExpansionPolicy<TAction, TEdge, TState, TSearchNode> createIntentionalExpansionPolicy(Iterable<ExpansionPolicy<TAction, TEdge, TState, TSearchNode>> preOrderExpansionPolicies, Iterable<ExpansionPolicy<TAction, TEdge, TState, TSearchNode>> postOrderExpansionPolicies);

    ExpansionPolicy<TAction, TEdge, TState, TSearchNode> createUnintentionalExpansionPolicy(Iterable<ExpansionPolicy<TAction, TEdge, TState, TSearchNode>> preOrderExpansionPolicies, Iterable<ExpansionPolicy<TAction, TEdge, TState, TSearchNode>> postOrderExpansionPolicies);

    SelectionPolicy<TAction, TEdge, TState, TSearchNode> createSelectionPolicy(SelectionConfidenceCalculator<TEdge> selectionConfidenceCalculator, ExpansionPolicy<TAction, TEdge, TState, TSearchNode> expansionPolicy);

    SimulationRolloutPolicy<TAction, TEdge, TState, TSearchNode> createSimulationRolloutPolicy(ExpansionPolicy<TAction, TEdge, TState, TSearchNode> expansionPolicy);

    BackPropagationPolicy<TAction, TEdge, TState, TSearchNode, TBackPropagationContext> createBackPropagationPolicy(BackPropagationStep<TAction, TEdge, TState, TSearchNode, TBackPropagationContext> step, BackPropagationObserver<TAction, TState> observer);

    SearchStrategy<TAction, TEdge, TState, TSearchNode> createSearchStrategy(SelectionPolicy<TAction, TEdge, TState, TSearchNode> selectionPolicy, SimulationRolloutPolicy<TAction, TEdge, TState, TSearchNode> simulationRolloutPolicy, BackPropagationPolicy<TAction, TEdge, TState, TSearchNode, TBackPropagationContext> backPropagationPolicy);
}
