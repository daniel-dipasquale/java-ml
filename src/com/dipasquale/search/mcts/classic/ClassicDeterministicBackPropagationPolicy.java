package com.dipasquale.search.mcts.classic;

import com.dipasquale.search.mcts.core.BackPropagationPolicy;
import com.dipasquale.search.mcts.core.MonteCarloTreeSearch;
import com.dipasquale.search.mcts.core.SearchNode;
import com.dipasquale.search.mcts.core.SearchState;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class ClassicDeterministicBackPropagationPolicy<T extends SearchState> implements BackPropagationPolicy<T, ClassicSearchEdge> {
    @Override
    public void process(final SearchNode<T, ClassicSearchEdge> rootNode, final SearchNode<T, ClassicSearchEdge> leafNode, final int simulationStatusId) {
        boolean isFullyExplored = true;

        for (SearchNode<T, ClassicSearchEdge> currentNode = leafNode; currentNode != null; ) {
            currentNode.getEdge().increaseVisited();

            if (currentNode.getState().getParticipantId() == simulationStatusId) {
                currentNode.getEdge().increaseWon();
            } else if (simulationStatusId == MonteCarloTreeSearch.DRAWN) {
                currentNode.getEdge().increaseDrawn();
            }

            SearchNode<T, ClassicSearchEdge> parentNode = currentNode.getParent();

            if (parentNode != null) {
                if (isFullyExplored) {
                    parentNode.getExplorableChildren().remove(parentNode.getChildSelectedIndex());
                    parentNode.getFullyExploredChildren().add(currentNode);
                    isFullyExplored = parentNode.isFullyExplored();
                }

                parentNode.setChildSelectedIndex(-1);
            }

            currentNode = parentNode;
        }
    }
}
