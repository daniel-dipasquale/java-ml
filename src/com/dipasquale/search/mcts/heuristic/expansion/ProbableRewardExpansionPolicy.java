package com.dipasquale.search.mcts.heuristic.expansion;

import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.expansion.ExpansionPolicy;
import com.dipasquale.search.mcts.heuristic.HeuristicEdge;
import com.dipasquale.search.mcts.heuristic.selection.RewardHeuristic;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class ProbableRewardExpansionPolicy<TAction, TEdge extends HeuristicEdge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> implements ExpansionPolicy<TAction, TEdge, TState, TSearchNode> {
    private final RewardHeuristic<TAction, TState> rewardHeuristic;

    @Override
    public void expand(final TSearchNode searchNode) {
        float probableReward = rewardHeuristic.estimate(searchNode.getState());

        searchNode.getEdge().setProbableReward(probableReward);
    }
}
