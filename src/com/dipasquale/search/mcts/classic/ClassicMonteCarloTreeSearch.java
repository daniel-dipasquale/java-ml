package com.dipasquale.search.mcts.classic;

import com.dipasquale.common.random.float1.RandomSupport;
import com.dipasquale.common.random.float1.UniformRandomSupport;
import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.BackPropagationObserver;
import com.dipasquale.search.mcts.BackPropagationPolicy;
import com.dipasquale.search.mcts.Cache;
import com.dipasquale.search.mcts.CacheAvailability;
import com.dipasquale.search.mcts.EdgeFactory;
import com.dipasquale.search.mcts.Expander;
import com.dipasquale.search.mcts.Mcts;
import com.dipasquale.search.mcts.MonteCarloTreeSearch;
import com.dipasquale.search.mcts.MultiExpander;
import com.dipasquale.search.mcts.ResetHandler;
import com.dipasquale.search.mcts.State;
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
import com.dipasquale.search.mcts.common.SelectionType;
import com.dipasquale.search.mcts.common.UnintentionalExpander;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ClassicMonteCarloTreeSearch<TAction extends Action, TState extends State<TAction, TState>> implements MonteCarloTreeSearch<TAction, TState> {
    private static final EdgeFactory<ClassicEdge> EDGE_FACTORY = ClassicEdgeFactory.getInstance();
    private static final ClassicSelectionConfidenceCalculator SELECTION_CONFIDENCE_CALCULATOR = new ClassicSelectionConfidenceCalculator();
    private static final PrevalentActionEfficiencyCalculator PREVALENT_ACTION_EFFICIENCY_CALCULATOR = PrevalentActionEfficiencyCalculator.getInstance();
    private final Mcts<TAction, ClassicEdge, TState> mcts;

    private static <TAction extends Action, TState extends State<TAction, TState>> ExpanderTraversalPolicy<TAction, ClassicEdge, TState> createExpanderTraversalPolicy(final RandomSupport randomSupport, final ExplorationProbabilityCalculator<TAction> explorationProbabilityCalculator, final Cache<TAction, ClassicEdge, TState> cache) {
        List<Expander<TAction, ClassicEdge, TState>> expanders = new ArrayList<>();
        IntentionalExpander<TAction, ClassicEdge, TState> intentionalExpander = new IntentionalExpander<>(EDGE_FACTORY, randomSupport);

        if (explorationProbabilityCalculator != null) {
            UnintentionalExpander<TAction, ClassicEdge, TState> unintentionalExpander = new UnintentionalExpander<>(EDGE_FACTORY, explorationProbabilityCalculator);
            IntentRegulatorExpander<TAction, ClassicEdge, TState> intentRegulatorExpander = new IntentRegulatorExpander<>(intentionalExpander, unintentionalExpander);

            expanders.add(intentRegulatorExpander);
        } else {
            expanders.add(intentionalExpander);
        }

        if (cache != null) {
            expanders.add(cache::storeIfApplicable);
        }

        Expander<TAction, ClassicEdge, TState> expander = switch (expanders.size()) {
            case 1 -> expanders.get(0);

            default -> new MultiExpander<>(expanders);
        };

        return new ExpanderTraversalPolicy<>(expander);
    }

    @Builder
    public static <TAction extends Action, TState extends State<TAction, TState>> ClassicMonteCarloTreeSearch<TAction, TState> create(final ExtendedSearchPolicy searchPolicy, final CacheAvailability cacheAvailability, final ExplorationProbabilityCalculator<TAction> explorationProbabilityCalculator, final BackPropagationObserver<TAction, TState> backPropagationObserver) {
        UniformRandomSupport randomSupport = new UniformRandomSupport();
        Cache<TAction, ClassicEdge, TState> cache = cacheAvailability.provide(EDGE_FACTORY);
        ExpanderTraversalPolicy<TAction, ClassicEdge, TState> expanderTraversalPolicy = createExpanderTraversalPolicy(randomSupport, explorationProbabilityCalculator, cache);
        CommonSelectionPolicy<TAction, ClassicEdge, TState> selectionPolicy = new CommonSelectionPolicyFactory<>(expanderTraversalPolicy, SELECTION_CONFIDENCE_CALCULATOR, SelectionType.determine(explorationProbabilityCalculator)).create();
        CommonSimulationRolloutPolicy<TAction, ClassicEdge, TState> simulationRolloutPolicy = new CommonSimulationRolloutPolicyFactory<>(searchPolicy, expanderTraversalPolicy, randomSupport).create();
        BackPropagationPolicy<TAction, ClassicEdge, TState, ?> backPropagationPolicy = new BackPropagationPolicy<>(ClassicBackPropagationStep.getInstance(), backPropagationObserver);
        MaximumEfficiencyProposalStrategy<TAction, ClassicEdge, TState> proposalStrategy = new MaximumEfficiencyProposalStrategy<>(PREVALENT_ACTION_EFFICIENCY_CALCULATOR);
        List<ResetHandler> resetHandlers = ResetHandler.create(cache);
        Mcts<TAction, ClassicEdge, TState> mcts = new Mcts<>(searchPolicy, EDGE_FACTORY, cache, selectionPolicy, simulationRolloutPolicy, backPropagationPolicy, proposalStrategy, resetHandlers);

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
