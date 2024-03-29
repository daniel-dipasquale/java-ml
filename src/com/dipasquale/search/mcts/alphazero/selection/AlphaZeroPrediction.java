package com.dipasquale.search.mcts.alphazero.selection;

import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.SearchNodeGroup;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.alphazero.AlphaZeroEdge;

public interface AlphaZeroPrediction<TAction, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, AlphaZeroEdge, TState, TSearchNode>> {
    float getValue();

    SearchNodeGroup<TAction, AlphaZeroEdge, TState, TSearchNode> getExplorableChildren();

    float[] getPolicies();
}
