package com.dipasquale.search.mcts;

import com.dipasquale.common.random.float1.RandomSupport;
import com.dipasquale.common.random.float1.UniformRandomSupport;
import com.dipasquale.data.structure.iterator.FlatIterator;
import com.dipasquale.search.mcts.alphazero.AlphaZeroBackPropagationPolicy;
import com.dipasquale.search.mcts.alphazero.AlphaZeroChildrenInitializerTraversalPolicy;
import com.dipasquale.search.mcts.alphazero.AlphaZeroEdge;
import com.dipasquale.search.mcts.alphazero.AlphaZeroEdgeFactory;
import com.dipasquale.search.mcts.alphazero.AlphaZeroHeuristic;
import com.dipasquale.search.mcts.alphazero.AlphaZeroSelectionConfidenceCalculator;
import com.dipasquale.search.mcts.alphazero.AlphaZeroSelectionPolicy;
import com.dipasquale.search.mcts.alphazero.RosinCPuctAlgorithm;
import com.dipasquale.search.mcts.alphazero.SearchNodeProviderSettings;
import com.dipasquale.search.mcts.classic.ClassicBackPropagationPolicy;
import com.dipasquale.search.mcts.classic.ClassicChildrenInitializerTraversalPolicy;
import com.dipasquale.search.mcts.classic.ClassicEdge;
import com.dipasquale.search.mcts.classic.ClassicEdgeFactory;
import com.dipasquale.search.mcts.classic.ClassicSelectionPolicy;
import com.dipasquale.search.mcts.classic.ClassicSimulationRolloutPolicyFactory;
import com.dipasquale.search.mcts.classic.PrevalentActionEfficiencyCalculator;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class MonteCarloTreeSearch<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> {
    private static final RosinCPuctAlgorithm ROSIN_C_PUCT_ALGORITHM = new RosinCPuctAlgorithm();
    private static final AlphaZeroSelectionConfidenceCalculator ALPHA_ZERO_SELECTION_CONFIDENCE_CALCULATOR = new AlphaZeroSelectionConfidenceCalculator(ROSIN_C_PUCT_ALGORITHM);
    public static final int INITIAL_ACTION_ID = -1;
    public static final int INITIAL_PARTICIPANT_ID = -1;
    public static final int IN_PROGRESS_STATUS_ID = 0;
    public static final int DRAWN_STATUS_ID = -1;
    private final SearchPolicy searchPolicy;
    private final EdgeFactory<TEdge> edgeFactory;
    private final SearchNodeProvider<TAction, TEdge, TState> nodeProvider;
    private final TraversalPolicy<TAction, TEdge, TState> selectionPolicy;
    private final TraversalPolicy<TAction, TEdge, TState> simulationRolloutPolicy;
    private final BackPropagationPolicy<TAction, TEdge, TState> backPropagationPolicy;
    private final SearchNodeProposalStrategy<TAction, TEdge, TState> nodeProposalStrategy;

    private static <TAction extends Action, TState extends State<TAction, TState>> CacheSearchNodeProvider<TAction, ClassicEdge, TState> createCacheNodeProvider(final SearchNodeCacheSettings nodeCacheSettings, final EdgeFactory<ClassicEdge> edgeFactory) {
        if (nodeCacheSettings == null) {
            return null;
        }

        return nodeCacheSettings.create(edgeFactory);
    }

    @Builder(builderMethodName = "classicBuilder", builderClassName = "ClassicBuilder")
    public static <TAction extends Action, TState extends State<TAction, TState>> MonteCarloTreeSearch<TAction, ClassicEdge, TState> createClassic(final SearchPolicy searchPolicy, final SearchNodeCacheSettings nodeCacheSettings, final SelectionConfidenceCalculator<ClassicEdge> selectionConfidenceCalculator, final LeafNodeObserver<TAction, ClassicEdge, TState> leafNodeObserver, final ActionEfficiencyCalculator<TAction, ClassicEdge> actionEfficiencyCalculator) {
        EdgeFactory<ClassicEdge> edgeFactory = ClassicEdgeFactory.getInstance();
        RandomSupport randomSupport = new UniformRandomSupport();
        CacheSearchNodeProvider<TAction, ClassicEdge, TState> cacheNodeProvider = createCacheNodeProvider(nodeCacheSettings, edgeFactory);
        ClassicChildrenInitializerTraversalPolicy<TAction, TState> childrenInitializerTraversalPolicy = new ClassicChildrenInitializerTraversalPolicy<>(edgeFactory, randomSupport, cacheNodeProvider);
        ClassicSimulationRolloutPolicyFactory<TAction, TState> simulationRolloutPolicyFactory = new ClassicSimulationRolloutPolicyFactory<>(childrenInitializerTraversalPolicy, randomSupport);
        TraversalPolicy<TAction, ClassicEdge, TState> simulationRolloutPolicy = simulationRolloutPolicyFactory.create();
        BackPropagationPolicy<TAction, ClassicEdge, TState> backPropagationPolicy = new ClassicBackPropagationPolicy<>(leafNodeObserver);
        ActionEfficiencyCalculator<TAction, ClassicEdge> actionEfficiencyCalculatorFixed = Objects.requireNonNullElseGet(actionEfficiencyCalculator, () -> new PrevalentActionEfficiencyCalculator<>(2f, 0.5f));
        DeterministicOutcomeSearchNodeProposalStrategy<TAction, ClassicEdge, TState> searchNodeProposalStrategy = new DeterministicOutcomeSearchNodeProposalStrategy<>(actionEfficiencyCalculatorFixed);

        if (selectionConfidenceCalculator == null) {
            return new MonteCarloTreeSearch<>(searchPolicy, edgeFactory, cacheNodeProvider, simulationRolloutPolicy, simulationRolloutPolicy, backPropagationPolicy, searchNodeProposalStrategy);
        }

        ClassicSelectionPolicy<TAction, TState> selectionPolicy = new ClassicSelectionPolicy<>(childrenInitializerTraversalPolicy, selectionConfidenceCalculator);

        return new MonteCarloTreeSearch<>(searchPolicy, edgeFactory, cacheNodeProvider, selectionPolicy, simulationRolloutPolicy, backPropagationPolicy, searchNodeProposalStrategy);
    }

    private static <TAction extends Action, TState extends State<TAction, TState>> SearchNodeProvider<TAction, AlphaZeroEdge, TState> createNodeProvider(final SearchNodeProviderSettings nodeProviderSettings, final EdgeFactory<AlphaZeroEdge> edgeFactory) {
        if (nodeProviderSettings == null) {
            return null;
        }

        return nodeProviderSettings.create(edgeFactory);
    }

    private static <TAction extends Action, TState extends State<TAction, TState>> SearchNodeProposalStrategy<TAction, AlphaZeroEdge, TState> createNodeProposalStrategy(final AlphaZeroHeuristic<TAction, TState> traversalHeuristic, final ActionEfficiencyCalculator<TAction, AlphaZeroEdge> actionEfficiencyCalculator) {
        if (traversalHeuristic.isEveryOutcomeDeterministic()) {
            return new DeterministicOutcomeSearchNodeProposalStrategy<>(actionEfficiencyCalculator);
        }

        return new NonDeterministicOutcomeSearchNodeProposalStrategy<>(actionEfficiencyCalculator);
    }

    @Builder(builderMethodName = "alphaZeroBuilder", builderClassName = "AlphaZeroBuilder")
    public static <TAction extends Action, TState extends State<TAction, TState>> MonteCarloTreeSearch<TAction, AlphaZeroEdge, TState> createAlphaZero(final SearchPolicy searchPolicy, final SearchNodeProviderSettings nodeProviderSettings, final AlphaZeroHeuristic<TAction, TState> traversalHeuristic, final SelectionConfidenceCalculator<AlphaZeroEdge> selectionConfidenceCalculator, final LeafNodeObserver<TAction, AlphaZeroEdge, TState> leafNodeObserver, final ActionEfficiencyCalculator<TAction, AlphaZeroEdge> actionEfficiencyCalculator) {
        EdgeFactory<AlphaZeroEdge> edgeFactory = AlphaZeroEdgeFactory.getInstance();
        SearchNodeProvider<TAction, AlphaZeroEdge, TState> nodeProvider = createNodeProvider(nodeProviderSettings, edgeFactory);
        AlphaZeroChildrenInitializerTraversalPolicy<TAction, TState> childrenInitializerTraversalPolicy = new AlphaZeroChildrenInitializerTraversalPolicy<>(edgeFactory, traversalHeuristic, nodeProvider);
        SelectionConfidenceCalculator<AlphaZeroEdge> selectionConfidenceCalculatorFixed = Objects.requireNonNullElse(selectionConfidenceCalculator, ALPHA_ZERO_SELECTION_CONFIDENCE_CALCULATOR);
        AlphaZeroSelectionPolicy<TAction, TState> selectionPolicy = new AlphaZeroSelectionPolicy<>(childrenInitializerTraversalPolicy, selectionConfidenceCalculatorFixed);
        BackPropagationPolicy<TAction, AlphaZeroEdge, TState> backPropagationPolicy = new AlphaZeroBackPropagationPolicy<>(leafNodeObserver);
        SearchNodeProposalStrategy<TAction, AlphaZeroEdge, TState> searchNodeProposalStrategy = createNodeProposalStrategy(traversalHeuristic, actionEfficiencyCalculator);

        return new MonteCarloTreeSearch<>(searchPolicy, edgeFactory, nodeProvider, selectionPolicy, null, backPropagationPolicy, searchNodeProposalStrategy);
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

    private SimulationResult<TAction, TEdge, TState> simulateNodeRollout(final int simulations, final SearchNode<TAction, TEdge, TState> selectedNode) {
        int selectedDepth = selectedNode.getDepth();
        int currentStatusId;

        for (SearchNode<TAction, TEdge, TState> currentNode = selectedNode; true; ) {
            int nextDepth = currentNode.getDepth() + 1;
            int simulatedNextDepth = nextDepth - selectedDepth;

            if (!searchPolicy.allowDepth(simulations, nextDepth, simulatedNextDepth)) {
                return new SimulationResult<>(currentNode, IN_PROGRESS_STATUS_ID);
            }

            SearchNode<TAction, TEdge, TState> childNode = simulationRolloutPolicy.next(simulations, currentNode);

            if (childNode == null) {
                return new SimulationResult<>(currentNode, IN_PROGRESS_STATUS_ID); // TODO: consider removing the additional statusId since it should be the same as the node being returned
            }

            currentNode = childNode;
            currentStatusId = currentNode.getState().getStatusId();

            if (currentStatusId != IN_PROGRESS_STATUS_ID) {
                return new SimulationResult<>(currentNode, currentStatusId);
            }
        }
    }

    private SearchNode<TAction, TEdge, TState> findBestNode(final TState state) {
        if (state.getStatusId() != IN_PROGRESS_STATUS_ID) {
            return null;
        }

        SearchNode<TAction, TEdge, TState> rootNode = getRootNode(state);
        int visitedRootNode = rootNode.getEdge().getVisited();
        int simulations = visitedRootNode;

        for (int simulationsPlusOne = simulations + 1; simulationsPlusOne - simulations == 1 && searchPolicy.allowSimulation(simulationsPlusOne); simulationsPlusOne++) {
            SearchNode<TAction, TEdge, TState> promisingNode = selectionPolicy.next(simulationsPlusOne, rootNode);

            if (promisingNode != null) {
                int statusId = promisingNode.getState().getStatusId();

                if (simulationRolloutPolicy != null && statusId == IN_PROGRESS_STATUS_ID) {
                    SimulationResult<TAction, TEdge, TState> simulationResult = simulateNodeRollout(simulations, promisingNode);

                    backPropagationPolicy.process(simulationResult);
                } else {
                    backPropagationPolicy.process(promisingNode, statusId);
                }

                simulations = simulationsPlusOne;
            }
        }

        if (simulations == visitedRootNode) {
            return null;
        }

        List<Iterator<SearchNode<TAction, TEdge, TState>>> childNodeIterators = List.of(rootNode.getExplorableChildren().iterator(), rootNode.getFullyExploredChildren().iterator());

        return nodeProposalStrategy.proposeBestNode(simulations, () -> FlatIterator.fromIterators(childNodeIterators));
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
