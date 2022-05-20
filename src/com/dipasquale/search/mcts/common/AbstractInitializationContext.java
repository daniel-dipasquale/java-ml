package com.dipasquale.search.mcts.common;

import com.dipasquale.common.random.float1.RandomSupport;
import com.dipasquale.common.random.float1.UniformRandomSupport;
import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.EdgeFactory;
import com.dipasquale.search.mcts.ExpansionPolicy;
import com.dipasquale.search.mcts.ExpansionPolicyController;
import com.dipasquale.search.mcts.InitializationContext;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.SearchNodeGroupProvider;
import com.dipasquale.search.mcts.State;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractInitializationContext<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>, TBackPropagationContext> implements InitializationContext<TAction, TEdge, TState, TSearchNode, TBackPropagationContext> {
    @Override
    public RandomSupport createRandomSupport() {
        return new UniformRandomSupport();
    }

    protected IntentionalExpansionPolicy<TAction, TEdge, TState, TSearchNode> createIntentionalExpansionPolicy(final RandomSupport randomSupport) {
        EdgeFactory<TEdge> edgeFactory = getEdgeFactory();
        SearchNodeGroupProvider<TAction, TEdge, TState, TSearchNode> searchNodeGroupProvider = getSearchNodeGroupProvider();

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
}
