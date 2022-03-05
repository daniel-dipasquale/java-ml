package com.dipasquale.search.mcts.alphazero;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.State;

@FunctionalInterface
public interface AlphaZeroPolicyDistributor<TAction extends Action, TState extends State<TAction, TState>> {
    float distribute(SearchNode<TAction, AlphaZeroEdge, TState> node, float explorationProbability);
}
