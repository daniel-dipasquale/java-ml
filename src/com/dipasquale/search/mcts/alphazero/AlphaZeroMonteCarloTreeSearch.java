package com.dipasquale.search.mcts.alphazero;

import com.dipasquale.common.random.float1.RandomSupport;
import com.dipasquale.common.random.float2.DirichletDistributionSupport;
import com.dipasquale.common.random.float2.GammaDistributionSupport;
import com.dipasquale.common.random.float2.GaussianDistributionSupport;
import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.EdgeFactory;
import com.dipasquale.search.mcts.Mcts;
import com.dipasquale.search.mcts.MonteCarloTreeSearch;
import com.dipasquale.search.mcts.ResetHandler;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.SearchNodeGroupProvider;
import com.dipasquale.search.mcts.SearchNodeResult;
import com.dipasquale.search.mcts.StandardSearchNode;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.alphazero.expansion.AlphaZeroExpansionPolicy;
import com.dipasquale.search.mcts.alphazero.expansion.ExplorationProbabilityNoiseRootExpansionPolicy;
import com.dipasquale.search.mcts.alphazero.expansion.RootExplorationProbabilityNoiseSettings;
import com.dipasquale.search.mcts.alphazero.initialization.AlphaZeroStandardInitializationContext;
import com.dipasquale.search.mcts.alphazero.proposal.AlphaZeroProposalStrategy;
import com.dipasquale.search.mcts.alphazero.proposal.ExploitationRankedActionDecisionMaker;
import com.dipasquale.search.mcts.alphazero.proposal.ExplorationRankedActionDecisionMaker;
import com.dipasquale.search.mcts.alphazero.proposal.TemperatureController;
import com.dipasquale.search.mcts.alphazero.proposal.VisitedActionEfficiencyCalculator;
import com.dipasquale.search.mcts.alphazero.selection.AlphaZeroModel;
import com.dipasquale.search.mcts.buffer.Buffer;
import com.dipasquale.search.mcts.buffer.BufferType;
import com.dipasquale.search.mcts.expansion.ExpansionPolicy;
import com.dipasquale.search.mcts.expansion.ExpansionPolicyController;
import com.dipasquale.search.mcts.heuristic.propagation.HeuristicBackPropagationStep;
import com.dipasquale.search.mcts.heuristic.selection.CPuctAlgorithm;
import com.dipasquale.search.mcts.heuristic.selection.HeuristicUctAlgorithm;
import com.dipasquale.search.mcts.heuristic.selection.RosinCPuctAlgorithm;
import com.dipasquale.search.mcts.initialization.InitializationContext;
import com.dipasquale.search.mcts.propagation.BackPropagationObserver;
import com.dipasquale.search.mcts.propagation.BackPropagationPolicy;
import com.dipasquale.search.mcts.propagation.BackPropagationType;
import com.dipasquale.search.mcts.proposal.ProposalStrategy;
import com.dipasquale.search.mcts.seek.SeekPolicy;
import com.dipasquale.search.mcts.seek.SeekStrategy;
import com.dipasquale.search.mcts.selection.SelectionPolicy;
import com.dipasquale.search.mcts.simulation.SimulationPolicy;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class AlphaZeroMonteCarloTreeSearch<TAction extends Action, TState extends State<TAction, TState>> implements MonteCarloTreeSearch<TAction, TState> {
    private static final RosinCPuctAlgorithm ROSIN_C_PUCT_ALGORITHM = new RosinCPuctAlgorithm();
    private static final HeuristicUctAlgorithm<AlphaZeroEdge> SELECTION_CONFIDENCE_CALCULATOR = new HeuristicUctAlgorithm<>(ROSIN_C_PUCT_ALGORITHM);
    private static final VisitedActionEfficiencyCalculator VISITED_ACTION_EFFICIENCY_CALCULATOR = VisitedActionEfficiencyCalculator.getInstance();
    private final Mcts<TAction, AlphaZeroEdge, TState, ?> mcts;

    private static HeuristicUctAlgorithm<AlphaZeroEdge> createSelectionConfidenceCalculator(final CPuctAlgorithm cpuctAlgorithm) {
        if (cpuctAlgorithm == null) {
            return SELECTION_CONFIDENCE_CALCULATOR;
        }

        return new HeuristicUctAlgorithm<>(cpuctAlgorithm);
    }

    private static <TAction extends Action, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, AlphaZeroEdge, TState, TSearchNode>> Buffer<TAction, AlphaZeroEdge, TState, TSearchNode> createBuffer(final BufferType bufferType, final InitializationContext<TAction, AlphaZeroEdge, TState, TSearchNode> initializationContext) {
        BufferType fixedBufferType = Objects.requireNonNullElse(bufferType, BufferType.DISABLED);

        return fixedBufferType.create(initializationContext);
    }

    private static <TAction extends Action, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, AlphaZeroEdge, TState, TSearchNode>> void addRootExplorationProbabilityNoiseToIfApplicable(final List<ExpansionPolicy<TAction, AlphaZeroEdge, TState, TSearchNode>> expansionPolicies, final RootExplorationProbabilityNoiseSettings rootExplorationProbabilityNoise) {
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

    private static <TAction extends Action, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, AlphaZeroEdge, TState, TSearchNode>> ExpansionPolicy<TAction, AlphaZeroEdge, TState, TSearchNode> createExpansionPolicy(final AlphaZeroModel<TAction, TState, TSearchNode> traversalModel, final InitializationContext<TAction, AlphaZeroEdge, TState, TSearchNode> initializationContext, final RootExplorationProbabilityNoiseSettings rootExplorationProbabilityNoise, final Buffer<TAction, AlphaZeroEdge, TState, TSearchNode> buffer) {
        List<ExpansionPolicy<TAction, AlphaZeroEdge, TState, TSearchNode>> expansionPolicies = new ArrayList<>();
        EdgeFactory<AlphaZeroEdge> edgeFactory = initializationContext.getEdgeFactory();
        SearchNodeGroupProvider<TAction, AlphaZeroEdge, TState, TSearchNode> searchNodeGroupProvider = initializationContext.getSearchNodeGroupProvider();

        expansionPolicies.add(new AlphaZeroExpansionPolicy<>(traversalModel, edgeFactory, searchNodeGroupProvider));
        addRootExplorationProbabilityNoiseToIfApplicable(expansionPolicies, rootExplorationProbabilityNoise);
        expansionPolicies.add(buffer::put);

        return ExpansionPolicyController.provide(expansionPolicies);
    }

    private static <TAction extends Action, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, AlphaZeroEdge, TState, TSearchNode>> HeuristicBackPropagationStep<TAction, AlphaZeroEdge, TState, TSearchNode> createBackPropagationStep(final BackPropagationType backPropagationType) {
        BackPropagationType fixedBackPropagationType = Objects.requireNonNullElse(backPropagationType, BackPropagationType.REVERSED_ON_BACKTRACK);

        return new HeuristicBackPropagationStep<>(fixedBackPropagationType);
    }

    private static <TAction extends Action, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, AlphaZeroEdge, TState, TSearchNode>> AlphaZeroProposalStrategy<TAction, TState, TSearchNode> createProposalStrategy(final TemperatureController temperatureController, final RandomSupport randomSupport) {
        ExplorationRankedActionDecisionMaker<TAction, TState, TSearchNode> explorationRankedActionDecisionMaker = new ExplorationRankedActionDecisionMaker<>(randomSupport);
        ExploitationRankedActionDecisionMaker<TAction, TState, TSearchNode> exploitationRankedActionDecisionMaker = ExploitationRankedActionDecisionMaker.getInstance();

        return new AlphaZeroProposalStrategy<>(VISITED_ACTION_EFFICIENCY_CALCULATOR, temperatureController, explorationRankedActionDecisionMaker, exploitationRankedActionDecisionMaker);
    }

    private static List<ResetHandler> createResetHandlers(final AlphaZeroModel<?, ?, ?> traversalModel, final Buffer<?, AlphaZeroEdge, ?, ?> buffer) {
        List<ResetHandler> resetHandlers = new ArrayList<>();

        resetHandlers.add(traversalModel::reset);
        resetHandlers.add(buffer::clear);

        return resetHandlers;
    }

    private static <TAction extends Action, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, AlphaZeroEdge, TState, TSearchNode>> Mcts<TAction, AlphaZeroEdge, TState, TSearchNode> createMcts(final BufferType bufferType, final InitializationContext<TAction, AlphaZeroEdge, TState, TSearchNode> initializationContext, final AlphaZeroModel<TAction, TState, TSearchNode> traversalModel, final RootExplorationProbabilityNoiseSettings rootExplorationProbabilityNoise, final HeuristicUctAlgorithm<AlphaZeroEdge> selectionConfidenceCalculator, final BackPropagationType backPropagationType, final BackPropagationObserver<TAction, TState> backPropagationObserver, final TemperatureController temperatureController) {
        Buffer<TAction, AlphaZeroEdge, TState, TSearchNode> buffer = createBuffer(bufferType, initializationContext);
        ExpansionPolicy<TAction, AlphaZeroEdge, TState, TSearchNode> expansionPolicy = createExpansionPolicy(traversalModel, initializationContext, rootExplorationProbabilityNoise, buffer);
        SelectionPolicy<TAction, AlphaZeroEdge, TState, TSearchNode> selectionPolicy = initializationContext.createSelectionPolicy(selectionConfidenceCalculator, expansionPolicy);
        SimulationPolicy<TAction, AlphaZeroEdge, TState, TSearchNode> simulationPolicy = initializationContext.createSimulationPolicy(expansionPolicy);
        HeuristicBackPropagationStep<TAction, AlphaZeroEdge, TState, TSearchNode> backPropagationStep = createBackPropagationStep(backPropagationType);
        BackPropagationPolicy<TAction, AlphaZeroEdge, TState, TSearchNode> backPropagationPolicy = initializationContext.createBackPropagationPolicy(backPropagationStep, backPropagationObserver);
        SeekStrategy<TAction, AlphaZeroEdge, TState, TSearchNode> seekStrategy = initializationContext.createSearchStrategy(selectionPolicy, simulationPolicy, backPropagationPolicy);
        ProposalStrategy<TAction, AlphaZeroEdge, TState, TSearchNode> proposalStrategy = createProposalStrategy(temperatureController, initializationContext.createRandomSupport());
        List<ResetHandler> resetHandlers = createResetHandlers(traversalModel, buffer);

        return new Mcts<>(buffer, seekStrategy, proposalStrategy, resetHandlers);
    }

    @Builder
    private static <TAction extends Action, TState extends State<TAction, TState>> AlphaZeroMonteCarloTreeSearch<TAction, TState> create(final SeekPolicy seekPolicy, final RootExplorationProbabilityNoiseSettings rootExplorationProbabilityNoise, final BufferType bufferType, final AlphaZeroModel<TAction, TState, StandardSearchNode<TAction, AlphaZeroEdge, TState>> traversalModel, final CPuctAlgorithm cpuctAlgorithm, final BackPropagationType backPropagationType, final BackPropagationObserver<TAction, TState> backPropagationObserver, final TemperatureController temperatureController) {
        InitializationContext<TAction, AlphaZeroEdge, TState, StandardSearchNode<TAction, AlphaZeroEdge, TState>> initializationContext = new AlphaZeroStandardInitializationContext<>(AlphaZeroEdgeFactory.getInstance(), traversalModel, seekPolicy);
        HeuristicUctAlgorithm<AlphaZeroEdge> selectionConfidenceCalculator = createSelectionConfidenceCalculator(cpuctAlgorithm);
        Mcts<TAction, AlphaZeroEdge, TState, StandardSearchNode<TAction, AlphaZeroEdge, TState>> mcts = createMcts(bufferType, initializationContext, traversalModel, rootExplorationProbabilityNoise, selectionConfidenceCalculator, backPropagationType, backPropagationObserver, temperatureController);

        return new AlphaZeroMonteCarloTreeSearch<>(mcts);
    }

    @Override
    public SearchNodeResult<TAction, TState> proposeNext(final SearchNodeResult<TAction, TState> searchNodeResult) {
        return mcts.proposeNext(searchNodeResult);
    }

    @Override
    public void reset() {
        mcts.reset();
    }
}
