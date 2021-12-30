package com.dipasquale.search.mcts;

import com.dipasquale.common.EntryOptimizer;
import com.dipasquale.common.random.float1.RandomSupport;
import com.dipasquale.common.random.float1.UniformRandomSupport;
import com.dipasquale.data.structure.iterator.FlatIterator;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class MonteCarloTreeSearch<T extends State> {
    private static final Comparator<Float> FLOAT_COMPARATOR = Float::compare;
    public static final int IN_PROGRESS = 0;
    public static final int DRAWN = -1;
    private final SimulationPolicy simulationPolicy;
    private final SelectionPolicy<T> selectionPolicy;
    private final SelectionPolicy<T> simulationRolloutPolicy;
    private final BackPropagationPolicy<T> backPropagationPolicy;
    private final StrategyCalculator<T> strategyCalculator;

    private static <T extends State> InitializeChildrenSelectionPolicy<T> createInitializeChildrenSelectionPolicy(final SimulationRolloutType simulationRolloutType) {
        return switch (simulationRolloutType) {
            case STOCHASTIC_CHOICE_DETERMINISTIC_OUTCOME, ALL_STOCHASTIC -> new InitializeChildrenSelectionPolicy<>(new UniformRandomSupport());

            case ALL_DETERMINISTIC -> new InitializeChildrenSelectionPolicy<>(null);
        };
    }

    private static <T extends State> HighestConfidenceSelectionPolicy<T> createHighestConfidenceSelectionPolicy(final ConfidenceCalculator<T> confidenceCalculator) {
        ConfidenceCalculator<T> confidenceCalculatorFixed = Objects.requireNonNullElseGet(confidenceCalculator, UctConfidenceCalculator::new);

        return new HighestConfidenceSelectionPolicy<>(confidenceCalculatorFixed);
    }

    private static <T extends State> SelectionPolicy<T> createSelectionPolicy(final InitializeChildrenSelectionPolicy<T> initializeChildrenSelectionPolicy, final ConfidenceCalculator<T> confidenceCalculator) {
        List<SelectionPolicy<T>> selectionPolicies = new ArrayList<>();

        selectionPolicies.add(initializeChildrenSelectionPolicy);
        selectionPolicies.add(new UnexploredFirstSelectionPolicy<>());
        selectionPolicies.add(createHighestConfidenceSelectionPolicy(confidenceCalculator));

        return new MultiSelectionPolicy<>(selectionPolicies);
    }

    private static <T extends State> SelectionPolicy<T> createSimulationRolloutPolicy(final InitializeChildrenSelectionPolicy<T> initializeChildrenSelectionPolicy, final SimulationRolloutType simulationRolloutType) {
        List<SelectionPolicy<T>> simulationRolloutPolicies = new ArrayList<>();
        RandomSupport randomSupport = new UniformRandomSupport();

        switch (simulationRolloutType) {
            case STOCHASTIC_CHOICE_DETERMINISTIC_OUTCOME -> {
                simulationRolloutPolicies.add(initializeChildrenSelectionPolicy);
                simulationRolloutPolicies.add(new DeterministicSelectionPolicy<>(randomSupport));
            }

            case ALL_DETERMINISTIC -> throw new UnsupportedOperationException();

            case ALL_STOCHASTIC -> simulationRolloutPolicies.add(new StochasticSelectionPolicy<>(randomSupport));
        }

        return new MultiSelectionPolicy<>(simulationRolloutPolicies);
    }

    private static <T extends State> BackPropagationPolicy<T> createBackPropagationPolicy(final SimulationRolloutType simulationRolloutType) {
        return switch (simulationRolloutType) {
            case STOCHASTIC_CHOICE_DETERMINISTIC_OUTCOME, ALL_DETERMINISTIC -> new DeterministicBackPropagationPolicy<>();

            case ALL_STOCHASTIC -> throw new UnsupportedOperationException();
        };
    }

    @Builder
    public static <T extends State> MonteCarloTreeSearch<T> create(final SimulationPolicy simulationPolicy, final ConfidenceCalculator<T> confidenceCalculator, final SimulationRolloutType simulationRolloutType, final StrategyCalculator<T> strategyCalculator) {
        InitializeChildrenSelectionPolicy<T> initializeChildrenSelectionPolicy = createInitializeChildrenSelectionPolicy(simulationRolloutType);
        SelectionPolicy<T> selectionPolicy = createSelectionPolicy(initializeChildrenSelectionPolicy, confidenceCalculator);
        SelectionPolicy<T> simulationRolloutPolicy = createSimulationRolloutPolicy(initializeChildrenSelectionPolicy, simulationRolloutType);
        BackPropagationPolicy<T> backPropagationPolicy = createBackPropagationPolicy(simulationRolloutType);

        return new MonteCarloTreeSearch<>(simulationPolicy, selectionPolicy, simulationRolloutPolicy, backPropagationPolicy, strategyCalculator);
    }

    private SimulationResult<T> simulateNodeRollout(final SearchNode<T> rootSearchNode, final int simulations) {
        SearchNode<T> currentSearchNode = rootSearchNode;
        int currentStatusId;

        for (int depth = 1; true; depth++) {
            if (!simulationPolicy.allowDepth(simulations, depth)) {
                return new SimulationResult<>(currentSearchNode, DRAWN);
            }

            SearchNode<T> childSearchNode = simulationRolloutPolicy.next(simulations, currentSearchNode);

            if (childSearchNode == null) {
                return new SimulationResult<>(currentSearchNode, DRAWN);
            }

            currentSearchNode = childSearchNode;
            currentStatusId = currentSearchNode.getEnvironment().getStatusId();

            if (currentStatusId != IN_PROGRESS) {
                return new SimulationResult<>(currentSearchNode, currentStatusId);
            }
        }
    }

    private SearchNode<T> findBestNode(final Environment<T> environment) {
        if (environment.getStatusId() != IN_PROGRESS) {
            return null;
        }

        int simulations = 1;
        SearchNode<T> rootSearchNode = new SearchNode<>(environment);
        SearchNode<T> promisingSearchNode = selectionPolicy.next(simulations, rootSearchNode);

        if (promisingSearchNode == null) {
            return null;
        }

        do {
            int statusId = promisingSearchNode.getEnvironment().getStatusId();

            switch (statusId) {
                case IN_PROGRESS -> {
                    SimulationResult<T> simulationResult = simulateNodeRollout(promisingSearchNode, simulations);

                    backPropagationPolicy.process(promisingSearchNode, simulationResult.getSearchNode(), simulationResult.getStatusId());
                }

                default -> backPropagationPolicy.process(promisingSearchNode, promisingSearchNode, statusId);
            }

            if (simulationPolicy.allowSimulation(++simulations)) {
                promisingSearchNode = selectionPolicy.next(simulations, rootSearchNode);
            } else {
                promisingSearchNode = null;
            }
        } while (promisingSearchNode != null);

        EntryOptimizer<Float, SearchNode<T>> childSearchNodeOptimizer = new EntryOptimizer<>(FLOAT_COMPARATOR);
        List<Iterator<SearchNode<T>>> childSearchNodeIterators = List.of(rootSearchNode.getExplorableChildren().iterator(), rootSearchNode.getFullyExploredChildren().iterator());
        Iterable<SearchNode<T>> childSearchNodes = () -> FlatIterator.fromIterators(childSearchNodeIterators);

        for (SearchNode<T> childSearchNode : childSearchNodes) {
            float efficiency = strategyCalculator.calculateEfficiency(childSearchNode);

            childSearchNodeOptimizer.collectIfMoreOptimum(efficiency, childSearchNode);
        }

        return childSearchNodeOptimizer.getValue();
    }

    public T findNextState(final Environment<T> environment) {
        simulationPolicy.beginSearch();

        return Optional.ofNullable(findBestNode(environment))
                .map(SearchNode::getState)
                .orElse(null);
    }
}
