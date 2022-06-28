package com.dipasquale.search.mcts.buffer;

import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.SearchResult;
import com.dipasquale.search.mcts.State;

public interface Buffer<TAction, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> {
    TSearchNode provide(SearchResult<TAction, TState> searchResult);

    void put(TSearchNode searchNode);

    void clear();
}
