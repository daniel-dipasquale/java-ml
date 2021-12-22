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
    static final int IN_PROGRESS = 0;
    static final int DRAWN = -1;
    private final SimulationPolicy simulationPolicy;
    private final ExplorationPolicy<T> explorationPolicy;
    private final ExplorationPolicy<T> simulationRolloutPolicy;
    private final SimulationResultFactory<T> simulationResultFactory;
    private final BackPropagationPolicy<T> backPropagationPolicy;
    private final StrategyCalculator<T> strategyCalculator;

    private static <T extends State> HighestUctExplorationPolicy<T> createHighestUctExplorationPolicy(final UctCalculator<T> uctCalculator) {
        UctCalculator<T> uctCalculatorFixed = Objects.requireNonNullElseGet(uctCalculator, DefaultUctCalculator::new);

        return new HighestUctExplorationPolicy<>(uctCalculatorFixed);
    }

    private static <T extends State> ExplorationPolicy<T> createExplorationPolicy(final InitializeChildrenExplorationPolicy<T> initializeChildrenExplorationPolicy, final UctCalculator<T> uctCalculator) {
        List<ExplorationPolicy<T>> explorationPolicies = new ArrayList<>();

        explorationPolicies.add(initializeChildrenExplorationPolicy);
        explorationPolicies.add(new UnexploredFirstExplorationPolicy<>());
        explorationPolicies.add(createHighestUctExplorationPolicy(uctCalculator));

        return new MultiExplorationPolicy<>(explorationPolicies);
    }

    private static <T extends State> ExplorationPolicy<T> createSimulationRolloutPolicy(final InitializeChildrenExplorationPolicy<T> initializeChildrenExplorationPolicy, final SimulationRolloutType simulationRolloutType, final RandomSupport randomSupport) {
        List<ExplorationPolicy<T>> simulationRolloutPolicies = new ArrayList<>();

        switch (simulationRolloutType) {
            case DETERMINISTIC -> {
                simulationRolloutPolicies.add(initializeChildrenExplorationPolicy);
                simulationRolloutPolicies.add(new StatefulRandomExplorationPolicy<>(randomSupport));
            }

            case RANDOM -> simulationRolloutPolicies.add(new StatelessRandomExplorationPolicy<>(randomSupport));
        }

        return new MultiExplorationPolicy<>(simulationRolloutPolicies);
    }

    private static <T extends State> SimulationResultFactory<T> createSimulationResultFactory(final SimulationRolloutType simulationRolloutType) {
        return switch (simulationRolloutType) {
            case DETERMINISTIC -> new StatefulSimulationResultFactory<>();

            case RANDOM -> new StatelessSimulationResultFactory<>();
        };
    }

    private static <T extends State> BackPropagationPolicy<T> createBackPropagationPolicy(final SimulationRolloutType simulationRolloutType) {
        List<BackPropagationPolicy<T>> backPropagationPolicies = new ArrayList<>();

        if (simulationRolloutType == SimulationRolloutType.DETERMINISTIC) {
            backPropagationPolicies.add(new AvoidDoubleDipBackPropagationPolicy<>());
        }

        backPropagationPolicies.add(new DefaultBackPropagationPolicy<>());

        return new MultiBackPropagationPolicy<>(backPropagationPolicies);
    }

    @Builder
    public static <T extends State> MonteCarloTreeSearch<T> create(final SimulationPolicy simulationPolicy, final UctCalculator<T> uctCalculator, final SimulationRolloutType simulationRolloutType, final StrategyCalculator<T> strategyCalculator) {
        RandomSupport randomSupport = new UniformRandomSupport();
        InitializeChildrenExplorationPolicy<T> initializeChildrenExplorationPolicy = new InitializeChildrenExplorationPolicy<>(randomSupport);
        ExplorationPolicy<T> explorationPolicy = createExplorationPolicy(initializeChildrenExplorationPolicy, uctCalculator);
        ExplorationPolicy<T> simulationRolloutPolicy = createSimulationRolloutPolicy(initializeChildrenExplorationPolicy, simulationRolloutType, randomSupport);
        SimulationResultFactory<T> simulationResultFactory = createSimulationResultFactory(simulationRolloutType);
        BackPropagationPolicy<T> backPropagationPolicy = createBackPropagationPolicy(simulationRolloutType);

        return new MonteCarloTreeSearch<>(simulationPolicy, explorationPolicy, simulationRolloutPolicy, simulationResultFactory, backPropagationPolicy, strategyCalculator);
    }

    private static <T extends State> int getEstimatedStatusId(final Environment<T> environment) {
        float[] scoreEstimates = environment.getScoreEstimates();
        EntryOptimizer<Float, List<Integer>> winningParticipantOptimizer = new EntryOptimizer<>(FLOAT_COMPARATOR);

        for (int i = 0; i < scoreEstimates.length; i++) {
            if (winningParticipantOptimizer.computeIfMoreOptimum(scoreEstimates[i], ArrayList::new)) {
                winningParticipantOptimizer.getValue().add(i);
            }
        }

        List<Integer> winningParticipants = winningParticipantOptimizer.getValue();

        if (winningParticipants.size() > 1) {
            return DRAWN;
        }

        return winningParticipants.get(0);
    }

    private SimulationResult<T> simulateNodeRollout(final Node<T> node, final int simulations) {
        Node<T> currentNode = node;
        int currentStatusId;

        for (int depth = 0; true; ) {
            Node<T> childNode = simulationRolloutPolicy.next(currentNode, simulations);

            if (childNode == null) {
                int statusId = getEstimatedStatusId(currentNode.getEnvironment());

                return simulationResultFactory.create(node, currentNode, statusId);
            }

            currentNode = childNode;
            currentStatusId = currentNode.getEnvironment().getStatusId();

            if (currentStatusId != IN_PROGRESS) {
                return simulationResultFactory.create(node, currentNode, currentStatusId);
            }

            if (!simulationPolicy.allowDepth(simulations, ++depth)) {
                int statusId = getEstimatedStatusId(currentNode.getEnvironment());

                return simulationResultFactory.create(node, currentNode, statusId);
            }
        }
    }

    private Node<T> findBestNode(final Environment<T> environment) {
        if (environment.getStatusId() != IN_PROGRESS) {
            return null;
        }

        Node<T> currentNode = environment.getCurrentNode();
        int simulations = currentNode.getVisited();
        int aborted = 0;
        Node<T> promisingNode = explorationPolicy.next(currentNode, simulations);

        if (promisingNode == null) {
            return null;
        }

        for (boolean allowed = true; allowed; ) {
            int statusId = promisingNode.getEnvironment().getStatusId();
            boolean backPropagated;

            if (statusId == IN_PROGRESS) {
                SimulationResult<T> simulationResult = simulateNodeRollout(promisingNode, simulations);

                backPropagated = backPropagationPolicy.process(simulationResult.getNode(), simulationResult.getStatusId());
            } else {
                backPropagated = backPropagationPolicy.process(promisingNode, statusId);
            }

            allowed = simulationPolicy.allowSimulation(++simulations, backPropagated ? ++aborted : aborted);

            if (allowed) {
                promisingNode = explorationPolicy.next(currentNode, simulations);
            }
        }

        EntryOptimizer<Float, Node<T>> childNodeOptimizer = new EntryOptimizer<>(FLOAT_COMPARATOR);

        for (Node<T> childNode : currentNode.getExploredChildren()) {
            float efficiency = strategyCalculator.calculateEfficiency(childNode);

            childNodeOptimizer.collectIfMoreOptimum(efficiency, childNode);
        }

        return childNodeOptimizer.getValue();
    }

    public Environment<T> findNext(final Environment<T> environment) {
        simulationPolicy.beginSearch();

        return Optional.ofNullable(findBestNode(environment))
                .map(Node::getEnvironment)
                .orElse(null);
    }
}
