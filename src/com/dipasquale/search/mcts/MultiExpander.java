package com.dipasquale.search.mcts;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public final class MultiExpander<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> implements Expander<TAction, TEdge, TState> {
    private final List<Expander<TAction, TEdge, TState>> expanders;

    @Override
    public void expand(final SearchNode<TAction, TEdge, TState> searchNode) {
        for (Expander<TAction, TEdge, TState> expander : expanders) {
            expander.expand(searchNode);
        }
    }
}
