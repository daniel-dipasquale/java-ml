package com.dipasquale.search.mcts;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractSearchNodeGroupProvider<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>, TSearchNode extends SearchNode<TAction, TEdge, TState, TSearchNode>> implements SearchNodeGroupProvider<TAction, TEdge, TState, TSearchNode> {
    private final EmptySearchNodeGroup<TAction, TEdge, TState, TSearchNode> EMPTY = EmptySearchNodeGroup.getInstance();

    @Override
    public SearchNodeGroup<TAction, TEdge, TState, TSearchNode> getEmpty() {
        return EMPTY;
    }
}
