package com.dipasquale.search.mcts;

import com.dipasquale.common.EntryOptimizer;
import com.dipasquale.common.random.float1.RandomSupport;
import com.dipasquale.common.random.float1.UniformRandomSupport;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class MonteCarloTreeSearch<T extends State> {
    private static final Comparator<Float> FLOAT_COMPARATOR = Float::compare;
    public static final int IN_PROGRESS = 0;
    public static final int DRAWN = -1;
    private final SimulationPolicy simulationPolicy;
    private final ExplorationPolicy<T> explorationPolicy;
    private final ExplorationPolicy<T> simulationRolloutPolicy;
    private final SimulationResultFactory<T> simulationResultFactory;
    private final BackPropagationPolicy<T> backPropagationPolicy;
    private final StrategyCalculator<T> strategyCalculator;

    private static <T extends State> InitializeChildrenExplorationPolicy<T> createInitializeChildrenExplorationPolicy(final SimulationRolloutType simulationRolloutType) {
        if (simulationRolloutType == SimulationRolloutType.ALL_DETERMINISTIC) {
            return new InitializeChildrenExplorationPolicy<>(null);
        }

        return new InitializeChildrenExplorationPolicy<>(new UniformRandomSupport());
    }

    private static <T extends State> HighestConfidenceExplorationPolicy<T> createHighestConfidenceExplorationPolicy(final SelectionPolicy<T> selectionPolicy) {
        SelectionPolicy<T> selectionPolicyFixed = Objects.requireNonNullElseGet(selectionPolicy, UctSelectionPolicy::new);

        return new HighestConfidenceExplorationPolicy<>(selectionPolicyFixed);
    }

    private static <T extends State> ExplorationPolicy<T> createExplorationPolicy(final InitializeChildrenExplorationPolicy<T> initializeChildrenExplorationPolicy, final SelectionPolicy<T> selectionPolicy) {
        List<ExplorationPolicy<T>> explorationPolicies = new ArrayList<>();

        explorationPolicies.add(initializeChildrenExplorationPolicy);
        explorationPolicies.add(new UnexploredFirstExplorationPolicy<>());
        explorationPolicies.add(createHighestConfidenceExplorationPolicy(selectionPolicy));

        return new MultiExplorationPolicy<>(explorationPolicies);
    }

    private static <T extends State> ExplorationPolicy<T> createSimulationRolloutPolicy(final InitializeChildrenExplorationPolicy<T> initializeChildrenExplorationPolicy, final SimulationRolloutType simulationRolloutType) {
        List<ExplorationPolicy<T>> simulationRolloutPolicies = new ArrayList<>();
        RandomSupport randomSupport = new UniformRandomSupport();

        switch (simulationRolloutType) {
            case STOCHASTIC_CHOICE_DETERMINISTIC_OUTCOME -> {
                simulationRolloutPolicies.add(initializeChildrenExplorationPolicy);
                simulationRolloutPolicies.add(new StatefulRandomExplorationPolicy<>(randomSupport));
            }

            case ALL_DETERMINISTIC -> throw new UnsupportedOperationException();

            case ALL_STOCHASTIC -> simulationRolloutPolicies.add(new StatelessRandomExplorationPolicy<>(randomSupport));
        }

        return new MultiExplorationPolicy<>(simulationRolloutPolicies);
    }

    private static <T extends State> SimulationResultFactory<T> createSimulationResultFactory(final SimulationRolloutType simulationRolloutType) {
        return switch (simulationRolloutType) {
            case STOCHASTIC_CHOICE_DETERMINISTIC_OUTCOME -> new StatefulSimulationResultFactory<>();

            case ALL_DETERMINISTIC -> throw new UnsupportedOperationException();

            case ALL_STOCHASTIC -> new StatelessSimulationResultFactory<>();
        };
    }

    private static <T extends State> BackPropagationPolicy<T> createBackPropagationPolicy(final SimulationRolloutType simulationRolloutType) {
        List<BackPropagationPolicy<T>> backPropagationPolicies = new ArrayList<>();

        if (simulationRolloutType == SimulationRolloutType.STOCHASTIC_CHOICE_DETERMINISTIC_OUTCOME) {
            backPropagationPolicies.add(new AvoidDoubleDipBackPropagationPolicy<>());
        }

        backPropagationPolicies.add(new DefaultBackPropagationPolicy<>());

        return new MultiBackPropagationPolicy<>(backPropagationPolicies);
    }

    @Builder
    public static <T extends State> MonteCarloTreeSearch<T> create(final SimulationPolicy simulationPolicy, final SelectionPolicy<T> selectionPolicy, final SimulationRolloutType simulationRolloutType, final StrategyCalculator<T> strategyCalculator) {
        InitializeChildrenExplorationPolicy<T> initializeChildrenExplorationPolicy = createInitializeChildrenExplorationPolicy(simulationRolloutType);
        ExplorationPolicy<T> explorationPolicy = createExplorationPolicy(initializeChildrenExplorationPolicy, selectionPolicy);
        ExplorationPolicy<T> simulationRolloutPolicy = createSimulationRolloutPolicy(initializeChildrenExplorationPolicy, simulationRolloutType);
        SimulationResultFactory<T> simulationResultFactory = createSimulationResultFactory(simulationRolloutType);
        BackPropagationPolicy<T> backPropagationPolicy = createBackPropagationPolicy(simulationRolloutType);

        return new MonteCarloTreeSearch<>(simulationPolicy, explorationPolicy, simulationRolloutPolicy, simulationResultFactory, backPropagationPolicy, strategyCalculator);
    }

    private SimulationResult<T> simulateNodeRollout(final SearchNode<T> rootSearchNode, final int simulations) {
        SearchNode<T> currentSearchNode = rootSearchNode;
        int currentStatusId;

        for (int depth = 1; true; depth++) {
            if (!simulationPolicy.allowDepth(simulations, depth)) {
                return simulationResultFactory.create(rootSearchNode, currentSearchNode, DRAWN);
            }

            SearchNode<T> childSearchNode = simulationRolloutPolicy.next(simulations, currentSearchNode);

            if (childSearchNode == null) {
                return simulationResultFactory.create(rootSearchNode, currentSearchNode, DRAWN);
            }

            currentSearchNode = childSearchNode;
            currentStatusId = currentSearchNode.getEnvironment().getStatusId();

            if (currentStatusId != IN_PROGRESS) {
                return simulationResultFactory.create(rootSearchNode, currentSearchNode, currentStatusId);
            }
        }
    }

    private SearchNode<T> findBestNode(final Environment<T> environment) {
        if (environment.getStatusId() != IN_PROGRESS) {
            return null;
        }

        int simulations = 1;
        SearchNode<T> rootSearchNode = new SearchNode<>(environment);
        SearchNode<T> promisingSearchNode = explorationPolicy.next(simulations, rootSearchNode);

        if (promisingSearchNode == null) {
            return null;
        }

        int aborted = 0;

        for (boolean allowed = true; allowed; ) {
            int statusId = promisingSearchNode.getEnvironment().getStatusId();

            boolean backPropagated = switch (statusId) {
                case IN_PROGRESS -> {
                    SimulationResult<T> simulationResult = simulateNodeRollout(promisingSearchNode, simulations);

                    yield backPropagationPolicy.process(simulationResult.getSearchNode(), simulationResult.getStatusId());
                }

                default -> backPropagationPolicy.process(promisingSearchNode, statusId);
            };

            allowed = simulationPolicy.allowSimulation(++simulations, backPropagated ? aborted : ++aborted);

            if (allowed) {
                promisingSearchNode = explorationPolicy.next(simulations, rootSearchNode);
            }
        }

        EntryOptimizer<Float, SearchNode<T>> childSearchNodeOptimizer = new EntryOptimizer<>(FLOAT_COMPARATOR);

        for (SearchNode<T> childSearchNode : rootSearchNode.getExploredChildren()) {
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
