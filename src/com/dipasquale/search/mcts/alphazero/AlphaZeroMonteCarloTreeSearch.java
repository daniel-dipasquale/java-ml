package com.dipasquale.search.mcts.alphazero;

import com.dipasquale.common.random.float1.RandomSupport;
import com.dipasquale.common.random.float2.DirichletDistributionSupport;
import com.dipasquale.common.random.float2.GammaDistributionSupport;
import com.dipasquale.common.random.float2.GaussianDistributionSupport;
import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.BackPropagationObserver;
import com.dipasquale.search.mcts.BackPropagationPolicy;
import com.dipasquale.search.mcts.Buffer;
import com.dipasquale.search.mcts.BufferType;
import com.dipasquale.search.mcts.EdgeFactory;
import com.dipasquale.search.mcts.ExpansionPolicy;
import com.dipasquale.search.mcts.ExpansionPolicyController;
import com.dipasquale.search.mcts.InitializationContext;
import com.dipasquale.search.mcts.Mcts;
import com.dipasquale.search.mcts.MonteCarloTreeSearch;
import com.dipasquale.search.mcts.ProposalStrategy;
import com.dipasquale.search.mcts.ResetHandler;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.SearchNodeGroupProvider;
import com.dipasquale.search.mcts.SearchPolicy;
import com.dipasquale.search.mcts.SearchStrategy;
import com.dipasquale.search.mcts.SelectionPolicy;
import com.dipasquale.search.mcts.SimulationRolloutPolicy;
import com.dipasquale.search.mcts.StandardInitializationContext;
import com.dipasquale.search.mcts.StandardSearchNode;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.TraversalPolicy;
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
    private static final AlphaZeroEdgeFactory EDGE_FACTORY = AlphaZeroEdgeFactory.getInstance();
    private static final RosinCPuctCalculator ROSIN_C_PUCT_ALGORITHM = new RosinCPuctCalculator();
    private static final TechniqueSelectionConfidenceCalculator<AlphaZeroEdge> SELECTION_CONFIDENCE_CALCULATOR = new TechniqueSelectionConfidenceCalculator<>(ROSIN_C_PUCT_ALGORITHM);
    private static final VisitedActionEfficiencyCalculator VISITED_ACTION_EFFICIENCY_CALCULATOR = VisitedActionEfficiencyCalculator.getInstance();
    private final Mcts<TAction, AlphaZeroEdge, TState, ?> mcts;

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

    private static <TAction extends Action, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, AlphaZeroEdge, TState, TSearchNode>> ExpansionPolicy<TAction, AlphaZeroEdge, TState, TSearchNode> createExpansionPolicy(final AlphaZeroModel<TAction, TState, TSearchNode> traversalModel, final InitializationContext<TAction, AlphaZeroEdge, TState, TSearchNode, TechniqueBackPropagationStep.Context> initializationContext, final RootExplorationProbabilityNoiseSettings rootExplorationProbabilityNoise, final Buffer<TAction, AlphaZeroEdge, TState, TSearchNode> buffer) {
        List<ExpansionPolicy<TAction, AlphaZeroEdge, TState, TSearchNode>> expansionPolicies = new ArrayList<>();
        EdgeFactory<AlphaZeroEdge> edgeFactory = initializationContext.getEdgeFactory();
        SearchNodeGroupProvider<TAction, AlphaZeroEdge, TState, TSearchNode> searchNodeGroupProvider = initializationContext.getSearchNodeGroupProvider();

        expansionPolicies.add(new AlphaZeroExpansionPolicy<>(traversalModel, edgeFactory, searchNodeGroupProvider));
        addRootExplorationProbabilityNoiseToIfApplicable(expansionPolicies, rootExplorationProbabilityNoise);
        expansionPolicies.add(buffer::store);

        return ExpansionPolicyController.provide(expansionPolicies);
    }

    private static TechniqueSelectionConfidenceCalculator<AlphaZeroEdge> createSelectionConfidenceCalculator(final CPuctCalculator cpuctCalculator) {
        if (cpuctCalculator == null) {
            return SELECTION_CONFIDENCE_CALCULATOR;
        }

        return new TechniqueSelectionConfidenceCalculator<>(cpuctCalculator);
    }

    private static <TAction extends Action, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, AlphaZeroEdge, TState, TSearchNode>> SelectionPolicy<TAction, AlphaZeroEdge, TState, TSearchNode> createSelectionPolicy(final CPuctCalculator cpuctCalculator, final InitializationContext<TAction, AlphaZeroEdge, TState, TSearchNode, TechniqueBackPropagationStep.Context> initializationContext, final AlphaZeroModel<TAction, TState, TSearchNode> traversalModel, final RootExplorationProbabilityNoiseSettings rootExplorationProbabilityNoise, final Buffer<TAction, AlphaZeroEdge, TState, TSearchNode> buffer) {
        TechniqueSelectionConfidenceCalculator<AlphaZeroEdge> selectionConfidenceCalculator = createSelectionConfidenceCalculator(cpuctCalculator);
        TraversalPolicy<TAction, AlphaZeroEdge, TState, TSearchNode> intentionalTraversalPolicy = initializationContext.createIntentionalTraversalPolicy(selectionConfidenceCalculator);
        ExpansionPolicy<TAction, AlphaZeroEdge, TState, TSearchNode> expansionPolicy = createExpansionPolicy(traversalModel, initializationContext, rootExplorationProbabilityNoise, buffer);
        RandomSupport randomSupport = initializationContext.createRandomSupport();
        AlphaZeroSelectionPolicyFactory<TAction, TState, TSearchNode> selectionPolicyFactory = new AlphaZeroSelectionPolicyFactory<>(traversalModel, intentionalTraversalPolicy, expansionPolicy, randomSupport);

        return selectionPolicyFactory.create();
    }

    private static <TAction extends Action, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, AlphaZeroEdge, TState, TSearchNode>> BackPropagationPolicy<TAction, AlphaZeroEdge, TState, TSearchNode, TechniqueBackPropagationStep.Context> createBackPropagationPolicy(final BackPropagationType backPropagationType, final BackPropagationObserver<TAction, TState> backPropagationObserver) {
        BackPropagationType fixedBackPropagationType = Objects.requireNonNullElse(backPropagationType, BackPropagationType.REVERSED_ON_BACKTRACK);
        TechniqueBackPropagationStep<TAction, AlphaZeroEdge, TState, TSearchNode> backPropagationStep = new TechniqueBackPropagationStep<>(fixedBackPropagationType);

        return new BackPropagationPolicy<>(backPropagationStep, backPropagationObserver);
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

    private static <TAction extends Action, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, AlphaZeroEdge, TState, TSearchNode>> Mcts<TAction, AlphaZeroEdge, TState, TSearchNode> createMcts(final InitializationContext<TAction, AlphaZeroEdge, TState, TSearchNode, TechniqueBackPropagationStep.Context> initializationContext, final SearchPolicy searchPolicy, final RootExplorationProbabilityNoiseSettings rootExplorationProbabilityNoise, final BufferType bufferType, final AlphaZeroModel<TAction, TState, TSearchNode> traversalModel, final CPuctCalculator cpuctCalculator, final BackPropagationType backPropagationType, final BackPropagationObserver<TAction, TState> backPropagationObserver, final TemperatureController temperatureController) {
        BufferType fixedBufferType = Objects.requireNonNullElse(bufferType, BufferType.DISABLED);
        Buffer<TAction, AlphaZeroEdge, TState, TSearchNode> buffer = fixedBufferType.create(initializationContext);
        SelectionPolicy<TAction, AlphaZeroEdge, TState, TSearchNode> selectionPolicy = createSelectionPolicy(cpuctCalculator, initializationContext, traversalModel, rootExplorationProbabilityNoise, buffer);
        SimulationRolloutPolicy<TAction, AlphaZeroEdge, TState, TSearchNode> simulationRolloutPolicy = AlphaZeroSimulationRolloutPolicy.getInstance();
        BackPropagationPolicy<TAction, AlphaZeroEdge, TState, TSearchNode, TechniqueBackPropagationStep.Context> backPropagationPolicy = createBackPropagationPolicy(backPropagationType, backPropagationObserver);
        SearchStrategy<TAction, AlphaZeroEdge, TState, TSearchNode> searchStrategy = initializationContext.createSearchStrategy(searchPolicy, selectionPolicy, simulationRolloutPolicy, backPropagationPolicy);
        ProposalStrategy<TAction, AlphaZeroEdge, TState, TSearchNode> proposalStrategy = createProposalStrategy(temperatureController, initializationContext.createRandomSupport());
        List<ResetHandler> resetHandlers = createResetHandlers(traversalModel, buffer);

        return new Mcts<>(buffer, searchStrategy, proposalStrategy, resetHandlers);
    }

    @Builder
    private static <TAction extends Action, TState extends State<TAction, TState>> AlphaZeroMonteCarloTreeSearch<TAction, TState> create(final SearchPolicy searchPolicy, final RootExplorationProbabilityNoiseSettings rootExplorationProbabilityNoise, final BufferType bufferType, final AlphaZeroModel<TAction, TState, StandardSearchNode<TAction, AlphaZeroEdge, TState>> traversalModel, final CPuctCalculator cpuctCalculator, final BackPropagationType backPropagationType, final BackPropagationObserver<TAction, TState> backPropagationObserver, final TemperatureController temperatureController) {
        InitializationContext<TAction, AlphaZeroEdge, TState, StandardSearchNode<TAction, AlphaZeroEdge, TState>, TechniqueBackPropagationStep.Context> initializationContext = new StandardInitializationContext<>(EDGE_FACTORY);
        Mcts<TAction, AlphaZeroEdge, TState, StandardSearchNode<TAction, AlphaZeroEdge, TState>> mcts = createMcts(initializationContext, searchPolicy, rootExplorationProbabilityNoise, bufferType, traversalModel, cpuctCalculator, backPropagationType, backPropagationObserver, temperatureController);

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
