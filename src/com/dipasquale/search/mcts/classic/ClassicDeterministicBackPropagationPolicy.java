package com.dipasquale.search.mcts.classic;

import com.dipasquale.search.mcts.core.BackPropagationPolicy;
import com.dipasquale.search.mcts.core.Environment;
import com.dipasquale.search.mcts.core.MonteCarloTreeSearch;
import com.dipasquale.search.mcts.core.SearchNode;
import com.dipasquale.search.mcts.core.SearchState;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class ClassicDeterministicBackPropagationPolicy<TState extends SearchState, TEnvironment extends Environment<TState, TEnvironment>> implements BackPropagationPolicy<TState, ClassicSearchEdge, TEnvironment> {
    @Override
    public void process(final SearchNode<TState, ClassicSearchEdge, TEnvironment> rootNode, final SearchNode<TState, ClassicSearchEdge, TEnvironment> leafNode, final int simulationStatusId) {
        boolean isFullyExplored = true;

        for (SearchNode<TState, ClassicSearchEdge, TEnvironment> currentNode = leafNode; currentNode != null; ) {
            currentNode.getEdge().increaseVisited();

            if (currentNode.getState().getParticipantId() == simulationStatusId) {
                currentNode.getEdge().increaseWon();
            } else if (simulationStatusId == MonteCarloTreeSearch.DRAWN) {
                currentNode.getEdge().increaseDrawn();
            }

            SearchNode<TState, ClassicSearchEdge, TEnvironment> parentNode = currentNode.getParent();

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
