package com.dipasquale.search.mcts.heuristic;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.ExpansionPolicy;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.common.RewardHeuristic;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class ProbableRewardExpansionPolicy<TAction extends Action, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, HeuristicEdge, TState, TSearchNode>> implements ExpansionPolicy<TAction, HeuristicEdge, TState, TSearchNode> {
    private final RewardHeuristic<TAction, TState> rewardHeuristic;

    @Override
    public void expand(final TSearchNode searchNode) {
        float probableReward = rewardHeuristic.estimate(searchNode.getState());

        searchNode.getEdge().setProbableReward(probableReward);
    }
}
