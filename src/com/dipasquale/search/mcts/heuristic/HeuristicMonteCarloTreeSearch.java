package com.dipasquale.search.mcts.heuristic;

import com.dipasquale.common.random.float1.RandomSupport;
import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.BackPropagationObserver;
import com.dipasquale.search.mcts.BackPropagationPolicy;
import com.dipasquale.search.mcts.Buffer;
import com.dipasquale.search.mcts.BufferType;
import com.dipasquale.search.mcts.EdgeFactory;
import com.dipasquale.search.mcts.ExpansionPolicy;
import com.dipasquale.search.mcts.ExpansionPolicyController;
import com.dipasquale.search.mcts.Mcts;
import com.dipasquale.search.mcts.MctsInitializationContext;
import com.dipasquale.search.mcts.MonteCarloTreeSearch;
import com.dipasquale.search.mcts.ResetHandler;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.SearchStrategy;
import com.dipasquale.search.mcts.SelectionPolicy;
import com.dipasquale.search.mcts.SimulationRolloutPolicy;
import com.dipasquale.search.mcts.StandardMctsInitializationContext;
import com.dipasquale.search.mcts.StandardSearchNode;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.alphazero.BackPropagationType;
import com.dipasquale.search.mcts.common.CPuctCalculator;
import com.dipasquale.search.mcts.common.CommonSelectionPolicyFactory;
import com.dipasquale.search.mcts.common.CommonSimulationRolloutPolicyFactory;
import com.dipasquale.search.mcts.common.ExplorationHeuristic;
import com.dipasquale.search.mcts.common.ExtendedSearchPolicy;
import com.dipasquale.search.mcts.common.IntentRegulatorExpansionPolicy;
import com.dipasquale.search.mcts.common.IntentionalExpansionPolicy;
import com.dipasquale.search.mcts.common.MaximumEfficiencyProposalStrategy;
import com.dipasquale.search.mcts.common.RewardHeuristic;
import com.dipasquale.search.mcts.common.RosinCPuctCalculator;
import com.dipasquale.search.mcts.common.SelectionType;
import com.dipasquale.search.mcts.common.TechniqueBackPropagationStep;
import com.dipasquale.search.mcts.common.TechniqueSelectionConfidenceCalculator;
import com.dipasquale.search.mcts.common.UnintentionalExpansionPolicy;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class HeuristicMonteCarloTreeSearch<TAction extends Action, TState extends State<TAction, TState>> implements MonteCarloTreeSearch<TAction, TState> {
    private static final HeuristicEdgeFactory EDGE_FACTORY = HeuristicEdgeFactory.getInstance();
    private static final RosinCPuctCalculator ROSIN_C_PUCT_ALGORITHM = new RosinCPuctCalculator();
    private static final TechniqueSelectionConfidenceCalculator<HeuristicEdge> SELECTION_CONFIDENCE_CALCULATOR = new TechniqueSelectionConfidenceCalculator<>(ROSIN_C_PUCT_ALGORITHM);
    private static final ExpectedRewardActionEfficiencyCalculator EXPECTED_REWARD_ACTION_EFFICIENCY_CALCULATOR = ExpectedRewardActionEfficiencyCalculator.getInstance();
    private final Mcts<TAction, HeuristicEdge, TState, ?> mcts;

    private static <TAction extends Action, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, HeuristicEdge, TState, TSearchNode>> ExpansionPolicy<TAction, HeuristicEdge, TState, TSearchNode> createIntentionalExpansionPolicy(final ProbableRewardExpansionPolicy<TAction, TState, TSearchNode> probableRewardExpansionPolicy, final EdgeFactory<HeuristicEdge> edgeFactory, final RandomSupport randomSupport) {
        IntentionalExpansionPolicy<TAction, HeuristicEdge, TState, TSearchNode> intentionalExpansionPolicy = new IntentionalExpansionPolicy<>(edgeFactory, randomSupport);
        List<ExpansionPolicy<TAction, HeuristicEdge, TState, TSearchNode>> expansionPolicies = List.of(probableRewardExpansionPolicy, intentionalExpansionPolicy);

        return ExpansionPolicyController.provide(expansionPolicies);
    }

    private static <TAction extends Action, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, HeuristicEdge, TState, TSearchNode>> ExpansionPolicy<TAction, HeuristicEdge, TState, TSearchNode> createUnintentionalExpansionPolicy(final ProbableRewardExpansionPolicy<TAction, TState, TSearchNode> probableRewardExpansionPolicy, final EdgeFactory<HeuristicEdge> edgeFactory, final ExplorationHeuristic<TAction> explorationHeuristic) {
        UnintentionalExpansionPolicy<TAction, HeuristicEdge, TState, TSearchNode> unintentionalExpansionPolicy = new UnintentionalExpansionPolicy<>(edgeFactory, explorationHeuristic);
        List<ExpansionPolicy<TAction, HeuristicEdge, TState, TSearchNode>> expansionPolicies = List.of(probableRewardExpansionPolicy, unintentionalExpansionPolicy);

        return ExpansionPolicyController.provide(expansionPolicies);
    }

    private static <TAction extends Action, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, HeuristicEdge, TState, TSearchNode>> ExpansionPolicy<TAction, HeuristicEdge, TState, TSearchNode> createExpansionPolicy(final MctsInitializationContext<TAction, HeuristicEdge, TState, TSearchNode, TechniqueBackPropagationStep.Context> initializationContext, final RewardHeuristic<TAction, TState> rewardHeuristic, final ExplorationHeuristic<TAction> explorationHeuristic, final Buffer<TAction, HeuristicEdge, TState, TSearchNode> buffer) {
        List<ExpansionPolicy<TAction, HeuristicEdge, TState, TSearchNode>> expansionPolicies = new ArrayList<>();
        ProbableRewardExpansionPolicy<TAction, TState, TSearchNode> probableRewardExpansionPolicy = new ProbableRewardExpansionPolicy<>(rewardHeuristic);
        EdgeFactory<HeuristicEdge> edgeFactory = initializationContext.getEdgeFactory();
        ExpansionPolicy<TAction, HeuristicEdge, TState, TSearchNode> intentionalExpansionPolicy = createIntentionalExpansionPolicy(probableRewardExpansionPolicy, edgeFactory, initializationContext.createRandomSupport());

        if (explorationHeuristic != null) {
            ExpansionPolicy<TAction, HeuristicEdge, TState, TSearchNode> unintentionalExpansionPolicy = createUnintentionalExpansionPolicy(probableRewardExpansionPolicy, edgeFactory, explorationHeuristic);
            IntentRegulatorExpansionPolicy<TAction, HeuristicEdge, TState, TSearchNode> intentRegulatorExpansionPolicy = new IntentRegulatorExpansionPolicy<>(intentionalExpansionPolicy, unintentionalExpansionPolicy);

            expansionPolicies.add(intentRegulatorExpansionPolicy);
        } else {
            expansionPolicies.add(intentionalExpansionPolicy);
        }

        expansionPolicies.add(buffer::store);

        return ExpansionPolicyController.provide(expansionPolicies);
    }

    private static TechniqueSelectionConfidenceCalculator<HeuristicEdge> createSelectionConfidenceCalculator(final CPuctCalculator cpuctCalculator) {
        if (cpuctCalculator == null) {
            return SELECTION_CONFIDENCE_CALCULATOR;
        }

        return new TechniqueSelectionConfidenceCalculator<>(cpuctCalculator);
    }

    private static <TAction extends Action, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, HeuristicEdge, TState, TSearchNode>> SelectionPolicy<TAction, HeuristicEdge, TState, TSearchNode> createSelectionPolicy(final CPuctCalculator cpuctCalculator, final ExplorationHeuristic<TAction> explorationHeuristic, final RandomSupport randomSupport, final ExpansionPolicy<TAction, HeuristicEdge, TState, TSearchNode> expansionPolicy) {
        TechniqueSelectionConfidenceCalculator<HeuristicEdge> selectionConfidenceCalculator = createSelectionConfidenceCalculator(cpuctCalculator);
        SelectionType selectionType = SelectionType.determine(explorationHeuristic);
        CommonSelectionPolicyFactory<TAction, HeuristicEdge, TState, TSearchNode> selectionPolicyFactory = new CommonSelectionPolicyFactory<>(selectionConfidenceCalculator, selectionType, randomSupport, expansionPolicy);

        return selectionPolicyFactory.create();
    }

    private static <TAction extends Action, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, HeuristicEdge, TState, TSearchNode>> SimulationRolloutPolicy<TAction, HeuristicEdge, TState, TSearchNode> createSimulationRolloutPolicy(final ExtendedSearchPolicy searchPolicy, final RandomSupport randomSupport, final ExplorationHeuristic<TAction> explorationHeuristic, final ExpansionPolicy<TAction, HeuristicEdge, TState, TSearchNode> expansionPolicy) {
        SelectionType selectionType = SelectionType.determine(explorationHeuristic);
        CommonSimulationRolloutPolicyFactory<TAction, HeuristicEdge, TState, TSearchNode> simulationRolloutPolicyFactory = new CommonSimulationRolloutPolicyFactory<>(searchPolicy, randomSupport, selectionType, expansionPolicy);

        return simulationRolloutPolicyFactory.create();
    }

    private static <TAction extends Action, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, HeuristicEdge, TState, TSearchNode>> BackPropagationPolicy<TAction, HeuristicEdge, TState, TSearchNode, TechniqueBackPropagationStep.Context> createBackPropagationPolicy(final BackPropagationType backPropagationType, final BackPropagationObserver<TAction, TState> backPropagationObserver) {
        BackPropagationType fixedBackPropagationType = Objects.requireNonNullElse(backPropagationType, BackPropagationType.REVERSED_ON_BACKTRACK);
        TechniqueBackPropagationStep<TAction, HeuristicEdge, TState, TSearchNode> backPropagationStep = new TechniqueBackPropagationStep<>(fixedBackPropagationType);

        return new BackPropagationPolicy<>(backPropagationStep, backPropagationObserver);
    }

    private static <TAction extends Action, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, HeuristicEdge, TState, TSearchNode>> Mcts<TAction, HeuristicEdge, TState, TSearchNode> createMcts(final MctsInitializationContext<TAction, HeuristicEdge, TState, TSearchNode, TechniqueBackPropagationStep.Context> initializationContext, final ExtendedSearchPolicy searchPolicy, final BufferType bufferType, final RewardHeuristic<TAction, TState> rewardHeuristic, final ExplorationHeuristic<TAction> explorationHeuristic, final CPuctCalculator cpuctCalculator, final BackPropagationType backPropagationType, final BackPropagationObserver<TAction, TState> backPropagationObserver) {
        BufferType fixedBufferType = Objects.requireNonNullElse(bufferType, BufferType.DISABLED);
        Buffer<TAction, HeuristicEdge, TState, TSearchNode> buffer = fixedBufferType.create(initializationContext);
        ExpansionPolicy<TAction, HeuristicEdge, TState, TSearchNode> expansionPolicy = createExpansionPolicy(initializationContext, rewardHeuristic, explorationHeuristic, buffer);
        SelectionPolicy<TAction, HeuristicEdge, TState, TSearchNode> selectionPolicy = createSelectionPolicy(cpuctCalculator, explorationHeuristic, initializationContext.createRandomSupport(), expansionPolicy);
        SimulationRolloutPolicy<TAction, HeuristicEdge, TState, TSearchNode> simulationRolloutPolicy = createSimulationRolloutPolicy(searchPolicy, initializationContext.createRandomSupport(), explorationHeuristic, expansionPolicy);
        BackPropagationPolicy<TAction, HeuristicEdge, TState, TSearchNode, TechniqueBackPropagationStep.Context> backPropagationPolicy = createBackPropagationPolicy(backPropagationType, backPropagationObserver);
        SearchStrategy<TAction, HeuristicEdge, TState, TSearchNode> searchStrategy = initializationContext.createSearchStrategy(searchPolicy, selectionPolicy, simulationRolloutPolicy, backPropagationPolicy);
        MaximumEfficiencyProposalStrategy<TAction, HeuristicEdge, TState, TSearchNode> proposalStrategy = new MaximumEfficiencyProposalStrategy<>(EXPECTED_REWARD_ACTION_EFFICIENCY_CALCULATOR);
        List<ResetHandler> resetHandlers = ResetHandler.create(buffer);

        return new Mcts<>(buffer, searchStrategy, proposalStrategy, resetHandlers);
    }

    @Builder
    public static <TAction extends Action, TState extends State<TAction, TState>> HeuristicMonteCarloTreeSearch<TAction, TState> create(final ExtendedSearchPolicy searchPolicy, final BufferType bufferType, final RewardHeuristic<TAction, TState> rewardHeuristic, final ExplorationHeuristic<TAction> explorationHeuristic, final CPuctCalculator cpuctCalculator, final BackPropagationType backPropagationType, final BackPropagationObserver<TAction, TState> backPropagationObserver) {
        MctsInitializationContext<TAction, HeuristicEdge, TState, StandardSearchNode<TAction, HeuristicEdge, TState>, TechniqueBackPropagationStep.Context> initializationContext = new StandardMctsInitializationContext<>(EDGE_FACTORY);
        Mcts<TAction, HeuristicEdge, TState, StandardSearchNode<TAction, HeuristicEdge, TState>> mcts = createMcts(initializationContext, searchPolicy, bufferType, rewardHeuristic, explorationHeuristic, cpuctCalculator, backPropagationType, backPropagationObserver);

        return new HeuristicMonteCarloTreeSearch<>(mcts);
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
