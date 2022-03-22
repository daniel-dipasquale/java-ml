package com.dipasquale.search.mcts.heuristic;

import com.dipasquale.common.random.float1.RandomSupport;
import com.dipasquale.common.random.float1.UniformRandomSupport;
import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.BackPropagationObserver;
import com.dipasquale.search.mcts.BackPropagationPolicy;
import com.dipasquale.search.mcts.Cache;
import com.dipasquale.search.mcts.CacheAvailability;
import com.dipasquale.search.mcts.Expander;
import com.dipasquale.search.mcts.Mcts;
import com.dipasquale.search.mcts.MonteCarloTreeSearch;
import com.dipasquale.search.mcts.MultiExpander;
import com.dipasquale.search.mcts.ResetHandler;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.alphazero.BackPropagationType;
import com.dipasquale.search.mcts.common.CPuctCalculator;
import com.dipasquale.search.mcts.common.CommonSelectionPolicy;
import com.dipasquale.search.mcts.common.CommonSelectionPolicyFactory;
import com.dipasquale.search.mcts.common.CommonSimulationRolloutPolicy;
import com.dipasquale.search.mcts.common.CommonSimulationRolloutPolicyFactory;
import com.dipasquale.search.mcts.common.ExpanderTraversalPolicy;
import com.dipasquale.search.mcts.common.ExplorationProbabilityCalculator;
import com.dipasquale.search.mcts.common.ExtendedSearchPolicy;
import com.dipasquale.search.mcts.common.IntentRegulatorExpander;
import com.dipasquale.search.mcts.common.IntentionalExpander;
import com.dipasquale.search.mcts.common.MaximumEfficiencyProposalStrategy;
import com.dipasquale.search.mcts.common.RosinCPuctCalculator;
import com.dipasquale.search.mcts.common.SelectionType;
import com.dipasquale.search.mcts.common.TechniqueBackPropagationStep;
import com.dipasquale.search.mcts.common.TechniqueSelectionConfidenceCalculator;
import com.dipasquale.search.mcts.common.UnintentionalExpander;
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

    private static <TAction extends Action, TState extends State<TAction, TState>> Expander<TAction, HeuristicEdge, TState> createIntentionalExpander(final ValueExpander<TAction, TState> valueExpander, final RandomSupport randomSupport) {
        IntentionalExpander<TAction, HeuristicEdge, TState> intentionalExpander = new IntentionalExpander<>(EDGE_FACTORY, randomSupport);
        List<Expander<TAction, HeuristicEdge, TState>> expanders = List.of(valueExpander, intentionalExpander);

        return new MultiExpander<>(expanders);
    }

    private static <TAction extends Action, TState extends State<TAction, TState>> Expander<TAction, HeuristicEdge, TState> createUnintentionalExpander(final ValueExpander<TAction, TState> valueExpander, final ExplorationProbabilityCalculator<TAction> explorationProbabilityCalculator) {
        UnintentionalExpander<TAction, HeuristicEdge, TState> unintentionalExpander = new UnintentionalExpander<>(EDGE_FACTORY, explorationProbabilityCalculator);
        List<Expander<TAction, HeuristicEdge, TState>> expanders = List.of(valueExpander, unintentionalExpander);

        return new MultiExpander<>(expanders);
    }

    private static <TAction extends Action, TState extends State<TAction, TState>> ExpanderTraversalPolicy<TAction, HeuristicEdge, TState> createExpanderTraversalPolicy(final RandomSupport randomSupport, final ValueHeuristic<TAction, TState> valueHeuristic, final ExplorationProbabilityCalculator<TAction> explorationProbabilityCalculator, final Cache<TAction, HeuristicEdge, TState> cache) {
        List<Expander<TAction, HeuristicEdge, TState>> expanders = new ArrayList<>();
        ValueExpander<TAction, TState> valueExpander = new ValueExpander<>(valueHeuristic);
        Expander<TAction, HeuristicEdge, TState> intentionalExpander = createIntentionalExpander(valueExpander, randomSupport);

        if (explorationProbabilityCalculator != null) {
            Expander<TAction, HeuristicEdge, TState> unintentionalExpander = createUnintentionalExpander(valueExpander, explorationProbabilityCalculator);
            IntentRegulatorExpander<TAction, HeuristicEdge, TState> intentRegulatorExpander = new IntentRegulatorExpander<>(intentionalExpander, unintentionalExpander);

            expanders.add(intentRegulatorExpander);
        } else {
            expanders.add(intentionalExpander);
        }

        if (cache != null) {
            expanders.add(cache::storeIfApplicable);
        }

        Expander<TAction, HeuristicEdge, TState> expander = switch (expanders.size()) {
            case 1 -> expanders.get(0);

            default -> new MultiExpander<>(expanders);
        };

        return new ExpanderTraversalPolicy<>(expander);
    }

    private static TechniqueSelectionConfidenceCalculator<HeuristicEdge> createSelectionConfidenceCalculator(final CPuctCalculator cpuctCalculator) {
        if (cpuctCalculator == null) {
            return SELECTION_CONFIDENCE_CALCULATOR;
        }

        return new TechniqueSelectionConfidenceCalculator<>(cpuctCalculator);
    }

    private static <TAction extends Action, TState extends State<TAction, TState>> CommonSelectionPolicy<TAction, HeuristicEdge, TState> createSelectionPolicy(final ExpanderTraversalPolicy<TAction, HeuristicEdge, TState> expanderTraversalPolicy, final CPuctCalculator cpuctCalculator, final ExplorationProbabilityCalculator<TAction> explorationProbabilityCalculator) {
        TechniqueSelectionConfidenceCalculator<HeuristicEdge> selectionConfidenceCalculator = createSelectionConfidenceCalculator(cpuctCalculator);

        return new CommonSelectionPolicyFactory<>(expanderTraversalPolicy, selectionConfidenceCalculator, SelectionType.determine(explorationProbabilityCalculator)).create();
    }

    private static <TAction extends Action, TState extends State<TAction, TState>> BackPropagationPolicy<TAction, HeuristicEdge, TState, ?> createBackPropagationPolicy(final BackPropagationType backPropagationType, final BackPropagationObserver<TAction, TState> backPropagationObserver) {
        BackPropagationType backPropagationTypeFixed = Objects.requireNonNullElse(backPropagationType, BackPropagationType.REVERSED_ON_BACKTRACK);
        TechniqueBackPropagationStep<TAction, HeuristicEdge, TState> backPropagationStep = new TechniqueBackPropagationStep<>(backPropagationTypeFixed);

        return new BackPropagationPolicy<>(backPropagationStep, backPropagationObserver);
    }

    @Builder
    public static <TAction extends Action, TState extends State<TAction, TState>> HeuristicMonteCarloTreeSearch<TAction, TState> create(final ExtendedSearchPolicy searchPolicy, final CacheAvailability cacheAvailability, final ValueHeuristic<TAction, TState> valueHeuristic, final ExplorationProbabilityCalculator<TAction> explorationProbabilityCalculator, final CPuctCalculator cpuctCalculator, final BackPropagationType backPropagationType, final BackPropagationObserver<TAction, TState> backPropagationObserver) {
        UniformRandomSupport randomSupport = new UniformRandomSupport();
        Cache<TAction, HeuristicEdge, TState> cache = cacheAvailability.provide(EDGE_FACTORY);
        ExpanderTraversalPolicy<TAction, HeuristicEdge, TState> expanderTraversalPolicy = createExpanderTraversalPolicy(randomSupport, valueHeuristic, explorationProbabilityCalculator, cache);
        CommonSelectionPolicy<TAction, HeuristicEdge, TState> selectionPolicy = createSelectionPolicy(expanderTraversalPolicy, cpuctCalculator, explorationProbabilityCalculator);
        CommonSimulationRolloutPolicy<TAction, HeuristicEdge, TState> simulationRolloutPolicy = new CommonSimulationRolloutPolicyFactory<>(searchPolicy, expanderTraversalPolicy, randomSupport).create();
        BackPropagationPolicy<TAction, HeuristicEdge, TState, ?> backPropagationPolicy = createBackPropagationPolicy(backPropagationType, backPropagationObserver);
        MaximumEfficiencyProposalStrategy<TAction, HeuristicEdge, TState> proposalStrategy = new MaximumEfficiencyProposalStrategy<>(EXPECTED_REWARD_ACTION_EFFICIENCY_CALCULATOR);
        List<ResetHandler> resetHandlers = ResetHandler.create(cache);
        Mcts<TAction, HeuristicEdge, TState> mcts = new Mcts<>(searchPolicy, EDGE_FACTORY, cache, selectionPolicy, simulationRolloutPolicy, backPropagationPolicy, proposalStrategy, resetHandlers);

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
