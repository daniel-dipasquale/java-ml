package com.dipasquale.search.mcts;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class ZeroCollectionProvider<TAction extends Action, TEdge extends Edge, TState extends State<TAction, TState>> implements Provider<TAction, TEdge, TState> {
    private static final boolean IS_ALLOWED_TO_COLLECT = false;
    private final EdgeFactory<TEdge> edgeFactory;

    @Override
    public SearchNode<TAction, TEdge, TState> recallOrCreate(final TState state) {
        return SearchNode.createRoot(edgeFactory, state);
    }

    @Override
    public boolean isAllowedToCollect() {
        return IS_ALLOWED_TO_COLLECT;
    }

    @Override
    public void collect(final SearchNode<TAction, TEdge, TState> searchNode) {
    }

    @Override
    public void clear() {
    }
}
