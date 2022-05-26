package com.dipasquale.search.mcts.initialization.concurrent;

import com.dipasquale.common.random.float1.RandomSupport;
import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.EdgeFactory;
import com.dipasquale.search.mcts.SearchNodeGroupProvider;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.TraversalPolicy;
import com.dipasquale.search.mcts.buffer.GenerationTree;
import com.dipasquale.search.mcts.buffer.concurrent.ConcurrentGenerationTree;
import com.dipasquale.search.mcts.concurrent.ConcurrentEdge;
import com.dipasquale.search.mcts.concurrent.ConcurrentSearchNode;
import com.dipasquale.search.mcts.concurrent.ConcurrentSearchNodeFactory;
import com.dipasquale.search.mcts.concurrent.ConcurrentSearchNodeGroupProvider;
import com.dipasquale.search.mcts.concurrent.ConcurrentSearchNodeManager;
import com.dipasquale.search.mcts.expansion.ExpansionPolicy;
import com.dipasquale.search.mcts.expansion.UnintentionalExpansionPolicy;
import com.dipasquale.search.mcts.expansion.concurrent.ConcurrentExpansionPolicy;
import com.dipasquale.search.mcts.expansion.intention.IntentionalExpansionPolicy;
import com.dipasquale.search.mcts.heuristic.intention.ExplorationHeuristic;
import com.dipasquale.search.mcts.initialization.AbstractInitializationContext;
import com.dipasquale.search.mcts.intention.IntentionType;
import com.dipasquale.search.mcts.propagation.BackPropagationObserver;
import com.dipasquale.search.mcts.propagation.BackPropagationPolicy;
import com.dipasquale.search.mcts.propagation.BackPropagationStep;
import com.dipasquale.search.mcts.propagation.concurrent.ConcurrentBackPropagationPolicy;
import com.dipasquale.search.mcts.seek.FullSeekPolicy;
import com.dipasquale.search.mcts.seek.SeekStrategy;
import com.dipasquale.search.mcts.seek.concurrent.ConcurrentSeekStrategy;
import com.dipasquale.search.mcts.selection.SelectionPolicy;
import com.dipasquale.search.mcts.selection.concurrent.ConcurrentSelectionPolicy;
import com.dipasquale.search.mcts.selection.concurrent.ConcurrentUnexploredPrimerTraversalPolicy;
import com.dipasquale.search.mcts.simulation.SimulationRolloutPolicy;
import com.dipasquale.search.mcts.simulation.concurrent.ConcurrentIntentionalSimulationTraversalPolicy;
import com.dipasquale.search.mcts.simulation.concurrent.ConcurrentSimulationRolloutPolicy;
import com.dipasquale.synchronization.event.loop.ParallelEventLoop;
import lombok.Getter;

public final class ConcurrentInitializationContext<TAction extends Action, TEdge extends ConcurrentEdge, TState extends State<TAction, TState>> extends AbstractInitializationContext<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> {
    private final ParallelEventLoop eventLoop;
    @Getter
    private final EdgeFactory<TEdge> edgeFactory;
    @Getter
    private final ConcurrentSearchNodeFactory<TAction, TEdge, TState> searchNodeFactory;
    private final ExplorationHeuristic<TAction> explorationHeuristic;
    private final FullSeekPolicy searchPolicy;

    private ConcurrentInitializationContext(final ParallelEventLoop eventLoop, final EdgeFactory<TEdge> edgeFactory, final ExplorationHeuristic<TAction> explorationHeuristic, final IntentionType intentionType, final FullSeekPolicy searchPolicy) {
        super(intentionType);
        this.eventLoop = eventLoop;
        this.edgeFactory = edgeFactory;
        this.searchNodeFactory = new ConcurrentSearchNodeFactory<>(eventLoop.getConcurrencyLevel());
        this.explorationHeuristic = explorationHeuristic;
        this.searchPolicy = searchPolicy;
    }

    public ConcurrentInitializationContext(final ParallelEventLoop eventLoop, final EdgeFactory<TEdge> edgeFactory, final ExplorationHeuristic<TAction> explorationHeuristic, final FullSeekPolicy searchPolicy) {
        this(eventLoop, edgeFactory, explorationHeuristic, IntentionType.determine(explorationHeuristic), searchPolicy);
    }

    @Override
    public GenerationTree<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> createGenerationTree() {
        return new ConcurrentGenerationTree<>();
    }

    @Override
    public SearchNodeGroupProvider<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> getSearchNodeGroupProvider() {
        return ConcurrentSearchNodeGroupProvider.getInstance();
    }

    @Override
    public ExpansionPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> createIntentionalExpansionPolicy(final Iterable<ExpansionPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>>> preOrderExpansionPolicies, final Iterable<ExpansionPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>>> postOrderExpansionPolicies) {
        IntentionalExpansionPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> intentionalExpansionPolicy = createIntentionalExpansionPolicy();
        ExpansionPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> expansionPolicy = mergeExpansionPolicies(preOrderExpansionPolicies, intentionalExpansionPolicy, postOrderExpansionPolicies);

        return new ConcurrentExpansionPolicy<>(expansionPolicy, true);
    }

    @Override
    public ExpansionPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> createUnintentionalExpansionPolicy(final Iterable<ExpansionPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>>> preOrderExpansionPolicies, final Iterable<ExpansionPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>>> postOrderExpansionPolicies) {
        UnintentionalExpansionPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> unintentionalExpansionPolicy = createUnintentionalExpansionPolicy(explorationHeuristic);
        ExpansionPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> expansionPolicy = mergeExpansionPolicies(preOrderExpansionPolicies, unintentionalExpansionPolicy, postOrderExpansionPolicies);

        return new ConcurrentExpansionPolicy<>(expansionPolicy, true);
    }

    @Override
    protected TraversalPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> createUnexploredPrimerTraversalPolicy() {
        return ConcurrentUnexploredPrimerTraversalPolicy.getInstance();
    }

    @Override
    protected SelectionPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> createSelectionPolicy(final TraversalPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> priorityTraversalPolicy, final TraversalPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> subsequentTraversalPolicy, final ExpansionPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> expansionPolicy) {
        return new ConcurrentSelectionPolicy<>(priorityTraversalPolicy, subsequentTraversalPolicy, expansionPolicy);
    }

    @Override
    protected TraversalPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> createIntentionalSimulationTraversalPolicy() {
        RandomSupport randomSupport = createRandomSupport();

        return new ConcurrentIntentionalSimulationTraversalPolicy<>(randomSupport);
    }

    @Override
    protected SimulationRolloutPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> createSimulationRolloutPolicy(final TraversalPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> traversalPolicy, final ExpansionPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> expansionPolicy) {
        return new ConcurrentSimulationRolloutPolicy<>(searchPolicy, traversalPolicy, expansionPolicy);
    }

    @Override
    public <TContext> BackPropagationPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> createBackPropagationPolicy(final BackPropagationStep<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>, TContext> step, final BackPropagationObserver<TAction, TState> observer) {
        return new ConcurrentBackPropagationPolicy<>(ConcurrentSearchNodeManager.getInstance(), step, observer);
    }

    @Override
    public SeekStrategy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> createSearchStrategy(final SelectionPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> selectionPolicy, final SimulationRolloutPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> simulationRolloutPolicy, final BackPropagationPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> backPropagationPolicy) {
        return new ConcurrentSeekStrategy<>(eventLoop, searchPolicy, selectionPolicy, simulationRolloutPolicy, backPropagationPolicy);
    }
}
