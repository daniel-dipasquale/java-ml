package com.dipasquale.search.mcts.initialization;

import com.dipasquale.common.random.float1.RandomSupport;
import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.EdgeFactory;
import com.dipasquale.search.mcts.SearchNodeFactory;
import com.dipasquale.search.mcts.SearchNodeGroupProvider;
import com.dipasquale.search.mcts.StandardSearchNode;
import com.dipasquale.search.mcts.StandardSearchNodeFactory;
import com.dipasquale.search.mcts.StandardSearchNodeGroupProvider;
import com.dipasquale.search.mcts.StandardSearchNodeManager;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.TraversalPolicy;
import com.dipasquale.search.mcts.buffer.GenerationTree;
import com.dipasquale.search.mcts.buffer.StandardGenerationTree;
import com.dipasquale.search.mcts.expansion.ExpansionPolicy;
import com.dipasquale.search.mcts.expansion.UnintentionalExpansionPolicy;
import com.dipasquale.search.mcts.expansion.intention.IntentionalExpansionPolicy;
import com.dipasquale.search.mcts.heuristic.intention.ExplorationHeuristic;
import com.dipasquale.search.mcts.intention.IntentionType;
import com.dipasquale.search.mcts.propagation.BackPropagationObserver;
import com.dipasquale.search.mcts.propagation.BackPropagationPolicy;
import com.dipasquale.search.mcts.propagation.BackPropagationStep;
import com.dipasquale.search.mcts.propagation.StandardBackPropagationPolicy;
import com.dipasquale.search.mcts.seek.FullSeekPolicy;
import com.dipasquale.search.mcts.seek.SeekStrategy;
import com.dipasquale.search.mcts.seek.StandardSeekStrategy;
import com.dipasquale.search.mcts.selection.SelectionPolicy;
import com.dipasquale.search.mcts.selection.StandardSelectionPolicy;
import com.dipasquale.search.mcts.selection.StandardUnexploredPrimerTraversalPolicy;
import com.dipasquale.search.mcts.simulation.SimulationRolloutPolicy;
import com.dipasquale.search.mcts.simulation.StandardIntentionalSimulationTraversalPolicy;
import com.dipasquale.search.mcts.simulation.StandardSimulationRolloutPolicy;
import lombok.Getter;

public final class StandardInitializationContext<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> extends AbstractInitializationContext<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> {
    @Getter
    private final EdgeFactory<TEdge> edgeFactory;
    private final ExplorationHeuristic<TAction> explorationHeuristic;
    private final FullSeekPolicy searchPolicy;

    private StandardInitializationContext(final EdgeFactory<TEdge> edgeFactory, final ExplorationHeuristic<TAction> explorationHeuristic, final IntentionType intentionType, final FullSeekPolicy searchPolicy) {
        super(intentionType);
        this.edgeFactory = edgeFactory;
        this.explorationHeuristic = explorationHeuristic;
        this.searchPolicy = searchPolicy;
    }

    public StandardInitializationContext(final EdgeFactory<TEdge> edgeFactory, final ExplorationHeuristic<TAction> explorationHeuristic, final FullSeekPolicy searchPolicy) {
        this(edgeFactory, explorationHeuristic, IntentionType.determine(explorationHeuristic), searchPolicy);
    }

    @Override
    public GenerationTree<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> createGenerationTree() {
        return new StandardGenerationTree<>();
    }

    @Override
    public SearchNodeFactory<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> getSearchNodeFactory() {
        return StandardSearchNodeFactory.getInstance();
    }

    @Override
    public SearchNodeGroupProvider<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> getSearchNodeGroupProvider() {
        return StandardSearchNodeGroupProvider.getInstance();
    }

    @Override
    public ExpansionPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> createIntentionalExpansionPolicy(final Iterable<ExpansionPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>>> preOrderExpansionPolicies, final Iterable<ExpansionPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>>> postOrderExpansionPolicies) {
        IntentionalExpansionPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> intentionalExpansionPolicy = createIntentionalExpansionPolicy();

        return mergeExpansionPolicies(preOrderExpansionPolicies, intentionalExpansionPolicy, postOrderExpansionPolicies);
    }

    @Override
    public ExpansionPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> createUnintentionalExpansionPolicy(final Iterable<ExpansionPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>>> preOrderExpansionPolicies, final Iterable<ExpansionPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>>> postOrderExpansionPolicies) {
        UnintentionalExpansionPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> unintentionalExpansionPolicy = createUnintentionalExpansionPolicy(explorationHeuristic);

        return mergeExpansionPolicies(preOrderExpansionPolicies, unintentionalExpansionPolicy, postOrderExpansionPolicies);
    }

    @Override
    protected TraversalPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> createUnexploredPrimerTraversalPolicy() {
        return StandardUnexploredPrimerTraversalPolicy.getInstance();
    }

    @Override
    protected SelectionPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> createSelectionPolicy(final TraversalPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> priorityTraversalPolicy, final TraversalPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> subsequentTraversalPolicy, final ExpansionPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> expansionPolicy) {
        return new StandardSelectionPolicy<>(priorityTraversalPolicy, subsequentTraversalPolicy, expansionPolicy);
    }

    @Override
    protected TraversalPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> createIntentionalSimulationTraversalPolicy() {
        RandomSupport randomSupport = createRandomSupport();

        return new StandardIntentionalSimulationTraversalPolicy<>(randomSupport);
    }

    @Override
    protected SimulationRolloutPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> createSimulationRolloutPolicy(final TraversalPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> traversalPolicy, final ExpansionPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> expansionPolicy) {
        return new StandardSimulationRolloutPolicy<>(searchPolicy, traversalPolicy, expansionPolicy);
    }

    @Override
    public <TContext> BackPropagationPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> createBackPropagationPolicy(final BackPropagationStep<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>, TContext> step, final BackPropagationObserver<TAction, TState> observer) {
        return new StandardBackPropagationPolicy<>(StandardSearchNodeManager.getInstance(), step, observer);
    }

    @Override
    public SeekStrategy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> createSearchStrategy(final SelectionPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> selectionPolicy, final SimulationRolloutPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> simulationRolloutPolicy, final BackPropagationPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> backPropagationPolicy) {
        return new StandardSeekStrategy<>(searchPolicy, selectionPolicy, simulationRolloutPolicy, backPropagationPolicy, StandardSearchNodeManager.getInstance());
    }
}
