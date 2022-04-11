package com.dipasquale.search.mcts.alphazero;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.State;

import java.util.List;

@FunctionalInterface
interface RankedActionDecisionMaker<TAction extends Action, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, AlphaZeroEdge, TState, TSearchNode>> {
    TSearchNode decide(int simulations, int depth, List<RankedAction<TAction, TState, TSearchNode>> rankedActions);
}
