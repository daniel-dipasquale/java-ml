package com.dipasquale.search.mcts.alphazero;

import com.dipasquale.common.factory.data.structure.map.MapFactory;
import com.dipasquale.common.random.float1.RandomSupport;
import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.BackPropagationObserver;
import com.dipasquale.search.mcts.BackPropagationPolicy;
import com.dipasquale.search.mcts.BackPropagationStep;
import com.dipasquale.search.mcts.CommonSearchStrategy;
import com.dipasquale.search.mcts.EdgeFactory;
import com.dipasquale.search.mcts.ExpansionPolicy;
import com.dipasquale.search.mcts.InitializationContext;
import com.dipasquale.search.mcts.SearchNodeFactory;
import com.dipasquale.search.mcts.SearchNodeGroupProvider;
import com.dipasquale.search.mcts.SearchPolicy;
import com.dipasquale.search.mcts.SearchStrategy;
import com.dipasquale.search.mcts.SelectionConfidenceCalculator;
import com.dipasquale.search.mcts.SelectionPolicy;
import com.dipasquale.search.mcts.SimulationRolloutPolicy;
import com.dipasquale.search.mcts.StandardInitializationContext;
import com.dipasquale.search.mcts.StandardSearchNode;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.TraversalPolicy;
import com.dipasquale.search.mcts.common.StandardIntentionalSelectionTraversalPolicy;

public final class AlphaZeroStandardInitializationContext<TAction extends Action, TState extends State<TAction, TState>, TBackPropagationContext> implements InitializationContext<TAction, AlphaZeroEdge, TState, StandardSearchNode<TAction, AlphaZeroEdge, TState>, TBackPropagationContext> {
    private final StandardInitializationContext<TAction, AlphaZeroEdge, TState, TBackPropagationContext> standardInitializationContext;
    private final AlphaZeroModel<TAction, TState, StandardSearchNode<TAction, AlphaZeroEdge, TState>> traversalModel;
    private final SearchPolicy searchPolicy;

    public AlphaZeroStandardInitializationContext(final EdgeFactory<AlphaZeroEdge> edgeFactory, final AlphaZeroModel<TAction, TState, StandardSearchNode<TAction, AlphaZeroEdge, TState>> traversalModel, final SearchPolicy searchPolicy) {
        this.standardInitializationContext = new StandardInitializationContext<>(edgeFactory, null, null);
        this.traversalModel = traversalModel;
        this.searchPolicy = searchPolicy;
    }

    @Override
    public RandomSupport createRandomSupport() {
        return standardInitializationContext.createRandomSupport();
    }

    @Override
    public MapFactory getMapFactory() {
        return standardInitializationContext.getMapFactory();
    }

    @Override
    public EdgeFactory<AlphaZeroEdge> getEdgeFactory() {
        return standardInitializationContext.getEdgeFactory();
    }

    @Override
    public SearchNodeFactory<TAction, AlphaZeroEdge, TState, StandardSearchNode<TAction, AlphaZeroEdge, TState>> getSearchNodeFactory() {
        return standardInitializationContext.getSearchNodeFactory();
    }

    @Override
    public SearchNodeGroupProvider<TAction, AlphaZeroEdge, TState, StandardSearchNode<TAction, AlphaZeroEdge, TState>> getSearchNodeGroupProvider() {
        return standardInitializationContext.getSearchNodeGroupProvider();
    }

    @Override
    public ExpansionPolicy<TAction, AlphaZeroEdge, TState, StandardSearchNode<TAction, AlphaZeroEdge, TState>> createIntentionalExpansionPolicy(final Iterable<ExpansionPolicy<TAction, AlphaZeroEdge, TState, StandardSearchNode<TAction, AlphaZeroEdge, TState>>> preOrderExpansionPolicies, final Iterable<ExpansionPolicy<TAction, AlphaZeroEdge, TState, StandardSearchNode<TAction, AlphaZeroEdge, TState>>> postOrderExpansionPolicies) {
        return standardInitializationContext.createIntentionalExpansionPolicy(preOrderExpansionPolicies, postOrderExpansionPolicies);
    }

