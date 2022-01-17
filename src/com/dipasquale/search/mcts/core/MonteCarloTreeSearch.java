package com.dipasquale.search.mcts.core;

import com.dipasquale.common.EntryOptimizer;
import com.dipasquale.common.random.float1.RandomSupport;
import com.dipasquale.common.random.float1.UniformRandomSupport;
import com.dipasquale.data.structure.iterator.FlatIterator;
import com.dipasquale.search.mcts.alphazero.AlphaZeroBackPropagationPolicy;
import com.dipasquale.search.mcts.alphazero.AlphaZeroChildrenInitializerTraversalPolicy;
import com.dipasquale.search.mcts.alphazero.AlphaZeroConfidenceCalculator;
import com.dipasquale.search.mcts.alphazero.AlphaZeroEdge;
import com.dipasquale.search.mcts.alphazero.AlphaZeroEdgeFactory;
import com.dipasquale.search.mcts.alphazero.AlphaZeroHeuristic;
import com.dipasquale.search.mcts.alphazero.AlphaZeroSelectionPolicyFactory;
import com.dipasquale.search.mcts.alphazero.RosinCPuctAlgorithm;
import com.dipasquale.search.mcts.classic.ClassicBackPropagationPolicy;
import com.dipasquale.search.mcts.classic.ClassicChildrenInitializerTraversalPolicy;
import com.dipasquale.search.mcts.classic.ClassicEdge;
import com.dipasquale.search.mcts.classic.ClassicEdgeFactory;
import com.dipasquale.search.mcts.classic.ClassicPrevalentStrategyCalculator;
import com.dipasquale.search.mcts.classic.ClassicSelectionPolicyFactory;
import com.dipasquale.search.mcts.classic.ClassicSimulationRolloutPolicyFactory;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class MonteCarloTreeSearch<TState extends State, TEdge extends Edge, TEnvironment extends Environment<TState, TEnvironment>> {
    private static final Comparator<Float> FLOAT_COMPARATOR = Float::compare;
    private static final ClassicPrevalentStrategyCalculator CLASSIC_PREVALENT_STRATEGY_CALCULATOR = new ClassicPrevalentStrategyCalculator(2f, 0.5f);
    private static final RosinCPuctAlgorithm ROSIN_C_PUCT_ALGORITHM = new RosinCPuctAlgorithm();
    private static final AlphaZeroConfidenceCalculator ALPHA_ZERO_CONFIDENCE_CALCULATOR = new AlphaZeroConfidenceCalculator(ROSIN_C_PUCT_ALGORITHM);
    private static final MostVisitedStrategyCalculator<AlphaZeroEdge> ALPHA_ZERO_MOST_VISITED_STRATEGY_CALCULATOR = new MostVisitedStrategyCalculator<>();
    public static final int IN_PROGRESS = 0;
    public static final int DRAWN = -1;
    private final SearchPolicy searchPolicy;
    private final EdgeFactory<TEdge> edgeFactory;
    private final TraversalPolicy<TState, TEdge, TEnvironment> selectionPolicy;
    private final TraversalPolicy<TState, TEdge, TEnvironment> simulationRolloutPolicy;
    private final BackPropagationPolicy<TState, TEdge, TEnvironment> backPropagationPolicy;
    private final StrategyCalculator<TEdge> strategyCalculator;

    @Builder(builderMethodName = "classicBuilder", builderClassName = "ClassicBuilder")
    public static <TState extends State, TEnvironment extends Environment<TState, TEnvironment>> MonteCarloTreeSearch<TState, ClassicEdge, TEnvironment> createClassic(final SearchPolicy searchPolicy, final ConfidenceCalculator<ClassicEdge> confidenceCalculator, final BackPropagationObserver<TState, ClassicEdge, TEnvironment> backPropagationObserver, final StrategyCalculator<ClassicEdge> strategyCalculator) {
        EdgeFactory<ClassicEdge> edgeFactory = ClassicEdgeFactory.getInstance();
        RandomSupport randomSupport = new UniformRandomSupport();
        ClassicChildrenInitializerTraversalPolicy<TState, TEnvironment> childrenInitializerTraversalPolicy = new ClassicChildrenInitializerTraversalPolicy<>(edgeFactory, randomSupport);
        ClassicSimulationRolloutPolicyFactory<TState, TEnvironment> simulationRolloutPolicyFactory = new ClassicSimulationRolloutPolicyFactory<>(childrenInitializerTraversalPolicy, randomSupport);
        TraversalPolicy<TState, ClassicEdge, TEnvironment> simulationRolloutPolicy = simulationRolloutPolicyFactory.create();
        BackPropagationPolicy<TState, ClassicEdge, TEnvironment> backPropagationPolicy = new ClassicBackPropagationPolicy<>(backPropagationObserver);
        StrategyCalculator<ClassicEdge> strategyCalculatorFixed = Objects.requireNonNullElse(strategyCalculator, CLASSIC_PREVALENT_STRATEGY_CALCULATOR);

        if (confidenceCalculator == null) {
            return new MonteCarloTreeSearch<>(searchPolicy, edgeFactory, simulationRolloutPolicy, simulationRolloutPolicy, backPropagationPolicy, strategyCalculatorFixed);
        }

        ClassicSelectionPolicyFactory<TState, TEnvironment> selectionPolicyFactory = new ClassicSelectionPolicyFactory<>(childrenInitializerTraversalPolicy, confidenceCalculator);
        TraversalPolicy<TState, ClassicEdge, TEnvironment> selectionPolicy = selectionPolicyFactory.create();

        return new MonteCarloTreeSearch<>(searchPolicy, edgeFactory, selectionPolicy, simulationRolloutPolicy, backPropagationPolicy, strategyCalculatorFixed);
    }

    @Builder(builderMethodName = "alphaZeroBuilder", builderClassName = "AlphaZeroBuilder")
    public static <TState extends State, TEnvironment extends Environment<TState, TEnvironment>> MonteCarloTreeSearch<TState, AlphaZeroEdge, TEnvironment> createAlphaZero(final SearchPolicy searchPolicy, final AlphaZeroHeuristic<TState, TEnvironment> heuristic, final ConfidenceCalculator<AlphaZeroEdge> confidenceCalculator, final BackPropagationObserver<TState, AlphaZeroEdge, TEnvironment> backPropagationObserver, final StrategyCalculator<AlphaZeroEdge> strategyCalculator) {
        EdgeFactory<AlphaZeroEdge> edgeFactory = AlphaZeroEdgeFactory.getInstance();
        AlphaZeroChildrenInitializerTraversalPolicy<TState, TEnvironment> childrenInitializerTraversalPolicy = new AlphaZeroChildrenInitializerTraversalPolicy<>(edgeFactory, heuristic);
        ConfidenceCalculator<AlphaZeroEdge> confidenceCalculatorFixed = Objects.requireNonNullElse(confidenceCalculator, ALPHA_ZERO_CONFIDENCE_CALCULATOR);
        AlphaZeroSelectionPolicyFactory<TState, TEnvironment> selectionPolicyFactory = new AlphaZeroSelectionPolicyFactory<>(childrenInitializerTraversalPolicy, confidenceCalculatorFixed);
        TraversalPolicy<TState, AlphaZeroEdge, TEnvironment> selectionPolicy = selectionPolicyFactory.create();
        BackPropagationPolicy<TState, AlphaZeroEdge, TEnvironment> backPropagationPolicy = new AlphaZeroBackPropagationPolicy<>(backPropagationObserver);
        StrategyCalculator<AlphaZeroEdge> strategyCalculatorFixed = Objects.requireNonNullElse(strategyCalculator, ALPHA_ZERO_MOST_VISITED_STRATEGY_CALCULATOR);

        return new MonteCarloTreeSearch<>(searchPolicy, edgeFactory, selectionPolicy, selectionPolicy, backPropagationPolicy, strategyCalculatorFixed);
    }

    private SimulationResult<TState, TEdge, TEnvironment> simulateNodeRollout(final SearchNode<TState, TEdge, TEnvironment> selectedNode, final int simulations) {
        int selectedDepth = selectedNode.getDepth();
        int currentStatusId;

        for (SearchNode<TState, TEdge, TEnvironment> currentNode = selectedNode; true; ) {
            int nextDepth = currentNode.getDepth() + 1;
            int simulatedNextDepth = nextDepth - selectedDepth;

            if (!searchPolicy.allowDepth(simulations, nextDepth, simulatedNextDepth)) {
                return new SimulationResult<>(currentNode, IN_PROGRESS);
            }

            SearchNode<TState, TEdge, TEnvironment> childNode = simulationRolloutPolicy.next(simulations, currentNode);

            if (childNode == null) {
                return new SimulationResult<>(currentNode, IN_PROGRESS);
            }

            currentNode = childNode;
            currentStatusId = currentNode.getEnvironment().getStatusId();

            if (currentStatusId != IN_PROGRESS) {
                return new SimulationResult<>(currentNode, currentStatusId);
            }
        }
    }

    private SearchNode<TState, TEdge, TEnvironment> findBestNode(final TEnvironment environment) {
        if (environment.getStatusId() != IN_PROGRESS) {
            return null;
        }

        int simulations = 1;
        SearchNode<TState, TEdge, TEnvironment> rootNode = new SearchNode<>(environment, edgeFactory.create());
        SearchNode<TState, TEdge, TEnvironment> promisingNode = selectionPolicy.next(simulations, rootNode);

        if (promisingNode == null) {
            return null;
        }

        do {
            int statusId = promisingNode.getEnvironment().getStatusId();

            switch (statusId) {
                case IN_PROGRESS -> backPropagationPolicy.process(simulateNodeRollout(promisingNode, simulations));

                default -> backPropagationPolicy.process(promisingNode, statusId);
            }

            if (searchPolicy.allowSimulation(++simulations)) {
                promisingNode = selectionPolicy.next(simulations, rootNode);
            } else {
                promisingNode = null;
            }
        } while (promisingNode != null);

        EntryOptimizer<Float, SearchNode<TState, TEdge, TEnvironment>> childNodeOptimizer = new EntryOptimizer<>(FLOAT_COMPARATOR);
        List<Iterator<SearchNode<TState, TEdge, TEnvironment>>> childNodeIterators = List.of(rootNode.getExplorableChildren().iterator(), rootNode.getFullyExploredChildren().iterator());
        Iterable<SearchNode<TState, TEdge, TEnvironment>> childNodes = () -> FlatIterator.fromIterators(childNodeIterators);

        for (SearchNode<TState, TEdge, TEnvironment> childNode : childNodes) {
            float efficiency = strategyCalculator.calculateEfficiency(childNode.getEdge());

            childNodeOptimizer.replaceValueIfMoreOptimum(efficiency, childNode);
        }

        return childNodeOptimizer.getValue();
    }

    private TState findBestState(final TEnvironment environment) {
        SearchNode<TState, TEdge, TEnvironment> bestNode = findBestNode(environment);

        if (bestNode == null) {
            return null;
        }

        return bestNode.getState();
    }

    public TState proposeNextState(final TEnvironment environment) {
        searchPolicy.begin();

        try {
            return findBestState(environment);
        } finally {
            searchPolicy.end();
        }
    }
}
