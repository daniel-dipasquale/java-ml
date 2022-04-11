package com.dipasquale.search.mcts.alphazero;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.StandardSearchNode;
import com.dipasquale.search.mcts.State;

public interface StandardAlphaZeroPrediction<TAction extends Action, TState extends State<TAction, TState>> extends AlphaZeroPrediction<TAction, TState, StandardSearchNode<TAction, AlphaZeroEdge, TState>> {
}
