package com.dipasquale.search.mcts.buffer;

import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.SearchNodeFactory;
import com.dipasquale.search.mcts.State;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum BufferType {
    DISABLED,
    AUTO_CLEAR;

    public <TAction, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> Buffer<TAction, TEdge, TState, TSearchNode> create(final SearchNodeFactory<TAction, TEdge, TState, TSearchNode> searchNodeFactory) {
        return switch (this) {
            case DISABLED -> new DisabledBuffer<>(searchNodeFactory);

            case AUTO_CLEAR -> new AutoClearBuffer<>(searchNodeFactory);
        };
    }
}
