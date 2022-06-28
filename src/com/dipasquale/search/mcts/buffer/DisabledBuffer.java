package com.dipasquale.search.mcts.buffer;

import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.SearchNodeFactory;
import com.dipasquale.search.mcts.SearchResult;
import com.dipasquale.search.mcts.State;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class DisabledBuffer<TAction, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> implements Buffer<TAction, TEdge, TState, TSearchNode> {
    private final SearchNodeFactory<TAction, TEdge, TState, TSearchNode> searchNodeFactory;

    @Override
    public TSearchNode provide(final SearchResult<TAction, TState> searchResult) {
        return searchNodeFactory.createRoot(searchResult);
    }

    @Override
    public void put(final TSearchNode searchNode) {
    }

    @Override
    public void clear() {
    }
}
