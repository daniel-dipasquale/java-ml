package com.dipasquale.search.mcts;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class UnexploredChildrenFirstExplorationPolicy<T> implements ExplorationPolicy<T> {
    @Override
    public Node<T> next(final Node<T> node, final int simulations) {
        List<Node<T>> unexploredChildren = node.getUnexploredChildren();
        int size = unexploredChildren.size();

        if (size == 0) {
            return null;
        }

        Node<T> childNode = unexploredChildren.remove(size - 1);

        node.getExploredChildren().add(childNode);

        return childNode;
    }
}
