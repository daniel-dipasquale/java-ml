package com.dipasquale.search.mcts;

import com.dipasquale.common.factory.data.structure.map.MapFactory;
import com.dipasquale.common.random.float1.RandomSupport;
import com.dipasquale.search.mcts.common.AbstractInitializationContext;
import com.dipasquale.search.mcts.common.CommonSelectionPolicyFactory;
import com.dipasquale.search.mcts.common.CommonSimulationRolloutPolicyFactory;
import com.dipasquale.search.mcts.common.ExplorationHeuristic;
import com.dipasquale.search.mcts.common.FullSearchPolicy;
import com.dipasquale.search.mcts.common.IntentionalExpansionPolicy;
import com.dipasquale.search.mcts.common.SelectionType;
import com.dipasquale.search.mcts.common.StandardIntentionalSelectionTraversalPolicy;
import com.dipasquale.search.mcts.common.StandardIntentionalSimulationRolloutTraversalPolicy;
import com.dipasquale.search.mcts.common.UnintentionalExpansionPolicy;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

public final class StandardInitializationContext<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>, TBackPropagationContext> extends AbstractInitializationContext<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>, TBackPropagationContext> {
    private static final MapFactory MAP_FACTORY = new HashMapFactory();
    @Getter
    private final EdgeFactory<TEdge> edgeFactory;
    private final ExplorationHeuristic<TAction> explorationHeuristic;
    private final SelectionType selectionType;
    private final FullSearchPolicy searchPolicy;

    public StandardInitializationContext(final EdgeFactory<TEdge> edgeFactory, final ExplorationHeuristic<TAction> explorationHeuristic, final FullSearchPolicy searchPolicy) {
        this.edgeFactory = edgeFactory;
        this.explorationHeuristic = explorationHeuristic;
        this.selectionType = SelectionType.determine(explorationHeuristic);
        this.searchPolicy = searchPolicy;
    }

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
    public SelectionPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> createSelectionPolicy(final SelectionConfidenceCalculator<TEdge> selectionConfidenceCalculator, final ExpansionPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> expansionPolicy) {
        RandomSupport randomSupport = createRandomSupport();
        TraversalPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> intentionalTraversalPolicy = new StandardIntentionalSelectionTraversalPolicy<>(selectionConfidenceCalculator);
        CommonSelectionPolicyFactory<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> selectionPolicyFactory = new CommonSelectionPolicyFactory<>(selectionType, randomSupport, intentionalTraversalPolicy, expansionPolicy);

        return selectionPolicyFactory.create();
    }

    @Override
    public SimulationRolloutPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> createSimulationRolloutPolicy(final ExpansionPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> expansionPolicy) {
        RandomSupport randomSupport = createRandomSupport();
        TraversalPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> intentionalTraversalPolicy = new StandardIntentionalSimulationRolloutTraversalPolicy<>(randomSupport);
        CommonSimulationRolloutPolicyFactory<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> simulationRolloutPolicyFactory = new CommonSimulationRolloutPolicyFactory<>(searchPolicy, selectionType, randomSupport, intentionalTraversalPolicy, expansionPolicy);

        return simulationRolloutPolicyFactory.create();
    }

    @Override
    public BackPropagationPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>, TBackPropagationContext> createBackPropagationPolicy(final BackPropagationStep<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>, TBackPropagationContext> step, final BackPropagationObserver<TAction, TState> observer) {
        return new BackPropagationPolicy<>(step, observer);
    }

    @Override
    public SearchStrategy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> createSearchStrategy(final SelectionPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> selectionPolicy, final SimulationRolloutPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> simulationRolloutPolicy, final BackPropagationPolicy<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>, TBackPropagationContext> backPropagationPolicy) {
        return new CommonSearchStrategy<>(searchPolicy, selectionPolicy, simulationRolloutPolicy, backPropagationPolicy);
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
