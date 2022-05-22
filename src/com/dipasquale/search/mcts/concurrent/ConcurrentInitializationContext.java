package com.dipasquale.search.mcts.concurrent;

import com.dipasquale.common.factory.data.structure.map.MapFactory;
import com.dipasquale.common.random.float1.RandomSupport;
import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.BackPropagationObserver;
import com.dipasquale.search.mcts.BackPropagationPolicy;
import com.dipasquale.search.mcts.BackPropagationStep;
import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.EdgeFactory;
import com.dipasquale.search.mcts.ExpansionPolicy;
import com.dipasquale.search.mcts.SearchNodeGroupProvider;
import com.dipasquale.search.mcts.SearchStrategy;
import com.dipasquale.search.mcts.SelectionConfidenceCalculator;
import com.dipasquale.search.mcts.SelectionPolicy;
import com.dipasquale.search.mcts.SimulationRolloutPolicy;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.TraversalPolicy;
import com.dipasquale.search.mcts.common.AbstractInitializationContext;
import com.dipasquale.search.mcts.common.CommonSelectionPolicyFactory;
import com.dipasquale.search.mcts.common.CommonSimulationRolloutPolicyFactory;
import com.dipasquale.search.mcts.common.ExplorationHeuristic;
import com.dipasquale.search.mcts.common.FullSearchPolicy;
import com.dipasquale.search.mcts.common.IntentionalExpansionPolicy;
import com.dipasquale.search.mcts.common.SelectionType;
import com.dipasquale.search.mcts.common.UnintentionalExpansionPolicy;
import com.dipasquale.search.mcts.common.concurrent.ConcurrentExpansionPolicy;
import com.dipasquale.search.mcts.common.concurrent.ConcurrentIntentionalSelectionTraversalPolicy;
import com.dipasquale.search.mcts.common.concurrent.ConcurrentIntentionalSimulationRolloutTraversalPolicy;
import com.dipasquale.synchronization.event.loop.ParallelEventLoop;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ConcurrentInitializationContext<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>, TBackPropagationContext> extends AbstractInitializationContext<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>, TBackPropagationContext> {
    private final ParallelEventLoop eventLoop;
    @Getter
    private final MapFactory mapFactory;
    @Getter
    private final EdgeFactory<TEdge> edgeFactory;
    @Getter
    private final ConcurrentSearchNodeFactory<TAction, TEdge, TState> searchNodeFactory;
    private final ExplorationHeuristic<TAction> explorationHeuristic;
    private final SelectionType selectionType;
    private final FullSearchPolicy searchPolicy;

    public ConcurrentInitializationContext(final ParallelEventLoop eventLoop, final EdgeFactory<TEdge> edgeFactory, final ExplorationHeuristic<TAction> explorationHeuristic, final FullSearchPolicy searchPolicy) {
        this.eventLoop = eventLoop;
        this.mapFactory = new ConcurrentHashMapFactory(eventLoop.getConcurrencyLevel());
        this.edgeFactory = edgeFactory;
        this.searchNodeFactory = new ConcurrentSearchNodeFactory<>(eventLoop.getConcurrencyLevel());
        this.explorationHeuristic = explorationHeuristic;
        this.selectionType = SelectionType.determine(explorationHeuristic);
        this.searchPolicy = searchPolicy;
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
    public SelectionPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> createSelectionPolicy(final SelectionConfidenceCalculator<TEdge> selectionConfidenceCalculator, final ExpansionPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> expansionPolicy) {
        RandomSupport randomSupport = createRandomSupport();
        TraversalPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> intentionalTraversalPolicy = new ConcurrentIntentionalSelectionTraversalPolicy<>(selectionConfidenceCalculator);
        CommonSelectionPolicyFactory<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> selectionPolicyFactory = new CommonSelectionPolicyFactory<>(selectionType, randomSupport, intentionalTraversalPolicy, expansionPolicy);

        return selectionPolicyFactory.create();
    }

    @Override
    public SimulationRolloutPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> createSimulationRolloutPolicy(final ExpansionPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> expansionPolicy) {
        RandomSupport randomSupport = createRandomSupport();
        TraversalPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> intentionalTraversalPolicy = new ConcurrentIntentionalSimulationRolloutTraversalPolicy<>(randomSupport);
        CommonSimulationRolloutPolicyFactory<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> simulationRolloutPolicyFactory = new CommonSimulationRolloutPolicyFactory<>(searchPolicy, selectionType, randomSupport, intentionalTraversalPolicy, expansionPolicy);

        return simulationRolloutPolicyFactory.create();
    }

    @Override
    public BackPropagationPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>, TBackPropagationContext> createBackPropagationPolicy(final BackPropagationStep<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>, TBackPropagationContext> step, final BackPropagationObserver<TAction, TState> observer) {
        return null;
    }

    @Override
    public SearchStrategy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> createSearchStrategy(final SelectionPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> selectionPolicy, final SimulationRolloutPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>> simulationRolloutPolicy, final BackPropagationPolicy<TAction, TEdge, TState, ConcurrentSearchNode<TAction, TEdge, TState>, TBackPropagationContext> backPropagationPolicy) {
        return new ConcurrentSearchStrategy<>(eventLoop, searchPolicy, selectionPolicy, simulationRolloutPolicy, backPropagationPolicy);
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
