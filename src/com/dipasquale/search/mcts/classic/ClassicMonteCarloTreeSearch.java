package com.dipasquale.search.mcts.classic;

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
import com.dipasquale.search.mcts.common.ExplorationHeuristic;
import com.dipasquale.search.mcts.common.FullSearchPolicy;
import com.dipasquale.search.mcts.common.IntentRegulatorExpansionPolicy;
import com.dipasquale.search.mcts.common.MaximumEfficiencyProposalStrategy;
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

    private static <TAction extends Action, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, ClassicEdge, TState, TSearchNode>> Buffer<TAction, ClassicEdge, TState, TSearchNode> createBuffer(final BufferType bufferType, final InitializationContext<TAction, ClassicEdge, TState, TSearchNode, ClassicBackPropagationStep.Context> initializationContext) {
        BufferType fixedBufferType = Objects.requireNonNullElse(bufferType, BufferType.DISABLED);

        return fixedBufferType.create(initializationContext);
    }

    private static <TAction extends Action, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, ClassicEdge, TState, TSearchNode>> ExpansionPolicy<TAction, ClassicEdge, TState, TSearchNode> createExpansionPolicy(final InitializationContext<TAction, ClassicEdge, TState, TSearchNode, ClassicBackPropagationStep.Context> initializationContext, final ExplorationHeuristic<TAction> explorationHeuristic, final Buffer<TAction, ClassicEdge, TState, TSearchNode> buffer) {
        List<ExpansionPolicy<TAction, ClassicEdge, TState, TSearchNode>> expansionPolicies = new ArrayList<>();
        ExpansionPolicy<TAction, ClassicEdge, TState, TSearchNode> intentionalExpansionPolicy = initializationContext.createIntentionalExpansionPolicy(List.of(), List.of());

        if (explorationHeuristic != null) {
            ExpansionPolicy<TAction, ClassicEdge, TState, TSearchNode> unintentionalExpansionPolicy = initializationContext.createUnintentionalExpansionPolicy(List.of(), List.of());
            IntentRegulatorExpansionPolicy<TAction, ClassicEdge, TState, TSearchNode> intentRegulatorExpansionPolicy = new IntentRegulatorExpansionPolicy<>(intentionalExpansionPolicy, unintentionalExpansionPolicy);

            expansionPolicies.add(intentRegulatorExpansionPolicy);
        } else {
            expansionPolicies.add(intentionalExpansionPolicy);
        }

        expansionPolicies.add(buffer::store);

        return ExpansionPolicyController.provide(expansionPolicies);
    }

    private static <TAction extends Action, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, ClassicEdge, TState, TSearchNode>> Mcts<TAction, ClassicEdge, TState, TSearchNode> createMcts(final BufferType bufferType, final InitializationContext<TAction, ClassicEdge, TState, TSearchNode, ClassicBackPropagationStep.Context> initializationContext, final ExplorationHeuristic<TAction> explorationHeuristic, final BackPropagationObserver<TAction, TState> backPropagationObserver) {
        Buffer<TAction, ClassicEdge, TState, TSearchNode> buffer = createBuffer(bufferType, initializationContext);
        ExpansionPolicy<TAction, ClassicEdge, TState, TSearchNode> expansionPolicy = createExpansionPolicy(initializationContext, explorationHeuristic, buffer);
        SelectionPolicy<TAction, ClassicEdge, TState, TSearchNode> selectionPolicy = initializationContext.createSelectionPolicy(SELECTION_CONFIDENCE_CALCULATOR, expansionPolicy);
        SimulationRolloutPolicy<TAction, ClassicEdge, TState, TSearchNode> simulationRolloutPolicy = initializationContext.createSimulationRolloutPolicy(expansionPolicy);
        BackPropagationPolicy<TAction, ClassicEdge, TState, TSearchNode, ClassicBackPropagationStep.Context> backPropagationPolicy = initializationContext.createBackPropagationPolicy(ClassicBackPropagationStep.getInstance(), backPropagationObserver);
        SearchStrategy<TAction, ClassicEdge, TState, TSearchNode> searchStrategy = initializationContext.createSearchStrategy(selectionPolicy, simulationRolloutPolicy, backPropagationPolicy);
        MaximumEfficiencyProposalStrategy<TAction, ClassicEdge, TState, TSearchNode> proposalStrategy = new MaximumEfficiencyProposalStrategy<>(PREVALENT_ACTION_EFFICIENCY_CALCULATOR);
        List<ResetHandler> resetHandlers = ResetHandler.create(buffer);

        return new Mcts<>(buffer, searchStrategy, proposalStrategy, resetHandlers);
    }

    @Builder
    public static <TAction extends Action, TState extends State<TAction, TState>> ClassicMonteCarloTreeSearch<TAction, TState> create(final FullSearchPolicy searchPolicy, final ExplorationHeuristic<TAction> explorationHeuristic, final BufferType bufferType, final BackPropagationObserver<TAction, TState> backPropagationObserver) {
        InitializationContext<TAction, ClassicEdge, TState, StandardSearchNode<TAction, ClassicEdge, TState>, ClassicBackPropagationStep.Context> initializationContext = new StandardInitializationContext<>(EDGE_FACTORY, explorationHeuristic, searchPolicy);
        Mcts<TAction, ClassicEdge, TState, StandardSearchNode<TAction, ClassicEdge, TState>> mcts = createMcts(bufferType, initializationContext, explorationHeuristic, backPropagationObserver);

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
