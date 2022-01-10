package com.dipasquale.search.mcts.alphazero;

import com.dipasquale.search.mcts.core.AbstractBackPropagationPolicy;
import com.dipasquale.search.mcts.core.BackPropagationObserver;
import com.dipasquale.search.mcts.core.Environment;
import com.dipasquale.search.mcts.core.MonteCarloTreeSearch;
import com.dipasquale.search.mcts.core.SearchNode;
import com.dipasquale.search.mcts.core.State;

public final class AlphaZeroBackPropagationPolicy<TState extends State, TEnvironment extends Environment<TState, TEnvironment>> extends AbstractBackPropagationPolicy<TState, AlphaZeroEdge, TEnvironment> {
    public AlphaZeroBackPropagationPolicy(final BackPropagationObserver<TState, AlphaZeroEdge, TEnvironment> observer) {
        super(observer);
    }

    private static <TState extends State, TEnvironment extends Environment<TState, TEnvironment>> float getReward(final SearchNode<TState, AlphaZeroEdge, TEnvironment> node, final int ownerParticipantId, final int simulationStatusId) {
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

    private static void setExpectedReward(final AlphaZeroEdge edge, final float reward, final int visited) {
        float visitedFixed = (float) visited;
        float expectedReward = visitedFixed * edge.getExpectedReward() + reward / (visitedFixed + 1f);

        edge.setExpectedReward(expectedReward);
    }

    @Override
    protected void process(final SearchNode<TState, AlphaZeroEdge, TEnvironment> leafNode, final int simulationStatusId, final SearchNode<TState, AlphaZeroEdge, TEnvironment> currentNode) {
        int ownerParticipantId = leafNode.getState().getParticipantId();
        float reward = getReward(leafNode, ownerParticipantId, simulationStatusId);
        AlphaZeroEdge currentEdge = currentNode.getEdge();
        int visited = currentEdge.getVisited();

        currentEdge.increaseVisited();

        if (currentNode.getState().getParticipantId() == ownerParticipantId) {
            setExpectedReward(currentEdge, reward, visited);
        } else {
            setExpectedReward(currentEdge, -reward, visited);
        }
    }
}
