package com.dipasquale.search.mcts.alphazero;

import com.dipasquale.common.LimitSupport;
import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.BackPropagationStep;
import com.dipasquale.search.mcts.MonteCarloTreeSearch;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.State;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class AlphaZeroBackPropagationStep<TAction extends Action, TState extends State<TAction, TState>> implements BackPropagationStep<TAction, AlphaZeroEdge, TState, AlphaZeroBackPropagationStep.Context> {
    private static final int FIRST_PARTICIPANT_ID = 1;
    private final BackPropagationType type;

    private static float getProbableReward(final SearchNode<?, AlphaZeroEdge, ?> node) {
        int statusId = node.getState().getStatusId();

        if (statusId == node.getAction().getParticipantId()) {
            return 1f;
        }

        return switch (statusId) {
            case MonteCarloTreeSearch.IN_PROGRESS_STATUS_ID -> node.getEdge().getProbableReward();

            case MonteCarloTreeSearch.DRAWN_STATUS_ID -> 0f;

            default -> -1f;
        };
    }

    @Override
    public Context createContext(final SearchNode<TAction, AlphaZeroEdge, TState> leafNode) {
        float probableReward = getProbableReward(leafNode);
        int leafDepth = leafNode.getState().getDepth();
        int leafParticipantId = leafNode.getAction().getParticipantId();

        return new Context(probableReward, leafDepth, leafParticipantId);
    }

    private static void setExpectedReward(final AlphaZeroEdge edge, final float probableReward) {
        float expectedReward = LimitSupport.getFiniteValue(edge.getExpectedReward() + probableReward);

        edge.setExpectedReward(expectedReward);
    }

    @Override
    public void process(final Context context, final SearchNode<TAction, AlphaZeroEdge, TState> currentNode) {
        int currentParticipantId = currentNode.getAction().getParticipantId();
        AlphaZeroEdge currentEdge = currentNode.getEdge();

        currentEdge.increaseVisited();

        switch (type) {
            case IDENTITY -> setExpectedReward(currentEdge, context.probableReward);

            case REVERSED_ON_BACKTRACK -> {
                if ((context.leafDepth - currentNode.getState().getDepth()) % 2 != 0) {
                    setExpectedReward(currentEdge, context.probableReward);
                } else {
                    setExpectedReward(currentEdge, -context.probableReward);
                }
            }

            case REVERSED_ON_OPPONENT -> {
                if (currentParticipantId == context.leafParticipantId || currentParticipantId == MonteCarloTreeSearch.INITIAL_PARTICIPANT_ID && FIRST_PARTICIPANT_ID == context.leafParticipantId) { // NOTE: the OR check is inconsequential, consider removing
                    setExpectedReward(currentEdge, context.probableReward);
                } else {
                    setExpectedReward(currentEdge, -context.probableReward);
                }
            }
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Context {
        private final float probableReward;
        private final int leafDepth;
        private final int leafParticipantId;
    }
}
