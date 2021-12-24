package com.dipasquale.search.mcts;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class DefaultBackPropagationPolicy<T extends State> implements BackPropagationPolicy<T> {
    @Override
    public boolean process(final SearchNode<T> searchNode, final int statusId) {
        for (SearchNode<T> currentSearchNode = searchNode; currentSearchNode != null; currentSearchNode = currentSearchNode.getParent()) {
            currentSearchNode.increaseVisited();

            if (currentSearchNode.getState().getParticipantId() == statusId) {
                currentSearchNode.increaseWon();
            } else if (statusId == MonteCarloTreeSearch.DRAWN) {
                currentSearchNode.increaseDrawn();
            }
        }

        return true;
    }
}
