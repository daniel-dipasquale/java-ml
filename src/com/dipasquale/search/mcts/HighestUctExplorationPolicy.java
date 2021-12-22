package com.dipasquale.search.mcts;

import com.dipasquale.common.EntryOptimizer;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class HighestUctExplorationPolicy<T extends State> implements ExplorationPolicy<T> {
    private static final Comparator<Float> FLOAT_COMPARATOR = Float::compare;
    private final UctCalculator<T> uctCalculator;

    @Override
    public Node<T> next(final Node<T> node, final int simulations) {
        List<Node<T>> childNodes = node.getExploredChildren();

        if (childNodes.isEmpty()) {
            return null;
        }

        EntryOptimizer<Float, Node<T>> childNodeOptimizer = new EntryOptimizer<>(FLOAT_COMPARATOR);

        for (Node<T> childNode : childNodes) {
            float uct = uctCalculator.calculate(simulations, childNode);

            childNodeOptimizer.collectIfMoreOptimum(uct, childNode);
        }

        Node<T> childNode = childNodeOptimizer.getValue();

        if (childNode.getEnvironment() == null) {
            childNode.initializeEnvironment();
        }

        return childNode;
    }
}
