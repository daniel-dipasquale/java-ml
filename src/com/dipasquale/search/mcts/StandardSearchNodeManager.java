package com.dipasquale.search.mcts;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class StandardSearchNodeManager<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> extends AbstractSearchNodeManager<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> {
    private static final StandardSearchNodeManager<?, ?, ?> INSTANCE = new StandardSearchNodeManager<>();

    public static <TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> StandardSearchNodeManager<TAction, TEdge, TState> getInstance() {
        return (StandardSearchNodeManager<TAction, TEdge, TState>) INSTANCE;
    }
}
