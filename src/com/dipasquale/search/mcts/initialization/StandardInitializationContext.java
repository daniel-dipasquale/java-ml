package com.dipasquale.search.mcts.initialization;

import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.EdgeFactory;
import com.dipasquale.search.mcts.SearchNodeFactory;
import com.dipasquale.search.mcts.SearchNodeGroupProvider;
import com.dipasquale.search.mcts.StandardSearchNode;
import com.dipasquale.search.mcts.StandardSearchNodeExplorer;
import com.dipasquale.search.mcts.StandardSearchNodeFactory;
import com.dipasquale.search.mcts.StandardSearchNodeGroupProvider;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.TraversalPolicy;
import com.dipasquale.search.mcts.expansion.ExpansionPolicy;
import com.dipasquale.search.mcts.expansion.StandardOptionalExpansionPolicy;
import com.dipasquale.search.mcts.heuristic.intention.ExplorationHeuristic;
import com.dipasquale.search.mcts.intention.IntentionType;
import com.dipasquale.search.mcts.propagation.BackPropagationObserver;
import com.dipasquale.search.mcts.propagation.BackPropagationPolicy;
import com.dipasquale.search.mcts.propagation.BackPropagationStep;
import com.dipasquale.search.mcts.propagation.StandardBackPropagationPolicy;
import com.dipasquale.search.mcts.seek.ComprehensiveSeekPolicy;
import com.dipasquale.search.mcts.seek.SeekStrategy;
import com.dipasquale.search.mcts.seek.StandardSeekStrategy;
import com.dipasquale.search.mcts.selection.SelectionPolicy;
import com.dipasquale.search.mcts.selection.StandardSelectionPolicy;
import com.dipasquale.search.mcts.selection.StandardUnexploredPrimerTraversalPolicy;
import com.dipasquale.search.mcts.simulation.SimulationPolicy;
import com.dipasquale.search.mcts.simulation.StandardSimulationPolicy;
import lombok.Getter;

public final class StandardInitializationContext<TAction, TEdge extends Edge, TState extends State<TAction, TState>> extends AbstractInitializationContext<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> {
    @Getter
    private final SearchNodeFactory<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> searchNodeFactory;
    private final ComprehensiveSeekPolicy comprehensiveSeekPolicy;

    private StandardInitializationContext(final EdgeFactory<TEdge> edgeFactory, final ExplorationHeuristic<TAction> explorationHeuristic, final IntentionType intentionType, final ComprehensiveSeekPolicy comprehensiveSeekPolicy) {
        super(intentionType, explorationHeuristic);
        this.searchNodeFactory = new StandardSearchNodeFactory<>(edgeFactory);
        this.comprehensiveSeekPolicy = comprehensiveSeekPolicy;
    }

    public StandardInitializationContext(final EdgeFactory<TEdge> edgeFactory, final ExplorationHeuristic<TAction> explorationHeuristic, final ComprehensiveSeekPolicy comprehensiveSeekPolicy) {
        this(edgeFactory, explorationHeuristic, IntentionType.determine(explorationHeuristic), comprehensiveSeekPolicy);
    }

    @Override
    public SearchNodeGroupProvider<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> getSearchNodeGroupProvider() {
        return StandardSearchNodeGroupProvider.getInstance();
    }

    @Override
    protected TraversalPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> createUnexploredPrimerTraversalPolicy(final ExpansionPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> expansionPolicy) {
        return new StandardUnexploredPrimerTraversalPolicy<>(expansionPolicy);
    }

    @Override
    protected ExpansionPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> createOptionalExpansionPolicy(final ExpansionPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> expansionPolicy) {
        return new StandardOptionalExpansionPolicy<>(expansionPolicy);
    }

    @Override
    protected SelectionPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> createSelectionPolicy(final ExpansionPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> optionalExpansionPolicy, final TraversalPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> unexploredPrimerTraversalPolicy, final TraversalPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> explorableTraversalPolicy) {
        return new StandardSelectionPolicy<>(optionalExpansionPolicy, unexploredPrimerTraversalPolicy, explorableTraversalPolicy);
    }

    @Override
    protected SimulationPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> createSimulationPolicy(final TraversalPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> traversalPolicy) {
        return new StandardSimulationPolicy<>(comprehensiveSeekPolicy, traversalPolicy);
    }

    @Override
    public <TContext> BackPropagationPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> createBackPropagationPolicy(final BackPropagationStep<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>, TContext> step, final BackPropagationObserver<TAction, TState> observer) {
        return new StandardBackPropagationPolicy<>(StandardSearchNodeExplorer.getInstance(), step, observer);
    }

    @Override
    public SeekStrategy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> createSearchStrategy(final SelectionPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> selectionPolicy, final SimulationPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> simulationPolicy, final BackPropagationPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> backPropagationPolicy) {
        return new StandardSeekStrategy<>(comprehensiveSeekPolicy, selectionPolicy, simulationPolicy, backPropagationPolicy, StandardSearchNodeExplorer.getInstance());
    }
}
