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
import com.dipasquale.search.mcts.expansion.ExpansionTraversalPolicy;
import com.dipasquale.search.mcts.expansion.UnintentionalExpansionPolicy;
import com.dipasquale.search.mcts.expansion.intention.IntentionalExpansionPolicy;
import com.dipasquale.search.mcts.heuristic.intention.ExplorationHeuristic;
import com.dipasquale.search.mcts.heuristic.selection.UctAlgorithm;
import com.dipasquale.search.mcts.intention.IntentionType;
import com.dipasquale.search.mcts.intention.IntentionalTraversalPolicy;
import com.dipasquale.search.mcts.selection.FirstFoundTraversalPolicy;
import com.dipasquale.search.mcts.selection.SelectionPolicy;
import com.dipasquale.search.mcts.simulation.SimulationRolloutPolicy;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractInitializationContext<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> implements InitializationContext<TAction, TEdge, TState, TSearchNode> {
    private final IntentionType intentionType;

    @Override
    public RandomSupport createRandomSupport() {
        return new UniformRandomSupport();
    }

    protected IntentionalExpansionPolicy<TAction, TEdge, TState, TSearchNode> createIntentionalExpansionPolicy() {
        EdgeFactory<TEdge> edgeFactory = getEdgeFactory();
        SearchNodeGroupProvider<TAction, TEdge, TState, TSearchNode> searchNodeGroupProvider = getSearchNodeGroupProvider();
        RandomSupport randomSupport = createRandomSupport();

        return new IntentionalExpansionPolicy<>(edgeFactory, searchNodeGroupProvider, randomSupport);
    }

    protected UnintentionalExpansionPolicy<TAction, TEdge, TState, TSearchNode> createUnintentionalExpansionPolicy(final ExplorationHeuristic<TAction> explorationHeuristic) {
        EdgeFactory<TEdge> edgeFactory = getEdgeFactory();
        SearchNodeGroupProvider<TAction, TEdge, TState, TSearchNode> searchNodeGroupProvider = getSearchNodeGroupProvider();

        return new UnintentionalExpansionPolicy<>(edgeFactory, searchNodeGroupProvider, explorationHeuristic);
    }

    protected ExpansionPolicy<TAction, TEdge, TState, TSearchNode> mergeExpansionPolicies(final Iterable<ExpansionPolicy<TAction, TEdge, TState, TSearchNode>> preOrderExpansionPolicies, final ExpansionPolicy<TAction, TEdge, TState, TSearchNode> inOrderExpansionPolicy, final Iterable<ExpansionPolicy<TAction, TEdge, TState, TSearchNode>> postOrderExpansionPolicies) {
        List<ExpansionPolicy<TAction, TEdge, TState, TSearchNode>> expansionPolicies = new ArrayList<>();

        for (ExpansionPolicy<TAction, TEdge, TState, TSearchNode> preOrderExpansionPolicy : preOrderExpansionPolicies) {
            expansionPolicies.add(preOrderExpansionPolicy);
        }

        expansionPolicies.add(inOrderExpansionPolicy);

        for (ExpansionPolicy<TAction, TEdge, TState, TSearchNode> postOrderExpansionPolicy : postOrderExpansionPolicies) {
            expansionPolicies.add(postOrderExpansionPolicy);
        }

        return ExpansionPolicyController.provide(expansionPolicies);
    }

    protected abstract TraversalPolicy<TAction, TEdge, TState, TSearchNode> createUnexploredPrimerTraversalPolicy();

    private FirstFoundTraversalPolicy<TAction, TEdge, TState, TSearchNode> createPriorityTraversalPolicy(final ExpansionPolicy<TAction, TEdge, TState, TSearchNode> expansionPolicy) {
        List<TraversalPolicy<TAction, TEdge, TState, TSearchNode>> traversalPolicies = new ArrayList<>();

        traversalPolicies.add(new ExpansionTraversalPolicy<>(expansionPolicy));
        traversalPolicies.add(createUnexploredPrimerTraversalPolicy());

        return new FirstFoundTraversalPolicy<>(traversalPolicies);
    }

    protected abstract SelectionPolicy<TAction, TEdge, TState, TSearchNode> createSelectionPolicy(TraversalPolicy<TAction, TEdge, TState, TSearchNode> priorityTraversalPolicy, TraversalPolicy<TAction, TEdge, TState, TSearchNode> subsequentTraversalPolicy, ExpansionPolicy<TAction, TEdge, TState, TSearchNode> expansionPolicy);

    @Override
    public SelectionPolicy<TAction, TEdge, TState, TSearchNode> createSelectionPolicy(final UctAlgorithm<TEdge> uctAlgorithm, final ExpansionPolicy<TAction, TEdge, TState, TSearchNode> expansionPolicy) {
        TraversalPolicy<TAction, TEdge, TState, TSearchNode> priorityTraversalPolicy = createPriorityTraversalPolicy(expansionPolicy);
        TraversalPolicy<TAction, TEdge, TState, TSearchNode> intentionalTraversalPolicy = new IntentionalTraversalPolicy<>(uctAlgorithm);
        TraversalPolicy<TAction, TEdge, TState, TSearchNode> subsequentTraversalPolicy = intentionType.createTraversalPolicy(this, intentionalTraversalPolicy);

        return createSelectionPolicy(priorityTraversalPolicy, subsequentTraversalPolicy, expansionPolicy);
    }

    protected abstract TraversalPolicy<TAction, TEdge, TState, TSearchNode> createIntentionalSimulationTraversalPolicy();

    protected abstract SimulationRolloutPolicy<TAction, TEdge, TState, TSearchNode> createSimulationRolloutPolicy(TraversalPolicy<TAction, TEdge, TState, TSearchNode> traversalPolicy, ExpansionPolicy<TAction, TEdge, TState, TSearchNode> expansionPolicy);

    @Override
    public SimulationRolloutPolicy<TAction, TEdge, TState, TSearchNode> createSimulationRolloutPolicy(final ExpansionPolicy<TAction, TEdge, TState, TSearchNode> expansionPolicy) {
        TraversalPolicy<TAction, TEdge, TState, TSearchNode> intentionalTraversalPolicy = createIntentionalSimulationTraversalPolicy();
        TraversalPolicy<TAction, TEdge, TState, TSearchNode> traversalPolicy = intentionType.createTraversalPolicy(this, intentionalTraversalPolicy);

        return createSimulationRolloutPolicy(traversalPolicy, expansionPolicy);
    }
}
