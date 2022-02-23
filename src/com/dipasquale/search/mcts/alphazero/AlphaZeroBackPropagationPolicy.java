package com.dipasquale.search.mcts.alphazero;

import com.dipasquale.search.mcts.core.AbstractBackPropagationPolicy;
import com.dipasquale.search.mcts.core.Action;
import com.dipasquale.search.mcts.core.BackPropagationObserver;
import com.dipasquale.search.mcts.core.MonteCarloTreeSearch;
import com.dipasquale.search.mcts.core.SearchNode;
import com.dipasquale.search.mcts.core.State;

public final class AlphaZeroBackPropagationPolicy<TAction extends Action, TState extends State<TAction, TState>> extends AbstractBackPropagationPolicy<TAction, AlphaZeroEdge, TState> {
    public AlphaZeroBackPropagationPolicy(final BackPropagationObserver<TAction, AlphaZeroEdge, TState> observer) {
        super(observer);
    }

    private static <TAction extends Action, TState extends State<TAction, TState>> float getReward(final SearchNode<TAction, AlphaZeroEdge, TState> node, final int ownerParticipantId, final int simulationStatusId) {
        float reward = node.getEdge().getProbableReward();

        if (ownerParticipantId == simulationStatusId) {
            return 1f + reward;
        }

        if (simulationStatusId == MonteCarloTreeSearch.DRAWN || simulationStatusId == MonteCarloTreeSearch.IN_PROGRESS) {
            return reward;
        }

        return -1f + reward;
    }

    private static void setExpectedReward(final AlphaZeroEdge edge, final float reward, final int visited) {
        float visitedFixed = (float) visited;
        float expectedReward = visitedFixed * edge.getExpectedReward() + reward / (visitedFixed + 1f);

        edge.setExpectedReward(expectedReward);
    }

    @Override
    protected void process(final SearchNode<TAction, AlphaZeroEdge, TState> leafNode, final int simulationStatusId, final SearchNode<TAction, AlphaZeroEdge, TState> currentNode) {
        int ownerParticipantId = leafNode.getAction().getParticipantId();
        float reward = getReward(leafNode, ownerParticipantId, simulationStatusId);
        AlphaZeroEdge currentEdge = currentNode.getEdge();
        int visited = currentEdge.getVisited();

        currentEdge.increaseVisited();

        if (currentNode.getAction().getParticipantId() == ownerParticipantId) {
            setExpectedReward(currentEdge, reward, visited);
        } else {
            setExpectedReward(currentEdge, -reward, visited);
        }
    }
}
