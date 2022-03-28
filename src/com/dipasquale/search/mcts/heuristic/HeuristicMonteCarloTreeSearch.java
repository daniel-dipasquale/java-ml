package com.dipasquale.search.mcts.heuristic;

import com.dipasquale.common.random.float1.RandomSupport;
import com.dipasquale.common.random.float1.UniformRandomSupport;
import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.BackPropagationObserver;
import com.dipasquale.search.mcts.BackPropagationPolicy;
import com.dipasquale.search.mcts.CacheType;
import com.dipasquale.search.mcts.ExpansionPolicy;
import com.dipasquale.search.mcts.ExpansionPolicyController;
import com.dipasquale.search.mcts.Mcts;
import com.dipasquale.search.mcts.MonteCarloTreeSearch;
import com.dipasquale.search.mcts.Provider;
import com.dipasquale.search.mcts.ResetHandler;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.alphazero.BackPropagationType;
import com.dipasquale.search.mcts.common.CPuctCalculator;
import com.dipasquale.search.mcts.common.CommonSelectionPolicy;
import com.dipasquale.search.mcts.common.CommonSelectionPolicyFactory;
import com.dipasquale.search.mcts.common.CommonSimulationRolloutPolicy;
import com.dipasquale.search.mcts.common.CommonSimulationRolloutPolicyFactory;
import com.dipasquale.search.mcts.common.ExplorationProbabilityCalculator;
import com.dipasquale.search.mcts.common.ExtendedSearchPolicy;
import com.dipasquale.search.mcts.common.IntentRegulatorExpansionPolicy;
import com.dipasquale.search.mcts.common.IntentionalExpansionPolicy;
import com.dipasquale.search.mcts.common.MaximumEfficiencyProposalStrategy;
import com.dipasquale.search.mcts.common.RosinCPuctCalculator;
import com.dipasquale.search.mcts.common.SelectionType;
import com.dipasquale.search.mcts.common.TechniqueBackPropagationStep;
import com.dipasquale.search.mcts.common.TechniqueSelectionConfidenceCalculator;
import com.dipasquale.search.mcts.common.UnintentionalExpansionPolicy;
import com.dipasquale.search.mcts.common.ValueHeuristic;
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
    private final Mcts<TAction, HeuristicEdge, TState> mcts;

    private static <TAction extends Action, TState extends State<TAction, TState>> ExpansionPolicy<TAction, HeuristicEdge, TState> createIntentionalExpansionPolicy(final ValueExpansionPolicy<TAction, TState> valueExpansionPolicy, final RandomSupport randomSupport) {
        IntentionalExpansionPolicy<TAction, HeuristicEdge, TState> intentionalExpansionPolicy = new IntentionalExpansionPolicy<>(EDGE_FACTORY, randomSupport);
        List<ExpansionPolicy<TAction, HeuristicEdge, TState>> expansionPolicies = List.of(valueExpansionPolicy, intentionalExpansionPolicy);

        return ExpansionPolicyController.provide(expansionPolicies);
    }

    private static <TAction extends Action, TState extends State<TAction, TState>> ExpansionPolicy<TAction, HeuristicEdge, TState> createUnintentionalExpansionPolicy(final ValueExpansionPolicy<TAction, TState> valueExpansionPolicy, final ExplorationProbabilityCalculator<TAction> explorationProbabilityCalculator) {
        UnintentionalExpansionPolicy<TAction, HeuristicEdge, TState> unintentionalExpansionPolicy = new UnintentionalExpansionPolicy<>(EDGE_FACTORY, explorationProbabilityCalculator);
        List<ExpansionPolicy<TAction, HeuristicEdge, TState>> expansionPolicies = List.of(valueExpansionPolicy, unintentionalExpansionPolicy);

        return ExpansionPolicyController.provide(expansionPolicies);
    }

    private static <TAction extends Action, TState extends State<TAction, TState>> ExpansionPolicy<TAction, HeuristicEdge, TState> createExpansionPolicy(final RandomSupport randomSupport, final ValueHeuristic<TAction, TState> valueHeuristic, final ExplorationProbabilityCalculator<TAction> explorationProbabilityCalculator, final Provider<TAction, HeuristicEdge, TState> provider) {
        List<ExpansionPolicy<TAction, HeuristicEdge, TState>> expansionPolicies = new ArrayList<>();
        ValueExpansionPolicy<TAction, TState> valueExpansionPolicy = new ValueExpansionPolicy<>(valueHeuristic);
        ExpansionPolicy<TAction, HeuristicEdge, TState> intentionalExpansionPolicy = createIntentionalExpansionPolicy(valueExpansionPolicy, randomSupport);

        if (explorationProbabilityCalculator != null) {
            ExpansionPolicy<TAction, HeuristicEdge, TState> unintentionalExpansionPolicy = createUnintentionalExpansionPolicy(valueExpansionPolicy, explorationProbabilityCalculator);
            IntentRegulatorExpansionPolicy<TAction, HeuristicEdge, TState> intentRegulatorExpansionPolicy = new IntentRegulatorExpansionPolicy<>(intentionalExpansionPolicy, unintentionalExpansionPolicy);

            expansionPolicies.add(intentRegulatorExpansionPolicy);
        } else {
            expansionPolicies.add(intentionalExpansionPolicy);
        }

        if (provider.isAllowedToCollect()) {
            expansionPolicies.add(provider::collect);
        }

        return ExpansionPolicyController.provide(expansionPolicies);
    }

    private static TechniqueSelectionConfidenceCalculator<HeuristicEdge> createSelectionConfidenceCalculator(final CPuctCalculator cpuctCalculator) {
        if (cpuctCalculator == null) {
            return SELECTION_CONFIDENCE_CALCULATOR;
        }

        return new TechniqueSelectionConfidenceCalculator<>(cpuctCalculator);
    }

    private static <TAction extends Action, TState extends State<TAction, TState>> CommonSelectionPolicy<TAction, HeuristicEdge, TState> createSelectionPolicy(final CPuctCalculator cpuctCalculator, final ExplorationProbabilityCalculator<TAction> explorationProbabilityCalculator, final ExpansionPolicy<TAction, HeuristicEdge, TState> expansionPolicy) {
        TechniqueSelectionConfidenceCalculator<HeuristicEdge> selectionConfidenceCalculator = createSelectionConfidenceCalculator(cpuctCalculator);
        SelectionType selectionType = SelectionType.determine(explorationProbabilityCalculator);

        return new CommonSelectionPolicyFactory<>(selectionConfidenceCalculator, selectionType, expansionPolicy).create();
    }

    private static <TAction extends Action, TState extends State<TAction, TState>> CommonSimulationRolloutPolicy<TAction, HeuristicEdge, TState> createSimulationRolloutPolicy(final ExtendedSearchPolicy searchPolicy, final RandomSupport randomSupport, final ExplorationProbabilityCalculator<TAction> explorationProbabilityCalculator, final ExpansionPolicy<TAction, HeuristicEdge, TState> expansionPolicy) {
        SelectionType selectionType = SelectionType.determine(explorationProbabilityCalculator);

        return new CommonSimulationRolloutPolicyFactory<>(searchPolicy, randomSupport, selectionType, expansionPolicy).create();
    }

    private static <TAction extends Action, TState extends State<TAction, TState>> BackPropagationPolicy<TAction, HeuristicEdge, TState, ?> createBackPropagationPolicy(final BackPropagationType backPropagationType, final BackPropagationObserver<TAction, TState> backPropagationObserver) {
        BackPropagationType fixedBackPropagationType = Objects.requireNonNullElse(backPropagationType, BackPropagationType.REVERSED_ON_BACKTRACK);
        TechniqueBackPropagationStep<TAction, HeuristicEdge, TState> backPropagationStep = new TechniqueBackPropagationStep<>(fixedBackPropagationType);

        return new BackPropagationPolicy<>(backPropagationStep, backPropagationObserver);
    }

    @Builder
    public static <TAction extends Action, TState extends State<TAction, TState>> HeuristicMonteCarloTreeSearch<TAction, TState> create(final ExtendedSearchPolicy searchPolicy, final CacheType cacheType, final ValueHeuristic<TAction, TState> valueHeuristic, final ExplorationProbabilityCalculator<TAction> explorationProbabilityCalculator, final CPuctCalculator cpuctCalculator, final BackPropagationType backPropagationType, final BackPropagationObserver<TAction, TState> backPropagationObserver) {
        UniformRandomSupport randomSupport = new UniformRandomSupport();
        CacheType fixedCacheType = Objects.requireNonNullElse(cacheType, CacheType.NONE);
        Provider<TAction, HeuristicEdge, TState> provider = fixedCacheType.create(EDGE_FACTORY);
        ExpansionPolicy<TAction, HeuristicEdge, TState> expansionPolicy = createExpansionPolicy(randomSupport, valueHeuristic, explorationProbabilityCalculator, provider);
        CommonSelectionPolicy<TAction, HeuristicEdge, TState> selectionPolicy = createSelectionPolicy(cpuctCalculator, explorationProbabilityCalculator, expansionPolicy);
        CommonSimulationRolloutPolicy<TAction, HeuristicEdge, TState> simulationRolloutPolicy = createSimulationRolloutPolicy(searchPolicy, randomSupport, explorationProbabilityCalculator, expansionPolicy);
        BackPropagationPolicy<TAction, HeuristicEdge, TState, ?> backPropagationPolicy = createBackPropagationPolicy(backPropagationType, backPropagationObserver);
        MaximumEfficiencyProposalStrategy<TAction, HeuristicEdge, TState> proposalStrategy = new MaximumEfficiencyProposalStrategy<>(EXPECTED_REWARD_ACTION_EFFICIENCY_CALCULATOR);
        List<ResetHandler> resetHandlers = ResetHandler.create(provider);
        Mcts<TAction, HeuristicEdge, TState> mcts = new Mcts<>(searchPolicy, provider, selectionPolicy, simulationRolloutPolicy, backPropagationPolicy, proposalStrategy, resetHandlers);

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
