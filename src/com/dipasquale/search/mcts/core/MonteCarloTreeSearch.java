package com.dipasquale.search.mcts.core;

import com.dipasquale.common.EntryOptimizer;
import com.dipasquale.common.random.float1.RandomSupport;
import com.dipasquale.common.random.float1.UniformRandomSupport;
import com.dipasquale.data.structure.iterator.FlatIterator;
import com.dipasquale.search.mcts.alphazero.AlphaZeroBackPropagationPolicy;
import com.dipasquale.search.mcts.alphazero.AlphaZeroChildrenInitializerSelectionPolicy;
import com.dipasquale.search.mcts.alphazero.AlphaZeroConfidenceCalculator;
import com.dipasquale.search.mcts.alphazero.AlphaZeroHeuristic;
import com.dipasquale.search.mcts.alphazero.AlphaZeroSearchEdge;
import com.dipasquale.search.mcts.alphazero.AlphaZeroSearchEdgeFactory;
import com.dipasquale.search.mcts.alphazero.AlphaZeroSelectionPolicyFactory;
import com.dipasquale.search.mcts.alphazero.RosinCPuctAlgorithm;
import com.dipasquale.search.mcts.classic.ClassicChildrenInitializerSelectionPolicy;
import com.dipasquale.search.mcts.classic.ClassicConfidenceCalculator;
import com.dipasquale.search.mcts.classic.ClassicDeterministicBackPropagationPolicy;
import com.dipasquale.search.mcts.classic.ClassicPrevalentStrategyCalculator;
import com.dipasquale.search.mcts.classic.ClassicSearchEdge;
import com.dipasquale.search.mcts.classic.ClassicSearchEdgeFactory;
import com.dipasquale.search.mcts.classic.ClassicSelectionPolicyFactory;
import com.dipasquale.search.mcts.classic.ClassicSimulationRolloutPolicyFactory;
import com.dipasquale.search.mcts.classic.ClassicSimulationRolloutType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class MonteCarloTreeSearch<TState extends SearchState, TEdge extends SearchEdge> {
    private static final Comparator<Float> FLOAT_COMPARATOR = Float::compare;
    private static final ClassicConfidenceCalculator CLASSIC_CONFIDENCE_CALCULATOR = new ClassicConfidenceCalculator();
    private static final ClassicPrevalentStrategyCalculator CLASSIC_PREVALENT_STRATEGY_CALCULATOR = new ClassicPrevalentStrategyCalculator(2f, 0.5f);
    private static final RosinCPuctAlgorithm ROSIN_C_PUCT_ALGORITHM = new RosinCPuctAlgorithm();
    private static final AlphaZeroConfidenceCalculator ALPHA_ZERO_CONFIDENCE_CALCULATOR = new AlphaZeroConfidenceCalculator(ROSIN_C_PUCT_ALGORITHM);
    private static final MostVisitedStrategyCalculator<AlphaZeroSearchEdge> ALPHA_ZERO_MOST_VISITED_STRATEGY_CALCULATOR = new MostVisitedStrategyCalculator<>();
    public static final int IN_PROGRESS = 0;
    public static final int DRAWN = -1;
    private final SimulationPolicy simulationPolicy;
    private final SearchEdgeFactory<TEdge> edgeFactory;
    private final SelectionPolicy<TState, TEdge> selectionPolicy;
    private final SelectionPolicy<TState, TEdge> simulationRolloutPolicy;
    private final BackPropagationPolicy<TState, TEdge> backPropagationPolicy;
    private final StrategyCalculator<TEdge> strategyCalculator;

    @Builder(builderMethodName = "classicBuilder")
    public static <T extends SearchState> MonteCarloTreeSearch<T, ClassicSearchEdge> createClassic(final SimulationPolicy simulationPolicy, final ClassicSimulationRolloutType simulationRolloutType, final ConfidenceCalculator<ClassicSearchEdge> confidenceCalculator, final StrategyCalculator<ClassicSearchEdge> strategyCalculator) {
        SearchEdgeFactory<ClassicSearchEdge> edgeFactory = ClassicSearchEdgeFactory.getInstance();
        RandomSupport randomSupport = new UniformRandomSupport();
        ClassicChildrenInitializerSelectionPolicy<T> childrenInitializerSelectionPolicy = new ClassicChildrenInitializerSelectionPolicy<>(edgeFactory, randomSupport);
        ConfidenceCalculator<ClassicSearchEdge> confidenceCalculatorFixed = Objects.requireNonNullElse(confidenceCalculator, CLASSIC_CONFIDENCE_CALCULATOR);
        ClassicSelectionPolicyFactory<T> selectionPolicyFactory = new ClassicSelectionPolicyFactory<>(childrenInitializerSelectionPolicy, confidenceCalculatorFixed);
        SelectionPolicy<T, ClassicSearchEdge> selectionPolicy = selectionPolicyFactory.create();
        ClassicSimulationRolloutPolicyFactory<T> simulationRolloutPolicyFactory = new ClassicSimulationRolloutPolicyFactory<>(simulationRolloutType, childrenInitializerSelectionPolicy, edgeFactory, randomSupport);
        SelectionPolicy<T, ClassicSearchEdge> simulationRolloutPolicy = simulationRolloutPolicyFactory.create();
        BackPropagationPolicy<T, ClassicSearchEdge> backPropagationPolicy = new ClassicDeterministicBackPropagationPolicy<>();
        StrategyCalculator<ClassicSearchEdge> strategyCalculatorFixed = Objects.requireNonNullElse(strategyCalculator, CLASSIC_PREVALENT_STRATEGY_CALCULATOR);

        return new MonteCarloTreeSearch<>(simulationPolicy, edgeFactory, selectionPolicy, simulationRolloutPolicy, backPropagationPolicy, strategyCalculatorFixed);
    }

    public static <T extends SearchState> MonteCarloTreeSearch<T, AlphaZeroSearchEdge> createAlphaZero(final SimulationPolicy simulationPolicy, final AlphaZeroHeuristic<T> heuristic, final ConfidenceCalculator<AlphaZeroSearchEdge> confidenceCalculator, final StrategyCalculator<AlphaZeroSearchEdge> strategyCalculator) {
        SearchEdgeFactory<AlphaZeroSearchEdge> edgeFactory = AlphaZeroSearchEdgeFactory.getInstance();
        AlphaZeroChildrenInitializerSelectionPolicy<T> childrenInitializerSelectionPolicy = new AlphaZeroChildrenInitializerSelectionPolicy<>(edgeFactory, heuristic);
        ConfidenceCalculator<AlphaZeroSearchEdge> confidenceCalculatorFixed = Objects.requireNonNullElse(confidenceCalculator, ALPHA_ZERO_CONFIDENCE_CALCULATOR);
        AlphaZeroSelectionPolicyFactory<T> selectionPolicyFactory = new AlphaZeroSelectionPolicyFactory<>(childrenInitializerSelectionPolicy, confidenceCalculatorFixed);
        SelectionPolicy<T, AlphaZeroSearchEdge> selectionPolicy = selectionPolicyFactory.create();
        BackPropagationPolicy<T, AlphaZeroSearchEdge> backPropagationPolicy = new AlphaZeroBackPropagationPolicy<>();
        StrategyCalculator<AlphaZeroSearchEdge> strategyCalculatorFixed = Objects.requireNonNullElse(strategyCalculator, ALPHA_ZERO_MOST_VISITED_STRATEGY_CALCULATOR);

        return new MonteCarloTreeSearch<>(simulationPolicy, edgeFactory, selectionPolicy, selectionPolicy, backPropagationPolicy, strategyCalculatorFixed);
    }

    private SimulationResult<TState, TEdge> simulateNodeRollout(final SearchNode<TState, TEdge> rootNode, final int simulations) {
        SearchNode<TState, TEdge> currentNode = rootNode;
        int currentStatusId;

        for (int depth = 1; true; depth++) {
            if (!simulationPolicy.allowDepth(simulations, depth)) {
                return new SimulationResult<>(currentNode, IN_PROGRESS);
            }

            SearchNode<TState, TEdge> childNode = simulationRolloutPolicy.next(simulations, currentNode);

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

    private SearchNode<TState, TEdge> findBestNode(final Environment<TState> environment) {
        if (environment.getStatusId() != IN_PROGRESS) {
            return null;
        }

        int simulations = 1;
        SearchNode<TState, TEdge> rootNode = new SearchNode<>(environment, edgeFactory.create(null));
        SearchNode<TState, TEdge> promisingNode = selectionPolicy.next(simulations, rootNode);

        if (promisingNode == null) {
            return null;
        }

        do {
            int statusId = promisingNode.getEnvironment().getStatusId();

            switch (statusId) {
                case IN_PROGRESS -> {
                    SimulationResult<TState, TEdge> simulationResult = simulateNodeRollout(promisingNode, simulations);

                    backPropagationPolicy.process(promisingNode, simulationResult.getNode(), simulationResult.getStatusId());
                }

                default -> backPropagationPolicy.process(promisingNode, promisingNode, statusId);
            }

            if (simulationPolicy.allowSimulation(++simulations)) {
                promisingNode = selectionPolicy.next(simulations, rootNode);
            } else {
                promisingNode = null;
            }
        } while (promisingNode != null);

        EntryOptimizer<Float, SearchNode<TState, TEdge>> childNodeOptimizer = new EntryOptimizer<>(FLOAT_COMPARATOR);
        List<Iterator<SearchNode<TState, TEdge>>> childNodeIterators = List.of(rootNode.getExplorableChildren().iterator(), rootNode.getFullyExploredChildren().iterator());
        Iterable<SearchNode<TState, TEdge>> childNodes = () -> FlatIterator.fromIterators(childNodeIterators);

        for (SearchNode<TState, TEdge> childNode : childNodes) {
            float efficiency = strategyCalculator.calculateEfficiency(childNode.getEdge());

            childNodeOptimizer.collectIfMoreOptimum(efficiency, childNode);
        }

        return childNodeOptimizer.getValue();
    }

    public TState findNextState(final Environment<TState> environment) {
        simulationPolicy.beginSearch();

        return Optional.ofNullable(findBestNode(environment))
                .map(SearchNode::getState)
                .orElse(null);
    }
}
