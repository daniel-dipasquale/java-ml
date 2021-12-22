package com.dipasquale.search.mcts;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class DefaultBackPropagationPolicy<T extends State> implements BackPropagationPolicy<T> {
    @Override
    public boolean process(final Node<T> node, final int statusId) {
        for (Node<T> currentNode = node; currentNode != null; currentNode = currentNode.getParent()) {
            currentNode.increaseVisited();

            if (currentNode.getState().getParticipantId() == statusId) {
                currentNode.increaseWon();
            } else if (statusId == MonteCarloTreeSearch.DRAWN) {
                currentNode.increaseDrawn();
            }
        }

        return true;
    }
}