    @Override
    public ExpansionPolicy<TAction, AlphaZeroEdge, TState, StandardSearchNode<TAction, AlphaZeroEdge, TState>> createUnintentionalExpansionPolicy(final Iterable<ExpansionPolicy<TAction, AlphaZeroEdge, TState, StandardSearchNode<TAction, AlphaZeroEdge, TState>>> preOrderExpansionPolicies, final Iterable<ExpansionPolicy<TAction, AlphaZeroEdge, TState, StandardSearchNode<TAction, AlphaZeroEdge, TState>>> postOrderExpansionPolicies) {
        return standardInitializationContext.createUnintentionalExpansionPolicy(preOrderExpansionPolicies, postOrderExpansionPolicies);
    }

    @Override
    public SelectionPolicy<TAction, AlphaZeroEdge, TState, StandardSearchNode<TAction, AlphaZeroEdge, TState>> createSelectionPolicy(final SelectionConfidenceCalculator<AlphaZeroEdge> selectionConfidenceCalculator, final ExpansionPolicy<TAction, AlphaZeroEdge, TState, StandardSearchNode<TAction, AlphaZeroEdge, TState>> expansionPolicy) {
        RandomSupport randomSupport = createRandomSupport();
        TraversalPolicy<TAction, AlphaZeroEdge, TState, StandardSearchNode<TAction, AlphaZeroEdge, TState>> intentionalTraversalPolicy = new StandardIntentionalSelectionTraversalPolicy<>(selectionConfidenceCalculator);
        AlphaZeroSelectionPolicyFactory<TAction, TState, StandardSearchNode<TAction, AlphaZeroEdge, TState>> selectionPolicyFactory = new AlphaZeroSelectionPolicyFactory<>(traversalModel, intentionalTraversalPolicy, expansionPolicy, randomSupport);

        return selectionPolicyFactory.create();
    }

    @Override
    public SimulationRolloutPolicy<TAction, AlphaZeroEdge, TState, StandardSearchNode<TAction, AlphaZeroEdge, TState>> createSimulationRolloutPolicy(final ExpansionPolicy<TAction, AlphaZeroEdge, TState, StandardSearchNode<TAction, AlphaZeroEdge, TState>> expansionPolicy) {
        return AlphaZeroSimulationRolloutPolicy.getInstance();
    }

    @Override
    public BackPropagationPolicy<TAction, AlphaZeroEdge, TState, StandardSearchNode<TAction, AlphaZeroEdge, TState>, TBackPropagationContext> createBackPropagationPolicy(final BackPropagationStep<TAction, AlphaZeroEdge, TState, StandardSearchNode<TAction, AlphaZeroEdge, TState>, TBackPropagationContext> step, final BackPropagationObserver<TAction, TState> observer) {
        return standardInitializationContext.createBackPropagationPolicy(step, observer);
    }

    @Override
    public SearchStrategy<TAction, AlphaZeroEdge, TState, StandardSearchNode<TAction, AlphaZeroEdge, TState>> createSearchStrategy(final SelectionPolicy<TAction, AlphaZeroEdge, TState, StandardSearchNode<TAction, AlphaZeroEdge, TState>> selectionPolicy, final SimulationRolloutPolicy<TAction, AlphaZeroEdge, TState, StandardSearchNode<TAction, AlphaZeroEdge, TState>> simulationRolloutPolicy, final BackPropagationPolicy<TAction, AlphaZeroEdge, TState, StandardSearchNode<TAction, AlphaZeroEdge, TState>, TBackPropagationContext> backPropagationPolicy) {
        return new CommonSearchStrategy<>(searchPolicy, selectionPolicy, simulationRolloutPolicy, backPropagationPolicy);
    }
}
