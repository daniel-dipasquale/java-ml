package com.dipasquale.search.mcts.alphazero.selection;

import com.dipasquale.search.mcts.EdgeFactory;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.SearchNodeGroupProvider;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.alphazero.AlphaZeroEdge;

public interface AlphaZeroModel<TAction, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, AlphaZeroEdge, TState, TSearchNode>> {
    boolean isEveryStateIntentional();

    AlphaZeroPrediction<TAction, TState, TSearchNode> predict(TSearchNode searchNode, EdgeFactory<AlphaZeroEdge> edgeFactory, SearchNodeGroupProvider<TAction, AlphaZeroEdge, TState, TSearchNode> searchNodeGroupProvider);

    void reset();
}
