package com.dipasquale.search.mcts.alphazero;

import com.dipasquale.common.random.float1.UniformRandomSupport;
import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.ActionEfficiencyCalculator;
import com.dipasquale.search.mcts.BackPropagationPolicy;
import com.dipasquale.search.mcts.EdgeFactory;
import com.dipasquale.search.mcts.Mcts;
import com.dipasquale.search.mcts.MonteCarloTreeSearch;
import com.dipasquale.search.mcts.NodeCacheSettings;
import com.dipasquale.search.mcts.ResetHandler;
import com.dipasquale.search.mcts.SearchNodeCache;
import com.dipasquale.search.mcts.SearchNodeInitializer;
import com.dipasquale.search.mcts.SearchPolicy;
import com.dipasquale.search.mcts.SelectionConfidenceCalculator;
import com.dipasquale.search.mcts.SimulationResultObserver;
import com.dipasquale.search.mcts.State;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class AlphaZeroMonteCarloTreeSearch<TAction extends Action, TState extends State<TAction, TState>> implements MonteCarloTreeSearch<TAction, TState> {
    private static final RosinCPuctCalculator ROSIN_C_PUCT_ALGORITHM = new RosinCPuctCalculator();
    private static final AlphaZeroSelectionConfidenceCalculator ALPHA_ZERO_SELECTION_CONFIDENCE_CALCULATOR = new AlphaZeroSelectionConfidenceCalculator(ROSIN_C_PUCT_ALGORITHM);
    private final Mcts<TAction, AlphaZeroEdge, TState> mcts;

    private static <TAction extends Action, TState extends State<TAction, TState>> SearchNodeCache<TAction, AlphaZeroEdge, TState> createNodeCache(final NodeCacheSettings nodeCacheSettings, final EdgeFactory<AlphaZeroEdge> edgeFactory) {
        if (nodeCacheSettings == null) {
            return null;
        }

        return new SearchNodeCache<>(nodeCacheSettings.getParticipants(), edgeFactory);
    }

    private static <TAction extends Action, TState extends State<TAction, TState>> RankedActionMapper<TAction, TState> createRankedActionMapper(final AlphaZeroModel<TAction, TState> traversalModel, final ActionEfficiencyCalculator<TAction, AlphaZeroEdge> actionEfficiencyCalculator) {
        if (traversalModel.isEveryOutcomeDeterministic()) {
            return new DeterministicOutcomeRankedActionMapper<>(actionEfficiencyCalculator);
        }

        return new StochasticOutcomeRankedActionMapper<>(actionEfficiencyCalculator);
    }

    private static <TAction extends Action, TState extends State<TAction, TState>> AlphaZeroSearchNodeProposalStrategy<TAction, TState> createNodeProposalStrategy(final AlphaZeroModel<TAction, TState> traversalModel, final TemperatureController temperatureController) {
        RankedActionMapper<TAction, TState> rankedActionMapper = createRankedActionMapper(traversalModel, MostVisitedActionEfficiencyCalculator.getInstance());
        UniformRandomSupport randomSupport = new UniformRandomSupport();
        RankedActionDecisionMaker<TAction, TState> explorationRankedActionDecisionMaker = new ExplorationRankedActionDecisionMaker<>(randomSupport);
        RankedActionDecisionMaker<TAction, TState> exploitationRankedActionDecisionMaker = ExploitationRankedActionDecisionMaker.getInstance();

        return new AlphaZeroSearchNodeProposalStrategy<>(rankedActionMapper, temperatureController, explorationRankedActionDecisionMaker, exploitationRankedActionDecisionMaker);
    }

    private static <TAction extends Action, TState extends State<TAction, TState>> List<ResetHandler> createResetHandlers(final AlphaZeroModel<TAction, TState> traversalModel, final SearchNodeCache<TAction, AlphaZeroEdge, TState> nodeCache) {
        List<ResetHandler> resetHandlers = new ArrayList<>();

        resetHandlers.add(traversalModel::reset);

        if (nodeCache != null) {
            resetHandlers.add(nodeCache::clear);
        }

        return resetHandlers;
    }

    @Builder
    private static <TAction extends Action, TState extends State<TAction, TState>> AlphaZeroMonteCarloTreeSearch<TAction, TState> createAlphaZero(final SearchPolicy searchPolicy, final RootExplorationProbabilityNoiseSettings rootExplorationProbabilityNoise, final NodeCacheSettings nodeCache, final AlphaZeroModel<TAction, TState> traversalModel, final SelectionConfidenceCalculator<AlphaZeroEdge> selectionConfidenceCalculator, final BackPropagationType backPropagationType, final SimulationResultObserver<TAction, AlphaZeroEdge, TState> simulationResultObserver, final TemperatureController temperatureController) {
        EdgeFactory<AlphaZeroEdge> edgeFactory = AlphaZeroEdgeFactory.getInstance();
        SearchNodeCache<TAction, AlphaZeroEdge, TState> nodeCacheFixed = createNodeCache(nodeCache, edgeFactory);
        SearchNodeInitializer<TAction, AlphaZeroEdge, TState> nodeInitializer = new AlphaZeroSearchNodeInitializerFactory<>(rootExplorationProbabilityNoise, nodeCacheFixed).create();
        AlphaZeroChildrenInitializerTraversalPolicy<TAction, TState> childrenInitializerTraversalPolicy = new AlphaZeroChildrenInitializerTraversalPolicy<>(edgeFactory, traversalModel, nodeInitializer);
        SelectionConfidenceCalculator<AlphaZeroEdge> selectionConfidenceCalculatorFixed = Objects.requireNonNullElse(selectionConfidenceCalculator, ALPHA_ZERO_SELECTION_CONFIDENCE_CALCULATOR);
        AlphaZeroSelectionPolicy<TAction, TState> selectionPolicy = new AlphaZeroSelectionPolicy<>(childrenInitializerTraversalPolicy, selectionConfidenceCalculatorFixed);
        BackPropagationType backPropagationTypeFixed = Objects.requireNonNullElse(backPropagationType, BackPropagationType.REVERSED_ON_BACKTRACK);
        BackPropagationPolicy<TAction, AlphaZeroEdge, TState, ?> backPropagationPolicy = new BackPropagationPolicy<>(new AlphaZeroBackPropagationStep<>(backPropagationTypeFixed), simulationResultObserver);
        AlphaZeroSearchNodeProposalStrategy<TAction, TState> nodeProposalStrategy = createNodeProposalStrategy(traversalModel, temperatureController);
        List<ResetHandler> resetHandlers = createResetHandlers(traversalModel, nodeCacheFixed);
        Mcts<TAction, AlphaZeroEdge, TState> mcts = new Mcts<>(searchPolicy, edgeFactory, nodeCacheFixed, selectionPolicy, null, backPropagationPolicy, nodeProposalStrategy, resetHandlers);

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
