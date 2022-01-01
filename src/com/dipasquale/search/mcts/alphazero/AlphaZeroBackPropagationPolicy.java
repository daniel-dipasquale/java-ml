package com.dipasquale.search.mcts.alphazero;

import com.dipasquale.search.mcts.core.BackPropagationPolicy;
import com.dipasquale.search.mcts.core.MonteCarloTreeSearch;
import com.dipasquale.search.mcts.core.SearchNode;
import com.dipasquale.search.mcts.core.SearchState;

public final class AlphaZeroBackPropagationPolicy<T extends SearchState> implements BackPropagationPolicy<T, AlphaZeroSearchEdge> {
    private static <T extends SearchState> float getReward(final SearchNode<T, AlphaZeroSearchEdge> node, final int ownerParticipantId, final int simulationStatusId) {
        if (ownerParticipantId == simulationStatusId) {
            return 1f;
        }

        if (simulationStatusId == MonteCarloTreeSearch.DRAWN) {
            return 0f;
        }

        if (simulationStatusId == MonteCarloTreeSearch.IN_PROGRESS) {
            return node.getEdge().getProbableReward();
        }

        return -1f;
    }

    private static void setExpectedReward(final AlphaZeroSearchEdge edge, final float reward, final int visited) {
        float visitedFixed = (float) visited;
        float expectedReward = visitedFixed * edge.getExpectedReward() + reward / (visitedFixed + 1f);

        edge.setExpectedReward(expectedReward);
    }

    @Override
    public void process(final SearchNode<T, AlphaZeroSearchEdge> rootNode, final SearchNode<T, AlphaZeroSearchEdge> leafNode, final int simulationStatusId) {
        boolean isFullyExplored = true;
        int ownerParticipantId = leafNode.getState().getParticipantId();
        float reward = getReward(leafNode, ownerParticipantId, simulationStatusId);

        for (SearchNode<T, AlphaZeroSearchEdge> currentNode = leafNode; currentNode != null; ) {
            AlphaZeroSearchEdge currentEdge = currentNode.getEdge();
            int visited = currentEdge.getVisited();

            currentEdge.increaseVisited();

            if (currentNode.getState().getParticipantId() == ownerParticipantId) {
                setExpectedReward(currentEdge, reward, visited);
            } else {
                setExpectedReward(currentEdge, -reward, visited);
            }

            SearchNode<T, AlphaZeroSearchEdge> parentNode = currentNode.getParent();

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
