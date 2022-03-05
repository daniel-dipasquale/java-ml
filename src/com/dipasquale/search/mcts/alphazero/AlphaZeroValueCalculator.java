package com.dipasquale.search.mcts.alphazero;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.State;

@FunctionalInterface
public interface AlphaZeroValueCalculator<TAction extends Action, TState extends State<TAction, TState>> {
    float calculate(SearchNode<TAction, AlphaZeroEdge, TState> node);
}
