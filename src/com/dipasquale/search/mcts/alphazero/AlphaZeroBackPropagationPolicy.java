package com.dipasquale.search.mcts.alphazero;

import com.dipasquale.search.mcts.AbstractBackPropagationPolicy;
import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.LeafNodeObserver;
import com.dipasquale.search.mcts.MonteCarloTreeSearch;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.State;

public final class AlphaZeroBackPropagationPolicy<TAction extends Action, TState extends State<TAction, TState>> extends AbstractBackPropagationPolicy<TAction, AlphaZeroEdge, TState> {
    private static final ProbableRewardController PROBABLE_REWARD_CONTROLLER = ProbableRewardController.getInstance();

    public AlphaZeroBackPropagationPolicy(final LeafNodeObserver<TAction, AlphaZeroEdge, TState> leafNodeObserver) {
        super(leafNodeObserver);
    }

    private static void setExpectedReward(final AlphaZeroEdge edge, final float probableReward, final int visited) {
        float visitedFixed = (float) visited;
        float expectedReward = (visitedFixed - 1f) * edge.getExpectedReward() + probableReward / visitedFixed;

        edge.setExpectedReward(expectedReward);
    }

    @Override
    protected void process(final SearchNode<TAction, AlphaZeroEdge, TState> leafNode, final int simulationStatusId, final SearchNode<TAction, AlphaZeroEdge, TState> currentNode) {
        float probableReward = PROBABLE_REWARD_CONTROLLER.getProbableReward(leafNode, simulationStatusId);
        AlphaZeroEdge currentEdge = currentNode.getEdge();
        int currentParticipantId = currentNode.getAction().getParticipantId();

        currentEdge.increaseVisited();

        if (currentParticipantId == MonteCarloTreeSearch.INITIAL_PARTICIPANT_ID || currentParticipantId == leafNode.getAction().getParticipantId()) {
            setExpectedReward(currentEdge, probableReward, currentEdge.getVisited());
        } else {
            setExpectedReward(currentEdge, -probableReward, currentEdge.getVisited());
        }
    }
}
