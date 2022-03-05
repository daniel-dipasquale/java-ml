package com.dipasquale.search.mcts.alphazero;

import com.dipasquale.search.mcts.MonteCarloTreeSearch;
import com.dipasquale.search.mcts.SearchNode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ProbableRewardController {
    private static final ProbableRewardController INSTANCE = new ProbableRewardController();

    public static ProbableRewardController getInstance() {
        return INSTANCE;
    }

    public float getProbableReward(final SearchNode<?, AlphaZeroEdge, ?> node, final int simulationStatusId) {
        int ownerParticipantId = node.getAction().getParticipantId();
        float probableReward = node.getEdge().getProbableReward();

        if (ownerParticipantId == simulationStatusId) {
            return 1f + probableReward;
        }

        if (simulationStatusId == MonteCarloTreeSearch.DRAWN_STATUS_ID || simulationStatusId == MonteCarloTreeSearch.IN_PROGRESS_STATUS_ID) {
            return probableReward;
        }

        return -1f + probableReward;
    }
}
