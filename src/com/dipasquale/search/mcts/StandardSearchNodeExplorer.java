package com.dipasquale.search.mcts;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class StandardSearchNodeExplorer<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> extends AbstractSearchNodeExplorer<TAction, TEdge, TState, StandardSearchNode<TAction, TEdge, TState>> {
    private static final StandardSearchNodeExplorer<?, ?, ?> INSTANCE = new StandardSearchNodeExplorer<>();

    public static <TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> StandardSearchNodeExplorer<TAction, TEdge, TState> getInstance() {
        return (StandardSearchNodeExplorer<TAction, TEdge, TState>) INSTANCE;
    }
}
