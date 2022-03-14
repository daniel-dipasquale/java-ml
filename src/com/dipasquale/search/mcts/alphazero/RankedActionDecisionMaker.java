package com.dipasquale.search.mcts.alphazero;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.State;

import java.util.List;

@FunctionalInterface
interface RankedActionDecisionMaker<TAction extends Action, TState extends State<TAction, TState>> {
    SearchNode<TAction, AlphaZeroEdge, TState> decide(int simulations, int depth, List<RankedAction<TAction, TState>> rankedActions);
}
