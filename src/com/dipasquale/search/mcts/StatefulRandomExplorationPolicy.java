package com.dipasquale.search.mcts;

import com.dipasquale.common.random.float1.RandomSupport;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class StatefulRandomExplorationPolicy<T extends State> implements ExplorationPolicy<T> {
    private final RandomSupport randomSupport;

    @Override
    public Node<T> next(final Node<T> node, final int simulations) {
        List<Node<T>> exploredChildNodes = node.getExploredChildren();
        int exploredSize = exploredChildNodes.size();
        List<Node<T>> unexploredChildNodes = node.getUnexploredChildren();
        int unexploredSize = unexploredChildNodes.size();
        float totalSize = (float) (exploredSize + unexploredSize);

        if (randomSupport.isLessThan((float) exploredSize / totalSize)) {
            int index = randomSupport.next(0, exploredSize);

            Node<T> childNode = exploredChildNodes.get(index);

            if (childNode.getEnvironment() == null) {
                childNode.initializeEnvironment();
            }

            return childNode;
        }

        Node<T> childNode = unexploredChildNodes.remove(unexploredSize - 1);

        exploredChildNodes.add(childNode);
        childNode.initializeEnvironment();

        return childNode;
    }
}
