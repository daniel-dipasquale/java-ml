package com.dipasquale.search.mcts;

import com.dipasquale.common.random.float1.RandomSupport;
import com.dipasquale.common.random.float1.UniformRandomSupport;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class MonteCarloTreeSearch<T> {
    private static final int IN_PROGRESS = 0;
    private static final int DRAWN = -1;
    private Environment<T> environment;
    private final SimulationPredicate simulationPredicate;
    private final RandomSupport randomSupport;
    private final ExplorationPolicy<T> explorationPolicy;

    private static <T> HighestUctExplorationPolicy<T> createHighestUctExplorationPolicy(final UctCalculator<T> uctCalculator) {
        UctCalculator<T> uctCalculatorFixed = Objects.requireNonNullElseGet(uctCalculator, DefaultUctCalculator::new);

        return new HighestUctExplorationPolicy<>(uctCalculatorFixed);
    }

    @Builder
    public static <T> MonteCarloTreeSearch<T> create(final Environment<T> environment, final SimulationPredicate simulationPredicate, final UctCalculator<T> uctCalculator) {
        RandomSupport randomSupport = new UniformRandomSupport();
        List<ExplorationPolicy<T>> explorationPolicies = new ArrayList<>();

        explorationPolicies.add(new UnexploredChildrenFirstExplorationPolicy<>());
        explorationPolicies.add(createHighestUctExplorationPolicy(uctCalculator));

        ExplorationPolicy<T> explorationPolicy = new MultiExplorationPolicy<>(explorationPolicies);

        return new MonteCarloTreeSearch<>(environment, simulationPredicate, randomSupport, explorationPolicy);
    }

    private static <T> void initializeEnvironment(final Node<T> parentNode, final Node<T> childNode) {
        Environment<T> childEnvironment = parentNode.getEnvironment().accept(childNode);

        childNode.setEnvironment(childEnvironment);
    }

    private Node<T> selectPromisingNode(final Node<T> node, final int simulations) {
        if (node.getEnvironment().getStatusId() == 0 && node.getExploredChildren() == null) {
            List<Node<T>> childNodes = createAllPossibleNodes(node.getEnvironment());

            randomSupport.shuffle(childNodes);
            node.expandChildren(childNodes);
        }

        Node<T> childNode = explorationPolicy.next(node, simulations);

        if (childNode == null) {
            return null;
        }

        initializeEnvironment(node, childNode);

        return childNode;
    }

    private static <T> List<Node<T>> createAllPossibleNodes(final Environment<T> environment) {
        Node<T> parentNode = environment.getCurrentNode();
        int participantId = environment.getNextParticipantId();
        Iterable<T> possibleStates = environment.createAllPossibleStates();

        return StreamSupport.stream(possibleStates.spliterator(), false)
                .map(s -> new Node<>(parentNode, participantId, s))
                .collect(Collectors.toList());
    }

    private Node<T> selectRandomChildNode(final Node<T> node) {
        List<Node<T>> childNodes = createAllPossibleNodes(node.getEnvironment());
        int size = childNodes.size();

        if (size == 0) {
            return null;
        }

        int index = randomSupport.next(0, size);

        return childNodes.get(index);
    }

    private static <T> int getEstimatedStatusId(final Environment<T> environment) {
        float[] scores = environment.getScoreEstimates();
        NavigableMap<Float, List<Integer>> participantScores = new TreeMap<>(Float::compare);

        for (int i = 0; i < scores.length; i++) {
            participantScores.computeIfAbsent(scores[i], k -> new ArrayList<>()).add(i);
        }

        List<Integer> winningParticipants = participantScores.lastEntry().getValue();

        if (winningParticipants.size() > 1) {
            return -1;
        }

        return winningParticipants.get(0);
    }

    private int simulateNodeRollout(final Node<T> node, final int simulation) {
        Node<T> currentNode = node;

        for (int depth = 0; true; depth++) {
            if (!simulationPredicate.allowDepth(simulation, depth)) {
                return getEstimatedStatusId(currentNode.getEnvironment());
            }

            Node<T> childNode = selectRandomChildNode(currentNode);

            if (childNode == null && currentNode.getEnvironment().getStatusId() == IN_PROGRESS) {
                return getEstimatedStatusId(currentNode.getEnvironment());
            }

            if (childNode == null) {
                return currentNode.getEnvironment().getStatusId();
            }

            initializeEnvironment(currentNode, childNode);

            if (childNode.getEnvironment().getStatusId() != IN_PROGRESS) {
                return childNode.getEnvironment().getStatusId();
            }

            currentNode = childNode;
        }
    }

    private void backPropagateNode(final Node<T> node, final int statusId) {
        for (Node<T> currentNode = node; currentNode != null; currentNode = currentNode.getParent()) {
            currentNode.addVisited();

            if (currentNode.getParticipantId() == statusId) {
                currentNode.addWon();
            } else if (statusId == DRAWN) {
                currentNode.addDrawn();
            }
        }
    }

    private Node<T> findBestNode() {
        int simulation = 0;

        while (simulationPredicate.allowSimulation(simulation)) {
            Node<T> promisingNode = selectPromisingNode(environment.getCurrentNode(), simulation);

            if (promisingNode == null) {
                return null;
            }

            if (promisingNode.getEnvironment().getStatusId() == IN_PROGRESS) {
                int statusId = simulateNodeRollout(promisingNode, simulation);

                backPropagateNode(promisingNode, statusId);
            } else {
                int statusId = promisingNode.getEnvironment().getStatusId();

                backPropagateNode(promisingNode, statusId);
            }

            simulation++;
        }

        return selectPromisingNode(environment.getCurrentNode(), simulation);
    }

    public Environment<T> findNext() {
        simulationPredicate.beginSearch();

        Node<T> bestNode = findBestNode();

        if (bestNode == null) {
            return environment = null;
        }

        return environment = bestNode.getEnvironment();
    }
}
