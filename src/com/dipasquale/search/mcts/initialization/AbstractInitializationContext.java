package com.dipasquale.search.mcts.initialization;

import com.dipasquale.common.random.float1.RandomSupport;
import com.dipasquale.common.random.float1.UniformRandomSupport;
import com.dipasquale.search.mcts.Action;
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
import com.dipasquale.search.mcts.selection.FirstValidTraversalPolicy;
import com.dipasquale.search.mcts.selection.SelectionPolicy;
import com.dipasquale.search.mcts.simulation.SimulationPolicy;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractInitializationContext<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> implements InitializationContext<TAction, TEdge, TState, TSearchNode> {
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

    private static <TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> ExpansionPolicy<TAction, TEdge, TState, TSearchNode> mergeExpansionPolicies(final Iterable<ExpansionPolicy<TAction, TEdge, TState, TSearchNode>> preOrderExpansionPolicies, final ExpansionPolicy<TAction, TEdge, TState, TSearchNode> expansionPolicy, final Iterable<ExpansionPolicy<TAction, TEdge, TState, TSearchNode>> postOrderExpansionPolicies) {
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

    protected abstract TraversalPolicy<TAction, TEdge, TState, TSearchNode> createExpansionTraversalPolicy(ExpansionPolicy<TAction, TEdge, TState, TSearchNode> expansionPolicy);

    protected abstract TraversalPolicy<TAction, TEdge, TState, TSearchNode> createUnexploredPrimerTraversalPolicy();

    private FirstValidTraversalPolicy<TAction, TEdge, TState, TSearchNode> createUnexploredPrimerTraversalPolicy(final ExpansionPolicy<TAction, TEdge, TState, TSearchNode> expansionPolicy) {
        List<TraversalPolicy<TAction, TEdge, TState, TSearchNode>> traversalPolicies = new ArrayList<>();

        traversalPolicies.add(createExpansionTraversalPolicy(expansionPolicy));
        traversalPolicies.add(createUnexploredPrimerTraversalPolicy());

        return new FirstValidTraversalPolicy<>(traversalPolicies);
    }

    protected abstract SelectionPolicy<TAction, TEdge, TState, TSearchNode> createSelectionPolicy(TraversalPolicy<TAction, TEdge, TState, TSearchNode> unexploredPrimerTraversalPolicy, TraversalPolicy<TAction, TEdge, TState, TSearchNode> explorableTraversalPolicy, ExpansionPolicy<TAction, TEdge, TState, TSearchNode> expansionPolicy);

    @Override
    public SelectionPolicy<TAction, TEdge, TState, TSearchNode> createSelectionPolicy(final UctAlgorithm<TEdge> uctAlgorithm, final ExpansionPolicy<TAction, TEdge, TState, TSearchNode> expansionPolicy) {
        TraversalPolicy<TAction, TEdge, TState, TSearchNode> unexploredPrimerTraversalPolicy = createUnexploredPrimerTraversalPolicy(expansionPolicy);
        TraversalPolicy<TAction, TEdge, TState, TSearchNode> intentionalTraversalPolicy = new IntentionalTraversalPolicy<>(uctAlgorithm);
        TraversalPolicy<TAction, TEdge, TState, TSearchNode> explorableTraversalPolicy = intentionType.createTraversalPolicy(this, intentionalTraversalPolicy);

        return createSelectionPolicy(unexploredPrimerTraversalPolicy, explorableTraversalPolicy, expansionPolicy);
    }

    protected abstract TraversalPolicy<TAction, TEdge, TState, TSearchNode> createIntentionalSimulationTraversalPolicy();

    protected abstract SimulationPolicy<TAction, TEdge, TState, TSearchNode> createSimulationPolicy(TraversalPolicy<TAction, TEdge, TState, TSearchNode> traversalPolicy, ExpansionPolicy<TAction, TEdge, TState, TSearchNode> expansionPolicy);

    @Override
    public SimulationPolicy<TAction, TEdge, TState, TSearchNode> createSimulationPolicy(final ExpansionPolicy<TAction, TEdge, TState, TSearchNode> expansionPolicy) {
        TraversalPolicy<TAction, TEdge, TState, TSearchNode> intentionalTraversalPolicy = createIntentionalSimulationTraversalPolicy();
        TraversalPolicy<TAction, TEdge, TState, TSearchNode> traversalPolicy = intentionType.createTraversalPolicy(this, intentionalTraversalPolicy);

        return createSimulationPolicy(traversalPolicy, expansionPolicy);
    }
}
