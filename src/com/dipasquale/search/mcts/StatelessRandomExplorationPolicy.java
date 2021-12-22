package com.dipasquale.search.mcts;

import com.dipasquale.common.random.float1.RandomSupport;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class StatelessRandomExplorationPolicy<T extends State> implements ExplorationPolicy<T> {
    private final RandomSupport randomSupport;

    @Override
    public Node<T> next(final Node<T> node, final int simulations) {
        List<Node<T>> childNodes = node.createAllPossibleChildNodes(randomSupport);
        int size = childNodes.size();

        if (size == 0) {
            return null;
        }

        int index = randomSupport.next(0, size);
        Node<T> childNode = childNodes.get(index);

        childNode.initializeEnvironment();

        return childNode;
    }
}
