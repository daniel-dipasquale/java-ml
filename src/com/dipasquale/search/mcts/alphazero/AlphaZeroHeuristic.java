package com.dipasquale.search.mcts.alphazero;

import com.dipasquale.search.mcts.core.Action;
import com.dipasquale.search.mcts.core.EdgeFactory;
import com.dipasquale.search.mcts.core.SearchNode;
import com.dipasquale.search.mcts.core.State;

@FunctionalInterface
public interface AlphaZeroHeuristic<TAction extends Action, TState extends State<TAction, TState>> {
    AlphaZeroPrediction<TAction, TState> predict(SearchNode<TAction, AlphaZeroEdge, TState> node, EdgeFactory<AlphaZeroEdge> edgeFactory);
}
