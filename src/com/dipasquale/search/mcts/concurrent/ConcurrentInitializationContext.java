package com.dipasquale.search.mcts.concurrent;

import com.dipasquale.common.factory.data.structure.map.MapFactory;
import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.BackPropagationPolicy;
import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.EdgeFactory;
import com.dipasquale.search.mcts.ExpansionPolicy;
import com.dipasquale.search.mcts.SearchNodeGroupProvider;
import com.dipasquale.search.mcts.SearchPolicy;
import com.dipasquale.search.mcts.SearchStrategy;
import com.dipasquale.search.mcts.SelectionConfidenceCalculator;
import com.dipasquale.search.mcts.SelectionPolicy;
import com.dipasquale.search.mcts.SimulationRolloutPolicy;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.TraversalPolicy;
import com.dipasquale.search.mcts.common.AbstractInitializationContext;
import com.dipasquale.search.mcts.common.ExplorationHeuristic;
import com.dipasquale.search.mcts.common.IntentionalExpansionPolicy;
import com.dipasquale.search.mcts.common.UnintentionalExpansionPolicy;
import com.dipasquale.search.mcts.common.concurrent.ConcurrentExpansionPolicy;
import com.dipasquale.search.mcts.common.concurrent.ConcurrentIntentionalTraversalPolicy;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public final class ConcurrentInitializationContext<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>, TBackPropagationContext> extends AbstractInitializationContext<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>, TBackPropagationContext> {
    private final MapFactory mapFactory;
    private final EdgeFactory<TEdge> edgeFactory;
    private final ConcurrentSearchNodeFactory<TAction, TEdge, TState> searchNodeFactory;

    public ConcurrentInitializationContext(final int numberOfThreads, final EdgeFactory<TEdge> edgeFactory) {
        this.mapFactory = new ConcurrentHashMapFactory(numberOfThreads);
        this.edgeFactory = edgeFactory;
        this.searchNodeFactory = new ConcurrentSearchNodeFactory<>(numberOfThreads);
    }

    @Override
    public SearchNodeGroupProvider<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> getSearchNodeGroupProvider() {
        return ConcurrentSearchNodeGroupProvider.getInstance();
    }

    @Override
    public TraversalPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> createIntentionalTraversalPolicy(final SelectionConfidenceCalculator<TEdge> selectionConfidenceCalculator) {
        return new ConcurrentIntentionalTraversalPolicy<>(selectionConfidenceCalculator);
    }

    @Override
    public ExpansionPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> createIntentionalExpansionPolicy(final Iterable<ExpansionPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>>> preOrderExpansionPolicies, final Iterable<ExpansionPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>>> postOrderExpansionPolicies) {
        IntentionalExpansionPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> intentionalExpansionPolicy = createIntentionalExpansionPolicy(null);
        ExpansionPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> expansionPolicy = mergeExpansionPolicies(preOrderExpansionPolicies, intentionalExpansionPolicy, postOrderExpansionPolicies);

        return new ConcurrentExpansionPolicy<>(expansionPolicy);
    }

    @Override
    public ExpansionPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> createUnintentionalExpansionPolicy(final Iterable<ExpansionPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>>> preOrderExpansionPolicies, final ExplorationHeuristic<TAction> explorationHeuristic, final Iterable<ExpansionPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>>> postOrderExpansionPolicies) {
        UnintentionalExpansionPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> unintentionalExpansionPolicy = createUnintentionalExpansionPolicy(explorationHeuristic);
        ExpansionPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> expansionPolicy = mergeExpansionPolicies(preOrderExpansionPolicies, unintentionalExpansionPolicy, postOrderExpansionPolicies);

        return new ConcurrentExpansionPolicy<>(expansionPolicy);
    }

    @Override
    public SearchStrategy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> createSearchStrategy(final SearchPolicy searchPolicy, final SelectionPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> selectionPolicy, final SimulationRolloutPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> simulationRolloutPolicy, final BackPropagationPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>, TBackPropagationContext> backPropagationPolicy) {
        return null;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class ConcurrentHashMapFactory implements MapFactory {
        private static final int INITIAL_CAPACITY = 16;
        private static final float LOAD_FACTOR = 0.75f;
        private final int numberOfThreads;

        @Override
        public <TKey, TValue> Map<TKey, TValue> create(final Map<TKey, TValue> other) {
            Map<TKey, TValue> map = new ConcurrentHashMap<>(INITIAL_CAPACITY, LOAD_FACTOR, numberOfThreads);

            if (other != null) {
                map.putAll(other);
            }

            return map;
        }
    }
}
