package com.dipasquale.search.mcts.alphazero;

import com.dipasquale.search.mcts.core.Environment;
import com.dipasquale.search.mcts.core.SearchNode;
import com.dipasquale.search.mcts.core.SearchState;

@FunctionalInterface
public interface AlphaZeroHeuristic<TState extends SearchState, TEnvironment extends Environment<TState, TEnvironment>> {
    AlphaZeroPrediction predict(SearchNode<TState, AlphaZeroSearchEdge, TEnvironment> node, int childrenCount);
}
