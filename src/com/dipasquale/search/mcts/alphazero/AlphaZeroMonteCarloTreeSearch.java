package com.dipasquale.search.mcts.alphazero;

import com.dipasquale.common.random.float1.UniformRandomSupport;
import com.dipasquale.common.random.float2.DirichletDistributionSupport;
import com.dipasquale.common.random.float2.GammaDistributionSupport;
import com.dipasquale.common.random.float2.GaussianDistributionSupport;
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

    private static <TAction extends Action, TState extends State<TAction, TState>> void addRootExplorationProbabilityNoiseToIfApplicable(final List<ExpansionPolicy<TAction, AlphaZeroEdge, TState>> expansionPolicies, final RootExplorationProbabilityNoiseSettings rootExplorationProbabilityNoise) {
        if (rootExplorationProbabilityNoise == null) {
            return;
        }

        float epsilon = rootExplorationProbabilityNoise.getEpsilon();

        if (Float.compare(epsilon, 0f) <= 0 || Float.compare(epsilon, 1f) > 0) {
            return;
        }

        double shape = rootExplorationProbabilityNoise.getShape();
        com.dipasquale.common.random.float2.UniformRandomSupport uniformRandomSupport = new com.dipasquale.common.random.float2.UniformRandomSupport();
        GaussianDistributionSupport gaussianDistributionSupport = new GaussianDistributionSupport();
        GammaDistributionSupport gammaDistributionSupport = new GammaDistributionSupport(uniformRandomSupport, gaussianDistributionSupport);
        DirichletDistributionSupport dirichletDistributionSupport = new DirichletDistributionSupport(gammaDistributionSupport);

        expansionPolicies.add(new ExplorationProbabilityNoiseRootExpansionPolicy<>(shape, dirichletDistributionSupport, epsilon));
    }

    private static <TAction extends Action, TState extends State<TAction, TState>> ExpansionPolicy<TAction, AlphaZeroEdge, TState> createExpansionPolicy(final AlphaZeroModel<TAction, TState> traversalModel, final RootExplorationProbabilityNoiseSettings rootExplorationProbabilityNoise, final Provider<TAction, AlphaZeroEdge, TState> provider) {
        List<ExpansionPolicy<TAction, AlphaZeroEdge, TState>> expansionPolicies = new ArrayList<>();

        expansionPolicies.add(new AlphaZeroExpansionPolicy<>(EDGE_FACTORY, traversalModel));
        addRootExplorationProbabilityNoiseToIfApplicable(expansionPolicies, rootExplorationProbabilityNoise);

        if (provider.isAllowedToCollect()) {
            expansionPolicies.add(provider::collect);
        }

        return ExpansionPolicyController.provide(expansionPolicies);
    }

    private static TechniqueSelectionConfidenceCalculator<AlphaZeroEdge> createSelectionConfidenceCalculator(final CPuctCalculator cpuctCalculator) {
        if (cpuctCalculator == null) {
            return SELECTION_CONFIDENCE_CALCULATOR;
        }

        return new TechniqueSelectionConfidenceCalculator<>(cpuctCalculator);
    }

    private static <TAction extends Action, TState extends State<TAction, TState>> AlphaZeroSelectionPolicy<TAction, TState> createSelectionPolicy(final RootExplorationProbabilityNoiseSettings rootExplorationProbabilityNoise, final Provider<TAction, AlphaZeroEdge, TState> provider, final AlphaZeroModel<TAction, TState> traversalModel, final CPuctCalculator cpuctCalculator) {
        TechniqueSelectionConfidenceCalculator<AlphaZeroEdge> selectionConfidenceCalculator = createSelectionConfidenceCalculator(cpuctCalculator);
        ExpansionPolicy<TAction, AlphaZeroEdge, TState> expansionPolicy = createExpansionPolicy(traversalModel, rootExplorationProbabilityNoise, provider);

        return new AlphaZeroSelectionPolicyFactory<>(selectionConfidenceCalculator, traversalModel, expansionPolicy).create();
    }

    private static <TAction extends Action, TState extends State<TAction, TState>> BackPropagationPolicy<TAction, AlphaZeroEdge, TState, ?> createBackPropagationPolicy(final BackPropagationType backPropagationType, final BackPropagationObserver<TAction, TState> backPropagationObserver) {
        BackPropagationType fixedBackPropagationType = Objects.requireNonNullElse(backPropagationType, BackPropagationType.REVERSED_ON_BACKTRACK);
        TechniqueBackPropagationStep<TAction, AlphaZeroEdge, TState> backPropagationStep = new TechniqueBackPropagationStep<>(fixedBackPropagationType);

        return new BackPropagationPolicy<>(backPropagationStep, backPropagationObserver);
    }

    private static <TAction extends Action, TState extends State<TAction, TState>> AlphaZeroProposalStrategy<TAction, TState> createProposalStrategy(final TemperatureController temperatureController) {
        UniformRandomSupport randomSupport = new UniformRandomSupport();
        ExplorationRankedActionDecisionMaker<TAction, TState> explorationRankedActionDecisionMaker = new ExplorationRankedActionDecisionMaker<>(randomSupport);
        ExploitationRankedActionDecisionMaker<TAction, TState> exploitationRankedActionDecisionMaker = ExploitationRankedActionDecisionMaker.getInstance();

        return new AlphaZeroProposalStrategy<>(VISITED_ACTION_EFFICIENCY_CALCULATOR, temperatureController, explorationRankedActionDecisionMaker, exploitationRankedActionDecisionMaker);
    }

    private static <TAction extends Action, TState extends State<TAction, TState>> List<ResetHandler> createResetHandlers(final AlphaZeroModel<TAction, TState> traversalModel, final Provider<TAction, AlphaZeroEdge, TState> provider) {
        List<ResetHandler> resetHandlers = new ArrayList<>();

        resetHandlers.add(traversalModel::reset);
        resetHandlers.add(provider::clear);

        return resetHandlers;
    }

    @Builder
    private static <TAction extends Action, TState extends State<TAction, TState>> AlphaZeroMonteCarloTreeSearch<TAction, TState> create(final SearchPolicy searchPolicy, final RootExplorationProbabilityNoiseSettings rootExplorationProbabilityNoise, final CacheType cacheType, final AlphaZeroModel<TAction, TState> traversalModel, final CPuctCalculator cpuctCalculator, final BackPropagationType backPropagationType, final BackPropagationObserver<TAction, TState> backPropagationObserver, final TemperatureController temperatureController) {
        CacheType fixedCacheType = Objects.requireNonNullElse(cacheType, CacheType.NONE);
        Provider<TAction, AlphaZeroEdge, TState> provider = fixedCacheType.create(EDGE_FACTORY);
        AlphaZeroSelectionPolicy<TAction, TState> selectionPolicy = createSelectionPolicy(rootExplorationProbabilityNoise, provider, traversalModel, cpuctCalculator);
        AlphaZeroSimulationRolloutPolicy<TAction, TState> simulationRolloutPolicy = AlphaZeroSimulationRolloutPolicy.getInstance();
        BackPropagationPolicy<TAction, AlphaZeroEdge, TState, ?> backPropagationPolicy = createBackPropagationPolicy(backPropagationType, backPropagationObserver);
        AlphaZeroProposalStrategy<TAction, TState> proposalStrategy = createProposalStrategy(temperatureController);
        List<ResetHandler> resetHandlers = createResetHandlers(traversalModel, provider);
        Mcts<TAction, AlphaZeroEdge, TState> mcts = new Mcts<>(searchPolicy, provider, selectionPolicy, simulationRolloutPolicy, backPropagationPolicy, proposalStrategy, resetHandlers);

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
