package com.dipasquale.search.mcts;

import com.dipasquale.common.factory.data.structure.map.MapFactory;
import com.dipasquale.common.random.float1.RandomSupport;
import com.dipasquale.search.mcts.common.AbstractInitializationContext;
import com.dipasquale.search.mcts.common.ExplorationHeuristic;
import com.dipasquale.search.mcts.common.IntentionalExpansionPolicy;
import com.dipasquale.search.mcts.common.StandardIntentionalTraversalPolicy;
import com.dipasquale.search.mcts.common.UnintentionalExpansionPolicy;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public final class StandardInitializationContext<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>, TBackPropagationContext> extends AbstractInitializationContext<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>, TBackPropagationContext> {
    private static final MapFactory MAP_FACTORY = new HashMapFactory();
    @Getter
    private final EdgeFactory<TEdge> edgeFactory;

    @Override
    public MapFactory getMapFactory() {
        return MAP_FACTORY;
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
    public TraversalPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> createIntentionalTraversalPolicy(final SelectionConfidenceCalculator<TEdge> selectionConfidenceCalculator) {
        return new StandardIntentionalTraversalPolicy<>(selectionConfidenceCalculator);
    }

    @Override
    public ExpansionPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> createIntentionalExpansionPolicy(final Iterable<ExpansionPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>>> preOrderExpansionPolicies, final Iterable<ExpansionPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>>> postOrderExpansionPolicies) {
        RandomSupport randomSupport = createRandomSupport();
        IntentionalExpansionPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> intentionalExpansionPolicy = createIntentionalExpansionPolicy(randomSupport);

        return mergeExpansionPolicies(preOrderExpansionPolicies, intentionalExpansionPolicy, postOrderExpansionPolicies);
    }

    @Override
    public ExpansionPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> createUnintentionalExpansionPolicy(final Iterable<ExpansionPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>>> preOrderExpansionPolicies, final ExplorationHeuristic<TAction> explorationHeuristic, final Iterable<ExpansionPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>>> postOrderExpansionPolicies) {
        UnintentionalExpansionPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> unintentionalExpansionPolicy = createUnintentionalExpansionPolicy(explorationHeuristic);

        return mergeExpansionPolicies(preOrderExpansionPolicies, unintentionalExpansionPolicy, postOrderExpansionPolicies);
    }

    @Override
    public SearchStrategy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> createSearchStrategy(final SearchPolicy searchPolicy, final SelectionPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> selectionPolicy, final SimulationRolloutPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> simulationRolloutPolicy, final BackPropagationPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>, TBackPropagationContext> backPropagationPolicy) {
        return new StandardSearchStrategy<>(searchPolicy, selectionPolicy, simulationRolloutPolicy, backPropagationPolicy);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class HashMapFactory implements MapFactory {
        @Override
        public <TKey, TValue> Map<TKey, TValue> create(final Map<TKey, TValue> other) {
            if (other == null) {
                return new HashMap<>();
            }

            return new HashMap<>(other);
        }
    }
}
