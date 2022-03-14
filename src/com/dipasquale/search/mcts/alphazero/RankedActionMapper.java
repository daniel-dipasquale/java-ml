package com.dipasquale.search.mcts.alphazero;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.State;

@FunctionalInterface
interface RankedActionMapper<TAction extends Action, TState extends State<TAction, TState>> {
    Iterable<RankedAction<TAction, TState>> map(int simulations, int depth, Iterable<SearchNode<TAction, AlphaZeroEdge, TState>> nodes);
}
