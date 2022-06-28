package com.dipasquale.search.mcts.initialization.concurrent;

import com.dipasquale.search.mcts.SearchNodeFactory;
import com.dipasquale.search.mcts.SearchNodeGroupProvider;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.TraversalPolicy;
import com.dipasquale.search.mcts.concurrent.ConcurrentEdge;
import com.dipasquale.search.mcts.concurrent.ConcurrentSearchNode;
import com.dipasquale.search.mcts.concurrent.ConcurrentSearchNodeExplorer;
import com.dipasquale.search.mcts.concurrent.ConcurrentSearchNodeGroupProvider;
import com.dipasquale.search.mcts.expansion.ExpansionPolicy;
import com.dipasquale.search.mcts.expansion.concurrent.ConcurrentOptionalExpansionPolicy;
import com.dipasquale.search.mcts.heuristic.intention.ExplorationHeuristic;
import com.dipasquale.search.mcts.initialization.AbstractInitializationContext;
import com.dipasquale.search.mcts.intention.IntentionType;
import com.dipasquale.search.mcts.propagation.BackPropagationObserver;
import com.dipasquale.search.mcts.propagation.BackPropagationPolicy;
import com.dipasquale.search.mcts.propagation.BackPropagationStep;
import com.dipasquale.search.mcts.propagation.concurrent.ConcurrentBackPropagationPolicy;
import com.dipasquale.search.mcts.seek.ComprehensiveSeekPolicy;
import com.dipasquale.search.mcts.seek.SeekStrategy;
import com.dipasquale.search.mcts.seek.concurrent.ConcurrentSeekStrategy;
import com.dipasquale.search.mcts.selection.SelectionPolicy;
import com.dipasquale.search.mcts.selection.concurrent.ConcurrentSelectionPolicy;
import com.dipasquale.search.mcts.selection.concurrent.ConcurrentUnexploredPrimerTraversalPolicy;
import com.dipasquale.search.mcts.simulation.SimulationPolicy;
import com.dipasquale.search.mcts.simulation.concurrent.ConcurrentSimulationPolicy;
import com.dipasquale.synchronization.event.loop.ParallelEventLoop;
import lombok.Getter;

public final class ConcurrentInitializationContext<TAction, TEdge extends ConcurrentEdge, TState extends State<TAction, TState>> extends AbstractInitializationContext<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> {
    @Getter
    private final SearchNodeFactory<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> searchNodeFactory;
    private final ParallelEventLoop eventLoop;
    private final ComprehensiveSeekPolicy comprehensiveSeekPolicy;

    private ConcurrentInitializationContext(final SearchNodeFactory<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> searchNodeFactory, final ParallelEventLoop eventLoop, final ExplorationHeuristic<TAction> explorationHeuristic, final IntentionType intentionType, final ComprehensiveSeekPolicy comprehensiveSeekPolicy) {
        super(intentionType, explorationHeuristic);
        this.searchNodeFactory = searchNodeFactory;
        this.eventLoop = eventLoop;
        this.comprehensiveSeekPolicy = comprehensiveSeekPolicy;
    }

    public ConcurrentInitializationContext(final SearchNodeFactory<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> searchNodeFactory, final ParallelEventLoop eventLoop, final ExplorationHeuristic<TAction> explorationHeuristic, final ComprehensiveSeekPolicy comprehensiveSeekPolicy) {
        this(searchNodeFactory, eventLoop, explorationHeuristic, IntentionType.determine(explorationHeuristic), comprehensiveSeekPolicy);
    }

    @Override
    public SearchNodeGroupProvider<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> getSearchNodeGroupProvider() {
        return ConcurrentSearchNodeGroupProvider.getInstance();
    }

    @Override
    protected TraversalPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> createUnexploredPrimerTraversalPolicy(final ExpansionPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> expansionPolicy) {
        return new ConcurrentUnexploredPrimerTraversalPolicy<>(expansionPolicy);
    }

    @Override
    protected ExpansionPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> createOptionalExpansionPolicy(final ExpansionPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> expansionPolicy) {
        return new ConcurrentOptionalExpansionPolicy<>(expansionPolicy);
    }

    @Override
    protected SelectionPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> createSelectionPolicy(final ExpansionPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> optionalExpansionPolicy, final TraversalPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> unexploredPrimerTraversalPolicy, final TraversalPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> explorableTraversalPolicy) {
        return new ConcurrentSelectionPolicy<>(optionalExpansionPolicy, unexploredPrimerTraversalPolicy, explorableTraversalPolicy);
    }

    @Override
    protected SimulationPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> createSimulationPolicy(final TraversalPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> traversalPolicy) {
        return new ConcurrentSimulationPolicy<>(comprehensiveSeekPolicy, traversalPolicy);
    }

    @Override
    public <TContext> BackPropagationPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> createBackPropagationPolicy(final BackPropagationStep<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>, TContext> step, final BackPropagationObserver<TAction, TState> observer) {
        return new ConcurrentBackPropagationPolicy<>(ConcurrentSearchNodeExplorer.getInstance(), step, observer);
    }

    @Override
    public SeekStrategy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> createSearchStrategy(final SelectionPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> selectionPolicy, final SimulationPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> simulationPolicy, final BackPropagationPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> backPropagationPolicy) {
        return new ConcurrentSeekStrategy<>(eventLoop, comprehensiveSeekPolicy, selectionPolicy, simulationPolicy, backPropagationPolicy);
    }
}
