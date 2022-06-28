package com.dipasquale.search.mcts.alphazero.proposal;

import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.alphazero.AlphaZeroEdge;

import java.util.List;

@FunctionalInterface
interface RankedActionDecisionMaker<TAction, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, AlphaZeroEdge, TState, TSearchNode>> {
    TSearchNode decide(int simulations, int depth, List<RankedAction<TAction, TState, TSearchNode>> rankedActions);
}
