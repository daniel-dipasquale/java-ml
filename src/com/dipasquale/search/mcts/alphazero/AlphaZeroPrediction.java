package com.dipasquale.search.mcts.alphazero;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.State;

import java.util.List;

public interface AlphaZeroPrediction<TAction extends Action, TState extends State<TAction, TState>> {
    float getValue();

    List<SearchNode<TAction, AlphaZeroEdge, TState>> getExplorableChildren();

    float[] getPolicies();
}
