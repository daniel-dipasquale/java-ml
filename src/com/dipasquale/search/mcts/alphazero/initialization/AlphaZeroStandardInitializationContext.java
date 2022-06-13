package com.dipasquale.search.mcts.alphazero.initialization;

import com.dipasquale.common.random.float1.RandomSupport;
import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.EdgeFactory;
import com.dipasquale.search.mcts.SearchNodeFactory;
import com.dipasquale.search.mcts.SearchNodeGroupProvider;
import com.dipasquale.search.mcts.StandardSearchNode;
import com.dipasquale.search.mcts.StandardSearchNodeExplorer;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.TraversalPolicy;
import com.dipasquale.search.mcts.alphazero.AlphaZeroEdge;
import com.dipasquale.search.mcts.alphazero.selection.AlphaZeroModel;
import com.dipasquale.search.mcts.alphazero.selection.AlphaZeroSelectionPolicyFactory;
import com.dipasquale.search.mcts.alphazero.simulation.AlphaZeroSimulationPolicy;
import com.dipasquale.search.mcts.expansion.ExpansionPolicy;
import com.dipasquale.search.mcts.heuristic.selection.UctAlgorithm;
import com.dipasquale.search.mcts.initialization.InitializationContext;
import com.dipasquale.search.mcts.initialization.StandardInitializationContext;
import com.dipasquale.search.mcts.intention.IntentionalTraversalPolicy;
import com.dipasquale.search.mcts.propagation.BackPropagationObserver;
import com.dipasquale.search.mcts.propagation.BackPropagationPolicy;
import com.dipasquale.search.mcts.propagation.BackPropagationStep;
import com.dipasquale.search.mcts.seek.SeekPolicy;
import com.dipasquale.search.mcts.seek.SeekStrategy;
import com.dipasquale.search.mcts.seek.StandardSeekStrategy;
import com.dipasquale.search.mcts.selection.SelectionPolicy;
import com.dipasquale.search.mcts.simulation.SimulationPolicy;

public final class AlphaZeroStandardInitializationContext<TAction extends Action, TState extends State<TAction, TState>> implements InitializationContext<TAction, AlphaZeroEdge, TState, StandardSearchNode<TAction, AlphaZeroEdge, TState>> {
    private final StandardInitializationContext<TAction, AlphaZeroEdge, TState> standardInitializationContext;
    private final AlphaZeroModel<TAction, TState, StandardSearchNode<TAction, AlphaZeroEdge, TState>> traversalModel;
    private final SeekPolicy seekPolicy;

    public AlphaZeroStandardInitializationContext(final EdgeFactory<AlphaZeroEdge> edgeFactory, final AlphaZeroModel<TAction, TState, StandardSearchNode<TAction, AlphaZeroEdge, TState>> traversalModel, final SeekPolicy seekPolicy) {
        this.standardInitializationContext = new StandardInitializationContext<>(edgeFactory, null, null);
        this.traversalModel = traversalModel;
        this.seekPolicy = seekPolicy;
    }

    @Override
    public RandomSupport createRandomSupport() {
        return standardInitializationContext.createRandomSupport();
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
    public SelectionPolicy<TAction, AlphaZeroEdge, TState, StandardSearchNode<TAction, AlphaZeroEdge, TState>> createSelectionPolicy(final UctAlgorithm<AlphaZeroEdge> uctAlgorithm, final ExpansionPolicy<TAction, AlphaZeroEdge, TState, StandardSearchNode<TAction, AlphaZeroEdge, TState>> expansionPolicy) {
        RandomSupport randomSupport = createRandomSupport();
        TraversalPolicy<TAction, AlphaZeroEdge, TState, StandardSearchNode<TAction, AlphaZeroEdge, TState>> intentionalTraversalPolicy = new IntentionalTraversalPolicy<>(uctAlgorithm);
        AlphaZeroSelectionPolicyFactory<TAction, TState, StandardSearchNode<TAction, AlphaZeroEdge, TState>> selectionPolicyFactory = new AlphaZeroSelectionPolicyFactory<>(traversalModel, intentionalTraversalPolicy, expansionPolicy, randomSupport);

        return selectionPolicyFactory.create();
    }

    @Override
    public SimulationPolicy<TAction, AlphaZeroEdge, TState, StandardSearchNode<TAction, AlphaZeroEdge, TState>> createSimulationPolicy(final ExpansionPolicy<TAction, AlphaZeroEdge, TState, StandardSearchNode<TAction, AlphaZeroEdge, TState>> expansionPolicy) {
        return AlphaZeroSimulationPolicy.getInstance();
    }

    @Override
    public <TContext> BackPropagationPolicy<TAction, AlphaZeroEdge, TState, StandardSearchNode<TAction, AlphaZeroEdge, TState>> createBackPropagationPolicy(final BackPropagationStep<TAction, AlphaZeroEdge, TState, StandardSearchNode<TAction, AlphaZeroEdge, TState>, TContext> step, final BackPropagationObserver<TAction, TState> observer) {
        return standardInitializationContext.createBackPropagationPolicy(step, observer);
    }

    @Override
    public SeekStrategy<TAction, AlphaZeroEdge, TState, StandardSearchNode<TAction, AlphaZeroEdge, TState>> createSearchStrategy(final SelectionPolicy<TAction, AlphaZeroEdge, TState, StandardSearchNode<TAction, AlphaZeroEdge, TState>> selectionPolicy, final SimulationPolicy<TAction, AlphaZeroEdge, TState, StandardSearchNode<TAction, AlphaZeroEdge, TState>> simulationPolicy, final BackPropagationPolicy<TAction, AlphaZeroEdge, TState, StandardSearchNode<TAction, AlphaZeroEdge, TState>> backPropagationPolicy) {
        return new StandardSeekStrategy<>(seekPolicy, selectionPolicy, simulationPolicy, backPropagationPolicy, StandardSearchNodeExplorer.getInstance());
    }
}
