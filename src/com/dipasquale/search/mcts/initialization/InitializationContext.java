package com.dipasquale.search.mcts.initialization;

import com.dipasquale.common.random.float1.RandomSupport;
import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.SearchNodeFactory;
import com.dipasquale.search.mcts.SearchNodeGroupProvider;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.expansion.ExpansionPolicy;
import com.dipasquale.search.mcts.heuristic.selection.UctAlgorithm;
import com.dipasquale.search.mcts.propagation.BackPropagationObserver;
import com.dipasquale.search.mcts.propagation.BackPropagationPolicy;
import com.dipasquale.search.mcts.propagation.BackPropagationStep;
import com.dipasquale.search.mcts.seek.SeekStrategy;
import com.dipasquale.search.mcts.selection.SelectionPolicy;
import com.dipasquale.search.mcts.simulation.SimulationPolicy;

public interface InitializationContext<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> {
    RandomSupport createRandomSupport();

    SearchNodeFactory<TAction, TEdge, TState, TSearchNode> getSearchNodeFactory();

    SearchNodeGroupProvider<TAction, TEdge, TState, TSearchNode> getSearchNodeGroupProvider();

    ExpansionPolicy<TAction, TEdge, TState, TSearchNode> createIntentionalExpansionPolicy(Iterable<ExpansionPolicy<TAction, TEdge, TState, TSearchNode>> preOrderExpansionPolicies, Iterable<ExpansionPolicy<TAction, TEdge, TState, TSearchNode>> postOrderExpansionPolicies);

    ExpansionPolicy<TAction, TEdge, TState, TSearchNode> createUnintentionalExpansionPolicy(Iterable<ExpansionPolicy<TAction, TEdge, TState, TSearchNode>> preOrderExpansionPolicies, Iterable<ExpansionPolicy<TAction, TEdge, TState, TSearchNode>> postOrderExpansionPolicies);

    SelectionPolicy<TAction, TEdge, TState, TSearchNode> createSelectionPolicy(UctAlgorithm<TEdge> uctAlgorithm, ExpansionPolicy<TAction, TEdge, TState, TSearchNode> expansionPolicy);

    SimulationPolicy<TAction, TEdge, TState, TSearchNode> createSimulationPolicy(ExpansionPolicy<TAction, TEdge, TState, TSearchNode> expansionPolicy);

    <TContext> BackPropagationPolicy<TAction, TEdge, TState, TSearchNode> createBackPropagationPolicy(BackPropagationStep<TAction, TEdge, TState, TSearchNode, TContext> step, BackPropagationObserver<TAction, TState> observer);

    SeekStrategy<TAction, TEdge, TState, TSearchNode> createSearchStrategy(SelectionPolicy<TAction, TEdge, TState, TSearchNode> selectionPolicy, SimulationPolicy<TAction, TEdge, TState, TSearchNode> simulationPolicy, BackPropagationPolicy<TAction, TEdge, TState, TSearchNode> backPropagationPolicy);
}
