package com.dipasquale.search.mcts.alphazero;

import com.dipasquale.search.mcts.core.SearchNode;
import com.dipasquale.search.mcts.core.SearchState;

@FunctionalInterface
public interface AlphaZeroHeuristic<T extends SearchState> {
    AlphaZeroPrediction predict(SearchNode<T, AlphaZeroSearchEdge> node, int childrenCount);
}
