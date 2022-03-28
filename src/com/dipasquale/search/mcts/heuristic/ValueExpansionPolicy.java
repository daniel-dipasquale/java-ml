package com.dipasquale.search.mcts.heuristic;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.ExpansionPolicy;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.common.ValueHeuristic;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class ValueExpansionPolicy<TAction extends Action, TState extends State<TAction, TState>> implements ExpansionPolicy<TAction, HeuristicEdge, TState> {
    private final ValueHeuristic<TAction, TState> valueHeuristic;

    @Override
    public void expand(final SearchNode<TAction, HeuristicEdge, TState> searchNode) {
        float probableReward = valueHeuristic.estimate(searchNode.getState());

        searchNode.getEdge().setProbableReward(probableReward);
    }
}
