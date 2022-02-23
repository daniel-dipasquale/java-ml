package com.dipasquale.search.mcts.alphazero;

import com.dipasquale.search.mcts.core.Action;
import com.dipasquale.search.mcts.core.SearchNode;
import com.dipasquale.search.mcts.core.State;

import java.util.List;

public interface AlphaZeroPrediction<TAction extends Action, TState extends State<TAction, TState>> {
    List<SearchNode<TAction, AlphaZeroEdge, TState>> getNodes();

    float[] getPolicies();

    float getValue();
}
