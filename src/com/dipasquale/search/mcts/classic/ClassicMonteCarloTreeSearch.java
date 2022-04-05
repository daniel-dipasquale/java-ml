package com.dipasquale.search.mcts.classic;

import com.dipasquale.common.random.float1.RandomSupport;
import com.dipasquale.common.random.float1.UniformRandomSupport;
import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.BackPropagationObserver;
import com.dipasquale.search.mcts.BackPropagationPolicy;
import com.dipasquale.search.mcts.CacheType;
import com.dipasquale.search.mcts.EdgeFactory;
import com.dipasquale.search.mcts.ExpansionPolicy;
import com.dipasquale.search.mcts.ExpansionPolicyController;
import com.dipasquale.search.mcts.Mcts;
import com.dipasquale.search.mcts.MonteCarloTreeSearch;
import com.dipasquale.search.mcts.Provider;
import com.dipasquale.search.mcts.ResetHandler;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.common.CommonSelectionPolicy;
import com.dipasquale.search.mcts.common.CommonSelectionPolicyFactory;
import com.dipasquale.search.mcts.common.CommonSimulationRolloutPolicy;
import com.dipasquale.search.mcts.common.CommonSimulationRolloutPolicyFactory;
import com.dipasquale.search.mcts.common.ExplorationHeuristic;
import com.dipasquale.search.mcts.common.ExtendedSearchPolicy;
import com.dipasquale.search.mcts.common.IntentRegulatorExpansionPolicy;
import com.dipasquale.search.mcts.common.IntentionalExpansionPolicy;
import com.dipasquale.search.mcts.common.MaximumEfficiencyProposalStrategy;
import com.dipasquale.search.mcts.common.SelectionType;
import com.dipasquale.search.mcts.common.UnintentionalExpansionPolicy;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ClassicMonteCarloTreeSearch<TAction extends Action, TState extends State<TAction, TState>> implements MonteCarloTreeSearch<TAction, TState> {
    private static final EdgeFactory<ClassicEdge> EDGE_FACTORY = ClassicEdgeFactory.getInstance();
    private static final ClassicSelectionConfidenceCalculator SELECTION_CONFIDENCE_CALCULATOR = new ClassicSelectionConfidenceCalculator();
    private static final PrevalentActionEfficiencyCalculator PREVALENT_ACTION_EFFICIENCY_CALCULATOR = PrevalentActionEfficiencyCalculator.getInstance();
    private final Mcts<TAction, ClassicEdge, TState> mcts;

    private static <TAction extends Action, TState extends State<TAction, TState>> ExpansionPolicy<TAction, ClassicEdge, TState> createExpansionPolicy(final RandomSupport randomSupport, final ExplorationHeuristic<TAction> explorationHeuristic, final Provider<TAction, ClassicEdge, TState> provider) {
        List<ExpansionPolicy<TAction, ClassicEdge, TState>> expansionPolicies = new ArrayList<>();
        IntentionalExpansionPolicy<TAction, ClassicEdge, TState> intentionalExpansionPolicy = new IntentionalExpansionPolicy<>(EDGE_FACTORY, randomSupport);

        if (explorationHeuristic != null) {
            UnintentionalExpansionPolicy<TAction, ClassicEdge, TState> unintentionalExpansionPolicy = new UnintentionalExpansionPolicy<>(EDGE_FACTORY, explorationHeuristic);
            IntentRegulatorExpansionPolicy<TAction, ClassicEdge, TState> intentRegulatorExpansionPolicy = new IntentRegulatorExpansionPolicy<>(intentionalExpansionPolicy, unintentionalExpansionPolicy);

            expansionPolicies.add(intentRegulatorExpansionPolicy);
        } else {
            expansionPolicies.add(intentionalExpansionPolicy);
        }

        if (provider.isAllowedToCollect()) {
            expansionPolicies.add(provider::collect);
        }

        return ExpansionPolicyController.provide(expansionPolicies);
    }

    @Builder
    public static <TAction extends Action, TState extends State<TAction, TState>> ClassicMonteCarloTreeSearch<TAction, TState> create(final ExtendedSearchPolicy searchPolicy, final CacheType cacheType, final ExplorationHeuristic<TAction> explorationHeuristic, final BackPropagationObserver<TAction, TState> backPropagationObserver) {
        UniformRandomSupport randomSupport = new UniformRandomSupport();
        CacheType fixedCacheType = Objects.requireNonNullElse(cacheType, CacheType.NONE);
        Provider<TAction, ClassicEdge, TState> provider = fixedCacheType.create(EDGE_FACTORY);
        ExpansionPolicy<TAction, ClassicEdge, TState> expansionPolicy = createExpansionPolicy(randomSupport, explorationHeuristic, provider);
        CommonSelectionPolicy<TAction, ClassicEdge, TState> selectionPolicy = new CommonSelectionPolicyFactory<>(SELECTION_CONFIDENCE_CALCULATOR, SelectionType.determine(explorationHeuristic), expansionPolicy).create();
        CommonSimulationRolloutPolicy<TAction, ClassicEdge, TState> simulationRolloutPolicy = new CommonSimulationRolloutPolicyFactory<>(searchPolicy, randomSupport, SelectionType.determine(explorationHeuristic), expansionPolicy).create();
        BackPropagationPolicy<TAction, ClassicEdge, TState, ?> backPropagationPolicy = new BackPropagationPolicy<>(ClassicBackPropagationStep.getInstance(), backPropagationObserver);
        MaximumEfficiencyProposalStrategy<TAction, ClassicEdge, TState> proposalStrategy = new MaximumEfficiencyProposalStrategy<>(PREVALENT_ACTION_EFFICIENCY_CALCULATOR);
        List<ResetHandler> resetHandlers = ResetHandler.create(provider);
        Mcts<TAction, ClassicEdge, TState> mcts = new Mcts<>(searchPolicy, provider, selectionPolicy, simulationRolloutPolicy, backPropagationPolicy, proposalStrategy, resetHandlers);

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
