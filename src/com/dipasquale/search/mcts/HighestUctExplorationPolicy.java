package com.dipasquale.search.mcts;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class HighestUctExplorationPolicy<T> implements ExplorationPolicy<T> {
    private final UctCalculator<T> uctCalculator;

    @Override
    public Node<T> next(final Node<T> node, final int simulations) {
        List<Node<T>> childNodes = node.getExploredChildren();
        int size = childNodes.size();

        if (size == 0) {
            return null;
        }

        float highestUct = -Float.MAX_VALUE;
        int index = -1;

        for (int i = 0; i < size; i++) {
            Node<T> childNode = childNodes.get(i);
            float uct = uctCalculator.calculate(simulations, childNode);

            if (Float.compare(uct, highestUct) > 0) {
                highestUct = uct;
                index = i;
            }
        }

        return childNodes.get(index);
    }
}
