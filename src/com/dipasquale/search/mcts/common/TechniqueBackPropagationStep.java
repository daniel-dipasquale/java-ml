package com.dipasquale.search.mcts.common;

import com.dipasquale.common.LimitSupport;
import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.BackPropagationStep;
import com.dipasquale.search.mcts.MonteCarloTreeSearch;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.alphazero.BackPropagationType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class TechniqueBackPropagationStep<TAction extends Action, TEdge extends TechniqueEdge, TState extends State<TAction, TState>> implements BackPropagationStep<TAction, TEdge, TState, TechniqueBackPropagationStep.Context> {
    private final BackPropagationType type;

    private static <TEdge extends TechniqueEdge> float getProbableReward(final SearchNode<?, TEdge, ?> node) {
        int statusId = node.getState().getStatusId();

        if (statusId == node.getState().getParticipantId()) {
            return 1f;
        }

        return switch (statusId) {
            case MonteCarloTreeSearch.IN_PROGRESS_STATUS_ID -> node.getEdge().getProbableReward();

            case MonteCarloTreeSearch.DRAWN_STATUS_ID -> 0f;

            default -> -1f;
        };
    }

    @Override
    public Context createContext(final SearchNode<TAction, TEdge, TState> leafSearchNode) {
        float probableReward = getProbableReward(leafSearchNode);
        TState state = leafSearchNode.getState();
        int leafDepth = state.getDepth();
        int leafParticipantId = state.getParticipantId();

        return new Context(probableReward, leafDepth, leafParticipantId);
    }

    private static <TEdge extends TechniqueEdge> void setExpectedReward(final TEdge edge, final float probableReward) {
        float expectedReward = LimitSupport.getFiniteValue(edge.getExpectedReward() + probableReward);

        edge.setExpectedReward(expectedReward);
    }

    @Override
    public void process(final Context context, final SearchNode<TAction, TEdge, TState> currentSearchNode) {
        TState state = currentSearchNode.getState();
        int participantId = state.getParticipantId();
        TEdge currentEdge = currentSearchNode.getEdge();

        currentEdge.increaseVisited();

        switch (type) {
            case IDENTITY -> setExpectedReward(currentEdge, context.probableReward);

            case REVERSED_ON_BACKTRACK -> {
                if ((context.leafDepth - state.getDepth()) % 2 != 0) {
                    setExpectedReward(currentEdge, context.probableReward);
                } else {
                    setExpectedReward(currentEdge, -context.probableReward);
                }
            }

            case REVERSED_ON_OPPONENT -> {
                if (participantId == context.leafParticipantId) {
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
