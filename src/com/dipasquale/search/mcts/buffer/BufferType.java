package com.dipasquale.search.mcts.buffer;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.Edge;
import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.initialization.InitializationContext;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum BufferType {
    DISABLED,
    AUTO_CLEAR;

    public <TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> Buffer<TAction, TEdge, TState, TSearchNode> create(final InitializationContext<TAction, TEdge, TState, TSearchNode> initializationContext) {
        return switch (this) {
            case DISABLED -> new DisabledBuffer<>(initializationContext);

            case AUTO_CLEAR -> new AutoClearBuffer<>(initializationContext);
        };
    }
}
