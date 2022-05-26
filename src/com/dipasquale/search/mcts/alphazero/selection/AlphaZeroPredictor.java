package com.dipasquale.search.mcts.alphazero.selection;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.alphazero.AlphaZeroEdge;

@FunctionalInterface
public interface AlphaZeroPredictor<TAction extends Action, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, AlphaZeroEdge, TState, TSearchNode>> {
    float[] predict(TSearchNode searchNode);
}
