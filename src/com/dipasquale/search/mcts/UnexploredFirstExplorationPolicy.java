package com.dipasquale.search.mcts;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class UnexploredFirstExplorationPolicy<T extends State> implements ExplorationPolicy<T> {
    @Override
    public Node<T> next(final Node<T> node, final int simulations) {
        List<Node<T>> childNodes = node.getUnexploredChildren();
        int size = childNodes.size();

        if (size == 0) {
            return null;
        }

        Node<T> childNode = childNodes.remove(size - 1);

        node.getExploredChildren().add(childNode);

        return childNode;
    }
}
