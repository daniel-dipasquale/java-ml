package com.dipasquale.search.mcts.alphazero;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.EdgeFactory;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.State;

public interface AlphaZeroModel<TAction extends Action, TState extends State<TAction, TState>> {
    boolean isEveryOutcomeDeterministic();

    AlphaZeroPrediction<TAction, TState> predict(SearchNode<TAction, AlphaZeroEdge, TState> node, EdgeFactory<AlphaZeroEdge> edgeFactory);

    void reset();
}
