package com.dipasquale.search.mcts.initialization;

import com.dipasquale.common.random.RandomSupport;
import com.dipasquale.common.random.UniformRandomSupport;
import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.EdgeFactory;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.SearchNodeGroupProvider;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.TraversalPolicy;
import com.dipasquale.search.mcts.expansion.ExpansionPolicy;
import com.dipasquale.search.mcts.expansion.ExpansionPolicyController;
import com.dipasquale.search.mcts.expansion.intention.IntentionalExpansionPolicy;
import com.dipasquale.search.mcts.expansion.intention.UnintentionalExpansionPolicy;
import com.dipasquale.search.mcts.heuristic.intention.ExplorationHeuristic;
import com.dipasquale.search.mcts.heuristic.selection.UctAlgorithm;
import com.dipasquale.search.mcts.intention.IntentionType;
import com.dipasquale.search.mcts.intention.IntentionalTraversalPolicy;
import com.dipasquale.search.mcts.selection.SelectionPolicy;
import com.dipasquale.search.mcts.simulation.SimulationPolicy;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractInitializationContext<TAction, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> implements InitializationContext<TAction, TEdge, TState, TSearchNode> {
    private final IntentionType intentionType;
    private final ExplorationHeuristic<TAction> explorationHeuristic;

    @Override
    public RandomSupport createRandomSupport() {
        return new UniformRandomSupport();
    }

    private IntentionalExpansionPolicy<TAction, TEdge, TState, TSearchNode> createIntentionalExpansionPolicy() {
        EdgeFactory<TEdge> edgeFactory = getSearchNodeFactory().getEdgeFactory();
        SearchNodeGroupProvider<TAction, TEdge, TState, TSearchNode> searchNodeGroupProvider = getSearchNodeGroupProvider();
        RandomSupport randomSupport = createRandomSupport();

        return new IntentionalExpansionPolicy<>(edgeFactory, searchNodeGroupProvider, randomSupport);
    }

    private UnintentionalExpansionPolicy<TAction, TEdge, TState, TSearchNode> createUnintentionalExpansionPolicy() {
        EdgeFactory<TEdge> edgeFactory = getSearchNodeFactory().getEdgeFactory();
        SearchNodeGroupProvider<TAction, TEdge, TState, TSearchNode> searchNodeGroupProvider = getSearchNodeGroupProvider();

        return new UnintentionalExpansionPolicy<>(edgeFactory, searchNodeGroupProvider, explorationHeuristic);
    }

    private static <TAction, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> ExpansionPolicy<TAction, TEdge, TState, TSearchNode> mergeExpansionPolicies(final Iterable<ExpansionPolicy<TAction, TEdge, TState, TSearchNode>> preOrderExpansionPolicies, final ExpansionPolicy<TAction, TEdge, TState, TSearchNode> expansionPolicy, final Iterable<ExpansionPolicy<TAction, TEdge, TState, TSearchNode>> postOrderExpansionPolicies) {
        List<ExpansionPolicy<TAction, TEdge, TState, TSearchNode>> expansionPolicies = new ArrayList<>();

        for (ExpansionPolicy<TAction, TEdge, TState, TSearchNode> preOrderExpansionPolicy : preOrderExpansionPolicies) {
            expansionPolicies.add(preOrderExpansionPolicy);
        }

        expansionPolicies.add(expansionPolicy);

        for (ExpansionPolicy<TAction, TEdge, TState, TSearchNode> postOrderExpansionPolicy : postOrderExpansionPolicies) {
            expansionPolicies.add(postOrderExpansionPolicy);
        }

        return ExpansionPolicyController.provide(expansionPolicies);
    }

    @Override
    public ExpansionPolicy<TAction, TEdge, TState, TSearchNode> createIntentionalExpansionPolicy(final Iterable<ExpansionPolicy<TAction, TEdge, TState, TSearchNode>> preOrderExpansionPolicies, final Iterable<ExpansionPolicy<TAction, TEdge, TState, TSearchNode>> postOrderExpansionPolicies) {
        IntentionalExpansionPolicy<TAction, TEdge, TState, TSearchNode> intentionalExpansionPolicy = createIntentionalExpansionPolicy();

        return mergeExpansionPolicies(preOrderExpansionPolicies, intentionalExpansionPolicy, postOrderExpansionPolicies);
    }

    @Override
    public ExpansionPolicy<TAction, TEdge, TState, TSearchNode> createUnintentionalExpansionPolicy(final Iterable<ExpansionPolicy<TAction, TEdge, TState, TSearchNode>> preOrderExpansionPolicies, final Iterable<ExpansionPolicy<TAction, TEdge, TState, TSearchNode>> postOrderExpansionPolicies) {
        UnintentionalExpansionPolicy<TAction, TEdge, TState, TSearchNode> unintentionalExpansionPolicy = createUnintentionalExpansionPolicy();

        return mergeExpansionPolicies(preOrderExpansionPolicies, unintentionalExpansionPolicy, postOrderExpansionPolicies);
    }

    protected abstract TraversalPolicy<TAction, TEdge, TState, TSearchNode> createUnexploredPrimerTraversalPolicy(ExpansionPolicy<TAction, TEdge, TState, TSearchNode> expansionPolicy);

    protected abstract ExpansionPolicy<TAction, TEdge, TState, TSearchNode> createOptionalExpansionPolicy(ExpansionPolicy<TAction, TEdge, TState, TSearchNode> expansionPolicy);

    protected abstract SelectionPolicy<TAction, TEdge, TState, TSearchNode> createSelectionPolicy(ExpansionPolicy<TAction, TEdge, TState, TSearchNode> optionalExpansionPolicy, TraversalPolicy<TAction, TEdge, TState, TSearchNode> unexploredPrimerTraversalPolicy, TraversalPolicy<TAction, TEdge, TState, TSearchNode> explorableTraversalPolicy);

    @Override
    public SelectionPolicy<TAction, TEdge, TState, TSearchNode> createSelectionPolicy(final UctAlgorithm<TEdge> uctAlgorithm, final ExpansionPolicy<TAction, TEdge, TState, TSearchNode> expansionPolicy) {
        TraversalPolicy<TAction, TEdge, TState, TSearchNode> unexploredPrimerTraversalPolicy = createUnexploredPrimerTraversalPolicy(expansionPolicy);
        TraversalPolicy<TAction, TEdge, TState, TSearchNode> intentionalTraversalPolicy = new IntentionalTraversalPolicy<>(uctAlgorithm);
        ExpansionPolicy<TAction, TEdge, TState, TSearchNode> optionalExpansionPolicy = createOptionalExpansionPolicy(expansionPolicy);
        TraversalPolicy<TAction, TEdge, TState, TSearchNode> explorableTraversalPolicy = intentionType.createTraversalPolicy(this, intentionalTraversalPolicy, optionalExpansionPolicy);

        return createSelectionPolicy(optionalExpansionPolicy, unexploredPrimerTraversalPolicy, explorableTraversalPolicy);
    }

    protected abstract SimulationPolicy<TAction, TEdge, TState, TSearchNode> createSimulationPolicy(TraversalPolicy<TAction, TEdge, TState, TSearchNode> traversalPolicy);

    @Override
    public SimulationPolicy<TAction, TEdge, TState, TSearchNode> createSimulationPolicy(final ExpansionPolicy<TAction, TEdge, TState, TSearchNode> expansionPolicy) {
        TraversalPolicy<TAction, TEdge, TState, TSearchNode> unexploredPrimerTraversalPolicy = createUnexploredPrimerTraversalPolicy(expansionPolicy);
        ExpansionPolicy<TAction, TEdge, TState, TSearchNode> optionalExpansionPolicy = createOptionalExpansionPolicy(expansionPolicy);
        TraversalPolicy<TAction, TEdge, TState, TSearchNode> traversalPolicy = intentionType.createTraversalPolicy(this, unexploredPrimerTraversalPolicy, optionalExpansionPolicy);

        return createSimulationPolicy(traversalPolicy);
    }
}
