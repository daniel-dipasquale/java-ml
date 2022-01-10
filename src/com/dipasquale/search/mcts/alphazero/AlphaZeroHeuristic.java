package com.dipasquale.search.mcts.alphazero;

import com.dipasquale.search.mcts.core.Environment;
import com.dipasquale.search.mcts.core.SearchNode;
import com.dipasquale.search.mcts.core.State;

@FunctionalInterface
public interface AlphaZeroHeuristic<TState extends State, TEnvironment extends Environment<TState, TEnvironment>> {
    AlphaZeroPrediction predict(SearchNode<TState, AlphaZeroEdge, TEnvironment> node, int childrenCount);
}
