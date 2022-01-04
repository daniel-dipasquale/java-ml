package com.dipasquale.search.mcts.alphazero;

import com.dipasquale.search.mcts.core.BackPropagationPolicy;
import com.dipasquale.search.mcts.core.Environment;
import com.dipasquale.search.mcts.core.MonteCarloTreeSearch;
import com.dipasquale.search.mcts.core.SearchNode;
import com.dipasquale.search.mcts.core.SearchState;

public final class AlphaZeroBackPropagationPolicy<TState extends SearchState, TEnvironment extends Environment<TState, TEnvironment>> implements BackPropagationPolicy<TState, AlphaZeroSearchEdge, TEnvironment> {
    private static <TState extends SearchState, TEnvironment extends Environment<TState, TEnvironment>> float getReward(final SearchNode<TState, AlphaZeroSearchEdge, TEnvironment> node, final int ownerParticipantId, final int simulationStatusId) {
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
    public void process(final SearchNode<TState, AlphaZeroSearchEdge, TEnvironment> rootNode, final SearchNode<TState, AlphaZeroSearchEdge, TEnvironment> leafNode, final int simulationStatusId) {
        boolean isFullyExplored = true;
        int ownerParticipantId = leafNode.getState().getParticipantId();
        float reward = getReward(leafNode, ownerParticipantId, simulationStatusId);

        for (SearchNode<TState, AlphaZeroSearchEdge, TEnvironment> currentNode = leafNode; currentNode != null; ) {
            AlphaZeroSearchEdge currentEdge = currentNode.getEdge();
            int visited = currentEdge.getVisited();

            currentEdge.increaseVisited();

            if (currentNode.getState().getParticipantId() == ownerParticipantId) {
                setExpectedReward(currentEdge, reward, visited);
            } else {
                setExpectedReward(currentEdge, -reward, visited);
            }

            SearchNode<TState, AlphaZeroSearchEdge, TEnvironment> parentNode = currentNode.getParent();

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
