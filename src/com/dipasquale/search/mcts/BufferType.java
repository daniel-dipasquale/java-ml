package com.dipasquale.search.mcts;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum BufferType {
    DISABLED,
    AUTO_CLEAR;

    public <TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>, TBackPropagationContext> Buffer<TAction, TEdge, TState, TSearchNode> create(final InitializationContext<TAction, TEdge, TState, TSearchNode, TBackPropagationContext> initializationContext) {
        return switch (this) {
            case DISABLED -> new DisabledBuffer<>(initializationContext.getEdgeFactory(), initializationContext.getSearchNodeFactory());

            case AUTO_CLEAR -> new AutoClearBuffer<>(initializationContext.getMapFactory(), initializationContext.getEdgeFactory(), initializationContext.getSearchNodeFactory());
        };
    }
}
