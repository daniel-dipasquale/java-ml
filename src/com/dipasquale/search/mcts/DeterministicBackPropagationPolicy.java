package com.dipasquale.search.mcts;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class DeterministicBackPropagationPolicy<T extends State> implements BackPropagationPolicy<T> {
    @Override
    public void process(final SearchNode<T> rootSearchNode, final SearchNode<T> leafSearchNode, final int statusId) {
        boolean isFullyExplored = true;

        for (SearchNode<T> currentSearchNode = leafSearchNode; currentSearchNode != null; ) {
            currentSearchNode.increaseVisited();

            if (currentSearchNode.getState().getParticipantId() == statusId) {
                currentSearchNode.increaseWon();
            } else if (statusId == MonteCarloTreeSearch.DRAWN) {
                currentSearchNode.increaseDrawn();
            }

            SearchNode<T> parentSearchNode = currentSearchNode.getParent();

            if (parentSearchNode != null) {
                if (isFullyExplored) {
                    parentSearchNode.getExplorableChildren().remove(parentSearchNode.getChildSelectionIndex());
                    parentSearchNode.getFullyExploredChildren().add(currentSearchNode);
                    isFullyExplored = parentSearchNode.isFullyExplored();
                }

                parentSearchNode.setChildSelectionIndex(-1);
            }

            currentSearchNode = parentSearchNode;
        }
    }
}
