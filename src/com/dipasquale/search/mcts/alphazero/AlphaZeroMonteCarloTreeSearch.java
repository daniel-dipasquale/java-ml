package com.dipasquale.search.mcts.alphazero;

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
import com.dipasquale.search.mcts.ResetHandler;
import com.dipasquale.search.mcts.SearchPolicy;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.common.CPuctCalculator;
import com.dipasquale.search.mcts.common.RosinCPuctCalculator;
import com.dipasquale.search.mcts.common.TechniqueBackPropagationStep;
import com.dipasquale.search.mcts.common.TechniqueSelectionConfidenceCalculator;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class AlphaZeroMonteCarloTreeSearch<TAction extends Action, TState extends State<TAction, TState>> implements MonteCarloTreeSearch<TAction, TState> {
    private static final EdgeFactory<AlphaZeroEdge> EDGE_FACTORY = AlphaZeroEdgeFactory.getInstance();
    private static final RosinCPuctCalculator ROSIN_C_PUCT_ALGORITHM = new RosinCPuctCalculator();
    private static final TechniqueSelectionConfidenceCalculator<AlphaZeroEdge> SELECTION_CONFIDENCE_CALCULATOR = new TechniqueSelectionConfidenceCalculator<>(ROSIN_C_PUCT_ALGORITHM);
    private static final VisitedActionEfficiencyCalculator VISITED_ACTION_EFFICIENCY_CALCULATOR = VisitedActionEfficiencyCalculator.getInstance();
    private final Mcts<TAction, AlphaZeroEdge, TState> mcts;

    private static TechniqueSelectionConfidenceCalculator<AlphaZeroEdge> createSelectionConfidenceCalculator(final CPuctCalculator cpuctCalculator) {
        if (cpuctCalculator == null) {
            return SELECTION_CONFIDENCE_CALCULATOR;
        }

        return new TechniqueSelectionConfidenceCalculator<>(cpuctCalculator);
    }

    private static <TAction extends Action, TState extends State<TAction, TState>> AlphaZeroSelectionPolicy<TAction, TState> createSelectionPolicy(final RootExplorationProbabilityNoiseSettings rootExplorationProbabilityNoise, final Cache<TAction, AlphaZeroEdge, TState> cache, final AlphaZeroModel<TAction, TState> traversalModel, final CPuctCalculator cpuctCalculator) {
        Expander<TAction, AlphaZeroEdge, TState> additionalExpander = new AlphaZeroAdditionalExpanderFactory<>(rootExplorationProbabilityNoise, cache).create();
        AlphaZeroExpander<TAction, TState> expander = new AlphaZeroExpander<>(EDGE_FACTORY, traversalModel, additionalExpander);
        TechniqueSelectionConfidenceCalculator<AlphaZeroEdge> selectionConfidenceCalculator = createSelectionConfidenceCalculator(cpuctCalculator);

        return new AlphaZeroSelectionPolicyFactory<>(expander, selectionConfidenceCalculator, traversalModel).create();
    }

    private static <TAction extends Action, TState extends State<TAction, TState>> BackPropagationPolicy<TAction, AlphaZeroEdge, TState, ?> createBackPropagationPolicy(final BackPropagationType backPropagationType, final BackPropagationObserver<TAction, TState> backPropagationObserver) {
        BackPropagationType backPropagationTypeFixed = Objects.requireNonNullElse(backPropagationType, BackPropagationType.REVERSED_ON_BACKTRACK);
        TechniqueBackPropagationStep<TAction, AlphaZeroEdge, TState> backPropagationStep = new TechniqueBackPropagationStep<>(backPropagationTypeFixed);

        return new BackPropagationPolicy<>(backPropagationStep, backPropagationObserver);
    }

    private static <TAction extends Action, TState extends State<TAction, TState>> AlphaZeroProposalStrategy<TAction, TState> createProposalStrategy(final TemperatureController temperatureController) {
        UniformRandomSupport randomSupport = new UniformRandomSupport();
        RankedActionDecisionMaker<TAction, TState> explorationRankedActionDecisionMaker = new ExplorationRankedActionDecisionMaker<>(randomSupport);
        RankedActionDecisionMaker<TAction, TState> exploitationRankedActionDecisionMaker = ExploitationRankedActionDecisionMaker.getInstance();

        return new AlphaZeroProposalStrategy<>(VISITED_ACTION_EFFICIENCY_CALCULATOR, temperatureController, explorationRankedActionDecisionMaker, exploitationRankedActionDecisionMaker);
    }

    private static <TAction extends Action, TState extends State<TAction, TState>> List<ResetHandler> createResetHandlers(final AlphaZeroModel<TAction, TState> traversalModel, final Cache<TAction, AlphaZeroEdge, TState> cache) {
        List<ResetHandler> resetHandlers = new ArrayList<>();

        resetHandlers.add(traversalModel::reset);

        if (cache != null) {
            resetHandlers.add(cache::clear);
        }

        return resetHandlers;
    }

    @Builder
    private static <TAction extends Action, TState extends State<TAction, TState>> AlphaZeroMonteCarloTreeSearch<TAction, TState> create(final SearchPolicy searchPolicy, final RootExplorationProbabilityNoiseSettings rootExplorationProbabilityNoise, final CacheAvailability cacheAvailability, final AlphaZeroModel<TAction, TState> traversalModel, final CPuctCalculator cpuctCalculator, final BackPropagationType backPropagationType, final BackPropagationObserver<TAction, TState> backPropagationObserver, final TemperatureController temperatureController) {
        Cache<TAction, AlphaZeroEdge, TState> cache = cacheAvailability.provide(EDGE_FACTORY);
        AlphaZeroSelectionPolicy<TAction, TState> selectionPolicy = createSelectionPolicy(rootExplorationProbabilityNoise, cache, traversalModel, cpuctCalculator);
        BackPropagationPolicy<TAction, AlphaZeroEdge, TState, ?> backPropagationPolicy = createBackPropagationPolicy(backPropagationType, backPropagationObserver);
        AlphaZeroProposalStrategy<TAction, TState> proposalStrategy = createProposalStrategy(temperatureController);
        List<ResetHandler> resetHandlers = createResetHandlers(traversalModel, cache);
        Mcts<TAction, AlphaZeroEdge, TState> mcts = new Mcts<>(searchPolicy, EDGE_FACTORY, cache, selectionPolicy, null, backPropagationPolicy, proposalStrategy, resetHandlers);

        return new AlphaZeroMonteCarloTreeSearch<>(mcts);
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
