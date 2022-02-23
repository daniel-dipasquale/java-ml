package com.dipasquale.search.mcts.core;

import com.dipasquale.common.PairOptimizer;
import com.dipasquale.common.random.float1.RandomSupport;
import com.dipasquale.common.random.float1.UniformRandomSupport;
import com.dipasquale.data.structure.iterator.FlatIterator;
import com.dipasquale.search.mcts.alphazero.AlphaZeroBackPropagationPolicy;
import com.dipasquale.search.mcts.alphazero.AlphaZeroChildrenInitializerTraversalPolicy;
import com.dipasquale.search.mcts.alphazero.AlphaZeroConfidenceCalculator;
import com.dipasquale.search.mcts.alphazero.AlphaZeroEdge;
import com.dipasquale.search.mcts.alphazero.AlphaZeroEdgeFactory;
import com.dipasquale.search.mcts.alphazero.AlphaZeroHeuristic;
import com.dipasquale.search.mcts.alphazero.AlphaZeroSelectionPolicy;
import com.dipasquale.search.mcts.alphazero.ProposalStrategy;
import com.dipasquale.search.mcts.alphazero.RosinCPuctAlgorithm;
import com.dipasquale.search.mcts.classic.ClassicBackPropagationPolicy;
import com.dipasquale.search.mcts.classic.ClassicChildrenInitializerTraversalPolicy;
import com.dipasquale.search.mcts.classic.ClassicEdge;
import com.dipasquale.search.mcts.classic.ClassicEdgeFactory;
import com.dipasquale.search.mcts.classic.ClassicSelectionPolicy;
import com.dipasquale.search.mcts.classic.ClassicSimulationRolloutPolicyFactory;
import com.dipasquale.search.mcts.classic.PrevalentProposalStrategy;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class MonteCarloTreeSearch<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> {
    private static final Comparator<Float> FLOAT_COMPARATOR = Float::compare;
    private static final PrevalentProposalStrategy PREVALENT_PROPOSAL_STRATEGY = new PrevalentProposalStrategy(2f, 0.5f);
    private static final RosinCPuctAlgorithm ROSIN_C_PUCT_ALGORITHM = new RosinCPuctAlgorithm();
    private static final AlphaZeroConfidenceCalculator ALPHA_ZERO_SELECTION_CONFIDENCE_CALCULATOR = new AlphaZeroConfidenceCalculator(ROSIN_C_PUCT_ALGORITHM);
    public static final int IN_PROGRESS = 0;
    public static final int DRAWN = -1;
    private final SearchPolicy searchPolicy;
    private final EdgeFactory<TEdge> edgeFactory;
    private final SearchNodeProvider<TAction, TEdge, TState> nodeProvider;
    private final TraversalPolicy<TAction, TEdge, TState> selectionPolicy;
    private final TraversalPolicy<TAction, TEdge, TState> simulationRolloutPolicy;
    private final BackPropagationPolicy<TAction, TEdge, TState> backPropagationPolicy;
    private final ProposalStrategy<TEdge> proposalStrategy;

    private static <TAction extends Action, TState extends State<TAction, TState>> SearchNodeCache<TAction, ClassicEdge, TState> createNodeCache(final SearchNodeCacheSettings searchNodeCacheSettings, final EdgeFactory<ClassicEdge> edgeFactory) {
        if (searchNodeCacheSettings == null) {
            return null;
        }

        return searchNodeCacheSettings.create(edgeFactory);
    }

    @Builder(builderMethodName = "classicBuilder", builderClassName = "ClassicBuilder")
    public static <TAction extends Action, TState extends State<TAction, TState>> MonteCarloTreeSearch<TAction, ClassicEdge, TState> createClassic(final SearchPolicy searchPolicy, final SearchNodeCacheSettings searchNodeCacheSettings, final ConfidenceCalculator<ClassicEdge> confidenceCalculator, final BackPropagationObserver<TAction, ClassicEdge, TState> backPropagationObserver, final ProposalStrategy<ClassicEdge> proposalStrategy) {
        EdgeFactory<ClassicEdge> edgeFactory = ClassicEdgeFactory.getInstance();
        RandomSupport randomSupport = new UniformRandomSupport();
        SearchNodeCache<TAction, ClassicEdge, TState> nodeCache = createNodeCache(searchNodeCacheSettings, edgeFactory);
        ClassicChildrenInitializerTraversalPolicy<TAction, TState> childrenInitializerTraversalPolicy = new ClassicChildrenInitializerTraversalPolicy<>(edgeFactory, randomSupport, nodeCache);
        ClassicSimulationRolloutPolicyFactory<TAction, TState> simulationRolloutPolicyFactory = new ClassicSimulationRolloutPolicyFactory<>(childrenInitializerTraversalPolicy, randomSupport);
        TraversalPolicy<TAction, ClassicEdge, TState> simulationRolloutPolicy = simulationRolloutPolicyFactory.create();
        BackPropagationPolicy<TAction, ClassicEdge, TState> backPropagationPolicy = new ClassicBackPropagationPolicy<>(backPropagationObserver);
        ProposalStrategy<ClassicEdge> proposalStrategyFixed = Objects.requireNonNullElse(proposalStrategy, PREVALENT_PROPOSAL_STRATEGY);

        if (confidenceCalculator == null) {
            return new MonteCarloTreeSearch<>(searchPolicy, edgeFactory, nodeCache, simulationRolloutPolicy, simulationRolloutPolicy, backPropagationPolicy, proposalStrategyFixed);
        }

        ClassicSelectionPolicy<TAction, TState> selectionPolicy = new ClassicSelectionPolicy<>(childrenInitializerTraversalPolicy, confidenceCalculator);

        return new MonteCarloTreeSearch<>(searchPolicy, edgeFactory, nodeCache, selectionPolicy, simulationRolloutPolicy, backPropagationPolicy, proposalStrategyFixed);
    }

    private static <TAction extends Action, TState extends State<TAction, TState>> SearchNodeProvider<TAction, AlphaZeroEdge, TState> createNodeProvider(final SearchNodeProviderSettings searchNodeProviderSettings, final EdgeFactory<AlphaZeroEdge> edgeFactory) {
        if (searchNodeProviderSettings == null) {
            return null;
        }

        return searchNodeProviderSettings.create(edgeFactory);
    }

    @Builder(builderMethodName = "alphaZeroBuilder", builderClassName = "AlphaZeroBuilder")
    public static <TAction extends Action, TState extends State<TAction, TState>> MonteCarloTreeSearch<TAction, AlphaZeroEdge, TState> createAlphaZero(final SearchPolicy searchPolicy, final SearchNodeProviderSettings searchNodeProviderSettings, final AlphaZeroHeuristic<TAction, TState> heuristic, final ConfidenceCalculator<AlphaZeroEdge> confidenceCalculator, final BackPropagationObserver<TAction, AlphaZeroEdge, TState> backPropagationObserver, final ProposalStrategy<AlphaZeroEdge> proposalStrategy) {
        EdgeFactory<AlphaZeroEdge> edgeFactory = AlphaZeroEdgeFactory.getInstance();
        SearchNodeProvider<TAction, AlphaZeroEdge, TState> nodeProvider = createNodeProvider(searchNodeProviderSettings, edgeFactory);
        AlphaZeroChildrenInitializerTraversalPolicy<TAction, TState> childrenInitializerTraversalPolicy = new AlphaZeroChildrenInitializerTraversalPolicy<>(edgeFactory, heuristic, nodeProvider);
        ConfidenceCalculator<AlphaZeroEdge> confidenceCalculatorFixed = Objects.requireNonNullElse(confidenceCalculator, ALPHA_ZERO_SELECTION_CONFIDENCE_CALCULATOR);
        AlphaZeroSelectionPolicy<TAction, TState> selectionPolicy = new AlphaZeroSelectionPolicy<>(childrenInitializerTraversalPolicy, confidenceCalculatorFixed);
        BackPropagationPolicy<TAction, AlphaZeroEdge, TState> backPropagationPolicy = new AlphaZeroBackPropagationPolicy<>(backPropagationObserver);

        return new MonteCarloTreeSearch<>(searchPolicy, edgeFactory, nodeProvider, selectionPolicy, null, backPropagationPolicy, proposalStrategy);
    }

    private SearchNode<TAction, TEdge, TState> getRootNode(final TState state) {
        if (nodeProvider != null) {
            SearchNode<TAction, TEdge, TState> rootNode = nodeProvider.provide(state);

            if (rootNode != null) {
                return rootNode;
            }
        }

        return SearchNodeProvider.createRootNode(edgeFactory, state, 0);
    }

    private SimulationResult<TAction, TEdge, TState> simulateNodeRollout(final SearchNode<TAction, TEdge, TState> selectedNode, final int simulations) {
        int selectedDepth = selectedNode.getDepth();
        int currentStatusId;

        for (SearchNode<TAction, TEdge, TState> currentNode = selectedNode; true; ) {
            int nextDepth = currentNode.getDepth() + 1;
            int simulatedNextDepth = nextDepth - selectedDepth;

            if (!searchPolicy.allowDepth(simulations, nextDepth, simulatedNextDepth)) {
                return new SimulationResult<>(currentNode, IN_PROGRESS);
            }

            SearchNode<TAction, TEdge, TState> childNode = simulationRolloutPolicy.next(simulations, currentNode);

            if (childNode == null) {
                return new SimulationResult<>(currentNode, IN_PROGRESS);
            }

            currentNode = childNode;
            currentStatusId = currentNode.getState().getStatusId();

            if (currentStatusId != IN_PROGRESS) {
                return new SimulationResult<>(currentNode, currentStatusId);
            }
        }
    }

    private SearchNode<TAction, TEdge, TState> findBestNode(final TState state) {
        if (state.getStatusId() != IN_PROGRESS) {
            return null;
        }

        SearchNode<TAction, TEdge, TState> rootNode = getRootNode(state);
        int simulations = rootNode.getEdge().getVisited() + 1;
        SearchNode<TAction, TEdge, TState> promisingNode = selectionPolicy.next(simulations, rootNode);

        if (promisingNode == null) {
            return null;
        }

        do {
            int statusId = promisingNode.getState().getStatusId();

            if (simulationRolloutPolicy != null && statusId == IN_PROGRESS) {
                backPropagationPolicy.process(simulateNodeRollout(promisingNode, simulations));
            } else {
                backPropagationPolicy.process(promisingNode, statusId);
            }

            if (searchPolicy.allowSimulation(++simulations)) {
                promisingNode = selectionPolicy.next(simulations, rootNode);
            } else {
                promisingNode = null;
            }
        } while (promisingNode != null);

        PairOptimizer<Float, SearchNode<TAction, TEdge, TState>> childNodeOptimizer = new PairOptimizer<>(FLOAT_COMPARATOR);
        List<Iterator<SearchNode<TAction, TEdge, TState>>> childNodeIterators = List.of(rootNode.getExplorableChildren().iterator(), rootNode.getFullyExploredChildren().iterator());
        Iterable<SearchNode<TAction, TEdge, TState>> childNodes = () -> FlatIterator.fromIterators(childNodeIterators);

        for (SearchNode<TAction, TEdge, TState> childNode : childNodes) {
            float efficiency = proposalStrategy.calculateEfficiency(simulations, childNode.getEdge());

            childNodeOptimizer.replaceValueIfBetter(efficiency, childNode);
        }

        return childNodeOptimizer.getValue();
    }

    private TAction findBestAction(final TState state) {
        SearchNode<TAction, TEdge, TState> bestNode = findBestNode(state);

        if (bestNode == null) {
            return null;
        }

        return bestNode.getAction();
    }

    public TAction proposeNextAction(final TState state) {
        searchPolicy.begin();

        try {
            return findBestAction(state);
        } finally {
            searchPolicy.end();
        }
    }
}
