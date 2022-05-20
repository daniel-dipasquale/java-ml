package com.dipasquale.search.mcts.alphazero;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.SearchNodeGroup;
import com.dipasquale.search.mcts.State;

public interface AlphaZeroPrediction<TAction extends Action, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, AlphaZeroEdge, TState, TSearchNode>> {
    float getValue();

    SearchNodeGroup<TAction, AlphaZeroEdge, TState, TSearchNode> getExplorableChildren();

    float[] getPolicies();
}
