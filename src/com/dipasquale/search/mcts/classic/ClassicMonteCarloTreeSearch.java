package com.dipasquale.search.mcts.classic;

import com.dipasquale.common.random.float1.RandomSupport;
import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.BackPropagationObserver;
import com.dipasquale.search.mcts.BackPropagationPolicy;
import com.dipasquale.search.mcts.Buffer;
import com.dipasquale.search.mcts.BufferType;
import com.dipasquale.search.mcts.ExpansionPolicy;
import com.dipasquale.search.mcts.ExpansionPolicyController;
import com.dipasquale.search.mcts.InitializationContext;
import com.dipasquale.search.mcts.Mcts;
import com.dipasquale.search.mcts.MonteCarloTreeSearch;
import com.dipasquale.search.mcts.ResetHandler;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.SearchStrategy;
import com.dipasquale.search.mcts.SelectionPolicy;
import com.dipasquale.search.mcts.SimulationRolloutPolicy;
import com.dipasquale.search.mcts.StandardInitializationContext;
import com.dipasquale.search.mcts.StandardSearchNode;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.TraversalPolicy;
import com.dipasquale.search.mcts.common.CommonSelectionPolicyFactory;
import com.dipasquale.search.mcts.common.CommonSimulationRolloutPolicyFactory;
import com.dipasquale.search.mcts.common.ExplorationHeuristic;
import com.dipasquale.search.mcts.common.ExtendedSearchPolicy;
import com.dipasquale.search.mcts.common.IntentRegulatorExpansionPolicy;
import com.dipasquale.search.mcts.common.MaximumEfficiencyProposalStrategy;
import com.dipasquale.search.mcts.common.SelectionType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ClassicMonteCarloTreeSearch<TAction extends Action, TState extends State<TAction, TState>> implements MonteCarloTreeSearch<TAction, TState> {
    private static final ClassicEdgeFactory EDGE_FACTORY = ClassicEdgeFactory.getInstance();
    private static final ClassicSelectionConfidenceCalculator SELECTION_CONFIDENCE_CALCULATOR = new ClassicSelectionConfidenceCalculator();
    private static final PrevalentActionEfficiencyCalculator PREVALENT_ACTION_EFFICIENCY_CALCULATOR = PrevalentActionEfficiencyCalculator.getInstance();
    private final Mcts<TAction, ClassicEdge, TState, ?> mcts;

    private static <TAction extends Action, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, ClassicEdge, TState, TSearchNode>> ExpansionPolicy<TAction, ClassicEdge, TState, TSearchNode> createExpansionPolicy(final InitializationContext<TAction, ClassicEdge, TState, TSearchNode, ClassicBackPropagationStep.Context> initializationContext, final ExplorationHeuristic<TAction> explorationHeuristic, final Buffer<TAction, ClassicEdge, TState, TSearchNode> buffer) {
        List<ExpansionPolicy<TAction, ClassicEdge, TState, TSearchNode>> expansionPolicies = new ArrayList<>();
        ExpansionPolicy<TAction, ClassicEdge, TState, TSearchNode> intentionalExpansionPolicy = initializationContext.createIntentionalExpansionPolicy(List.of(), List.of());

        if (explorationHeuristic != null) {
            ExpansionPolicy<TAction, ClassicEdge, TState, TSearchNode> unintentionalExpansionPolicy = initializationContext.createUnintentionalExpansionPolicy(List.of(), explorationHeuristic, List.of());
            IntentRegulatorExpansionPolicy<TAction, ClassicEdge, TState, TSearchNode> intentRegulatorExpansionPolicy = new IntentRegulatorExpansionPolicy<>(intentionalExpansionPolicy, unintentionalExpansionPolicy);

            expansionPolicies.add(intentRegulatorExpansionPolicy);
        } else {
            expansionPolicies.add(intentionalExpansionPolicy);
        }

        expansionPolicies.add(buffer::store);

        return ExpansionPolicyController.provide(expansionPolicies);
    }

    private static <TAction extends Action, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, ClassicEdge, TState, TSearchNode>> SelectionPolicy<TAction, ClassicEdge, TState, TSearchNode> createSelectionPolicy(final ExplorationHeuristic<TAction> explorationHeuristic, final InitializationContext<TAction, ClassicEdge, TState, TSearchNode, ClassicBackPropagationStep.Context> initializationContext, final ExpansionPolicy<TAction, ClassicEdge, TState, TSearchNode> expansionPolicy) {
        SelectionType selectionType = SelectionType.determine(explorationHeuristic);
        RandomSupport randomSupport = initializationContext.createRandomSupport();
        TraversalPolicy<TAction, ClassicEdge, TState, TSearchNode> intentionalTraversalPolicy = initializationContext.createIntentionalTraversalPolicy(SELECTION_CONFIDENCE_CALCULATOR);
        CommonSelectionPolicyFactory<TAction, ClassicEdge, TState, TSearchNode> selectionPolicyFactory = new CommonSelectionPolicyFactory<>(selectionType, randomSupport, intentionalTraversalPolicy, expansionPolicy);

        return selectionPolicyFactory.create();
    }

    private static <TAction extends Action, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, ClassicEdge, TState, TSearchNode>> SimulationRolloutPolicy<TAction, ClassicEdge, TState, TSearchNode> createSimulationRolloutPolicy(final ExtendedSearchPolicy searchPolicy, final RandomSupport randomSupport, final ExplorationHeuristic<TAction> explorationHeuristic, final ExpansionPolicy<TAction, ClassicEdge, TState, TSearchNode> expansionPolicy) {
        return new CommonSimulationRolloutPolicyFactory<>(searchPolicy, randomSupport, SelectionType.determine(explorationHeuristic), expansionPolicy).create();
    }

    private static <TAction extends Action, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, ClassicEdge, TState, TSearchNode>> BackPropagationPolicy<TAction, ClassicEdge, TState, TSearchNode, ClassicBackPropagationStep.Context> createBackPropagationPolicy(final BackPropagationObserver<TAction, TState> backPropagationObserver) {
        return new BackPropagationPolicy<>(ClassicBackPropagationStep.<TAction, TState, TSearchNode>getInstance(), backPropagationObserver);
    }

    private static <TAction extends Action, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, ClassicEdge, TState, TSearchNode>> Mcts<TAction, ClassicEdge, TState, TSearchNode> createMcts(final InitializationContext<TAction, ClassicEdge, TState, TSearchNode, ClassicBackPropagationStep.Context> initializationContext, final ExtendedSearchPolicy searchPolicy, final BufferType bufferType, final ExplorationHeuristic<TAction> explorationHeuristic, final BackPropagationObserver<TAction, TState> backPropagationObserver) {
        BufferType fixedBufferType = Objects.requireNonNullElse(bufferType, BufferType.DISABLED);
        Buffer<TAction, ClassicEdge, TState, TSearchNode> buffer = fixedBufferType.create(initializationContext);
        ExpansionPolicy<TAction, ClassicEdge, TState, TSearchNode> expansionPolicy = createExpansionPolicy(initializationContext, explorationHeuristic, buffer);
        SelectionPolicy<TAction, ClassicEdge, TState, TSearchNode> selectionPolicy = createSelectionPolicy(explorationHeuristic, initializationContext, expansionPolicy);
        SimulationRolloutPolicy<TAction, ClassicEdge, TState, TSearchNode> simulationRolloutPolicy = createSimulationRolloutPolicy(searchPolicy, initializationContext.createRandomSupport(), explorationHeuristic, expansionPolicy);
        BackPropagationPolicy<TAction, ClassicEdge, TState, TSearchNode, ClassicBackPropagationStep.Context> backPropagationPolicy = createBackPropagationPolicy(backPropagationObserver);
        SearchStrategy<TAction, ClassicEdge, TState, TSearchNode> searchStrategy = initializationContext.createSearchStrategy(searchPolicy, selectionPolicy, simulationRolloutPolicy, backPropagationPolicy);
        MaximumEfficiencyProposalStrategy<TAction, ClassicEdge, TState, TSearchNode> proposalStrategy = new MaximumEfficiencyProposalStrategy<>(PREVALENT_ACTION_EFFICIENCY_CALCULATOR);
        List<ResetHandler> resetHandlers = ResetHandler.create(buffer);

        return new Mcts<>(buffer, searchStrategy, proposalStrategy, resetHandlers);
    }

    @Builder
    public static <TAction extends Action, TState extends State<TAction, TState>> ClassicMonteCarloTreeSearch<TAction, TState> create(final ExtendedSearchPolicy searchPolicy, final BufferType bufferType, final ExplorationHeuristic<TAction> explorationHeuristic, final BackPropagationObserver<TAction, TState> backPropagationObserver) {
        InitializationContext<TAction, ClassicEdge, TState, StandardSearchNode<TAction, ClassicEdge, TState>, ClassicBackPropagationStep.Context> initializationContext = new StandardInitializationContext<>(EDGE_FACTORY);
        Mcts<TAction, ClassicEdge, TState, StandardSearchNode<TAction, ClassicEdge, TState>> mcts = createMcts(initializationContext, searchPolicy, bufferType, explorationHeuristic, backPropagationObserver);

        return new ClassicMonteCarloTreeSearch<>(mcts);
    }

    @Override
    public TAction proposeNextAction(final TState state) {
        return mcts.proposeNextAction(state);
    }

    @Override
    public void reset() {
        mcts.reset();
    }
}
